package com.rehome.main.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rehome.main.dto.request.MissingPublishRequest;
import com.rehome.main.dto.response.CaseStatusResponse;
import com.rehome.main.dto.response.MissingInfoResponse;
import com.rehome.main.entity.AnimalSpecies;
import com.rehome.main.entity.Case;
import com.rehome.main.entity.CaseStatus;
import com.rehome.main.entity.CaseType;
import com.rehome.main.entity.Contact;
import com.rehome.main.entity.Member;
import com.rehome.main.entity.PetDetail;
import com.rehome.main.entity.PetImage;
import com.rehome.main.entity.PetInfo;
import com.rehome.main.entity.Region;
import com.rehome.main.repository.AnimalSpeciesRepo;
import com.rehome.main.repository.CaseRepo;
import com.rehome.main.repository.CaseStatusRepo;
import com.rehome.main.repository.CaseTypeRepo;
import com.rehome.main.repository.MemberRepository;
import com.rehome.main.repository.RegionRepo;

import jakarta.transaction.Transactional;

@Service
public class MissingPublishService {

    @Autowired
    private CaseRepo caseRepository;
    @Autowired
    private CaseStatusRepo caseStatusRepository;
    @Autowired
    private CaseTypeRepo caseTypeRepository;
    @Autowired
    private AnimalSpeciesRepo animalSpeciesRepository;
    @Autowired
    private RegionRepo regionRepository;
    @Autowired
    private MemberRepository memberRepository; // 如果需要驗證會員是否存在

// 用來處理流水號 (簡單版，若多台伺服器需改用 Redis 或資料庫 Sequence)
    private static final AtomicInteger sequence = new AtomicInteger(1);
    private static String lastTimeKey = "";

    @Transactional
    public Case createMissingCase(MissingPublishRequest request, Long memberId) {
        Case newCase = new Case();

        // 1. 設定基本資料與會員 FK
        Member member = memberRepository.findById(memberId).orElse(null);
        newCase.setMember(member);
        newCase.setCaseDateStart(LocalDateTime.now()); // 自動寫入立案時間

        // 2. 設定案件狀態 (預設: 待審核) 與 類型 (走失)
        // id=1 是 "待審核", id=1 是 "走失"
        CaseStatus initialStatus = caseStatusRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("找不到初始案件狀態"));
        CaseType type = caseTypeRepository.findById(1L) // 1 是走失
                .orElseThrow(() -> new RuntimeException("找不到案件類型"));

        newCase.setCaseStatus(initialStatus);
        newCase.setCaseType(type);

        // 3. 生成案號 (ZS + yyyyMMdd + HHmm + 2碼auto)
        String caseNumber = generateCaseNumber();
        newCase.setCaseNumber(caseNumber);

        // 4. 處理 PetInfo (基本資料)
        PetInfo petInfo = new PetInfo();
        petInfo.setName(request.getPetName());
        petInfo.setGender(request.getPetGender());
        petInfo.setBreed(request.getPetBreed());
        petInfo.setColor(request.getPetColor());
        petInfo.setFeature(request.getPetFeature());
        petInfo.setAnimalSpeciesOther(request.getPetTypeOther());
        // 設定寵物狀態 (例如：1 = 走失中, 2 = 待認養... 請確認你資料庫 pet_status 表的 ID)

        Region region = regionRepository.findById(request.getMissingDistrict())
                .orElseThrow(() -> new RuntimeException("找不到叫做" + request.getMissingDistrict() + " 的地區"));

        petInfo.setRegion(region);
        // ==================================
        String typeStr = request.getPetType(); 

        if (typeStr != null && !typeStr.isEmpty()) {
            try {

                // 2. 查詢資料庫 (findById 回傳的是 Optional<AnimalSpecies>)
                AnimalSpecies species = animalSpeciesRepository.findByName(typeStr)
                        .orElseThrow(() -> new RuntimeException("找不到名為=" + typeStr + " 的動物種類"));

                // 3. 塞入物件
                petInfo.setAnimalSpecies(species);

            } catch (Exception e) {
                // 防止前端傳了 "abc" 這種無法轉成數字的字串
                throw new RuntimeException("動物種類錯誤: " + typeStr);
            }
        } else {
            // 若沒傳 ID 的處理邏輯 (例如設為 null 或預設值)
            petInfo.setAnimalSpecies(null);
        }

        // 處理 Boolean/String 轉換 (假設前端傳 "yes"/"no")
        petInfo.setIsEarTipping("yes".equalsIgnoreCase(request.getPetEarClip()));
        petInfo.setIsChip("yes".equalsIgnoreCase(request.getPetChip()));
        petInfo.setChipNumber(request.getPetChipNumber());

        petInfo.setPetCase(newCase); // 雙向關聯
        newCase.setPetInfo(petInfo);

        // 5. 處理 PetDetail (遺失細節)
        PetDetail petDetail = new PetDetail();
        // 轉換日期
        if (request.getMissingDate() != null) {
            petDetail.setLostDate(LocalDateTime.parse(request.getMissingDate() + "T00:00:00"));
        }
        petDetail.setLostAddr(request.getLostLocation());
        petDetail.setLostProcess(request.getMissingStory());

        petDetail.setRegion(region);

        // 經緯度轉換
        if (request.getLostLocationLat() != null) {
            petDetail.setLat(new BigDecimal(request.getLostLocationLat()));
        }
        if (request.getLostLocationLng() != null) {
            petDetail.setLng(new BigDecimal(request.getLostLocationLng()));
        }

        petDetail.setDescription(request.getMissingNotes());
        petDetail.setPetCase(newCase);
        newCase.setPetDetail(petDetail);

        // 6. 處理 Contact (聯絡人)
        Contact contact = new Contact();
        contact.setName(request.getContactName());
        contact.setTel(request.getContactPhone());
        contact.setMail(request.getContactEmail());
        contact.setOtherContact(request.getContactOther());
        // 其他欄位如 isPhoneDisplay 若前端有傳需 set
        contact.setPetCase(newCase);
        newCase.setContact(contact);

        // 7. 處理 PetImage (圖片 Base64 解碼)
        List<PetImage> images = new ArrayList<>();
        processImage(request.getCroppedPetImage_1(), 1, newCase, images);
        processImage(request.getCroppedPetImage_2(), 2, newCase, images);
        processImage(request.getCroppedPetImage_3(), 3, newCase, images);
        processImage(request.getCroppedPetImage_4(), 4, newCase, images);

        newCase.setPetImage(images);

        // 8. 存檔 (因為 Case 有 CascadeType.ALL，存 Case 會連帶存下面所有表)
        return caseRepository.save(newCase);
    }

    // 案號生成邏輯
    private synchronized String generateCaseNumber() {
        LocalDateTime now = LocalDateTime.now();
        String timePart = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")); // 12碼

        // 檢查是否同一分鐘
        if (!timePart.equals(lastTimeKey)) {
            lastTimeKey = timePart;
            sequence.set(1); // 新的一分鐘，流水號重置
        }

        // 格式化流水號為 2 碼 (01, 02...99)
        int seq = sequence.getAndIncrement();
        String seqStr = String.format("%02d", seq);

        // 組合: ZS (2) + Time (12) + Seq (2) = 16碼
        return "ZS" + timePart + seqStr;
    }

    // 圖片處理輔助方法
    private void processImage(String base64Str, int sortOrder, Case petCase, List<PetImage> images) {
        if (base64Str != null && !base64Str.isEmpty()) {
            try {
                // 去除 Data URI scheme (data:image/jpeg;base64,)
                String cleanBase64 = base64Str;
                if (base64Str.contains(",")) {
                    cleanBase64 = base64Str.split(",")[1];
                }

                byte[] photoBytes = Base64.getDecoder().decode(cleanBase64);

                PetImage image = new PetImage();
                image.setPhoto(photoBytes);
                image.setSortOrder(sortOrder);
                image.setPetCase(petCase);
                images.add(image);
            } catch (IllegalArgumentException e) {
                System.err.println("圖片解碼失敗: " + e.getMessage());
            }
        }
    }

    //查詢案號狀態+原因
    public CaseStatusResponse getCaseStatus(String caseNumber, Long memberId) {
        Case petCase = caseRepository.findByCaseNumberAndMemberId(caseNumber, memberId)
                .orElseThrow(() -> new RuntimeException("找不到此案件編號"));

        CaseStatusResponse response = new CaseStatusResponse();
        response.setCaseNumber(petCase.getCaseNumber());
        response.setStatus(petCase.getCaseStatus().getId());
        response.setRejectReason(petCase.getDescription());
        response.setMemberId(petCase.getMember().getId());

        return response;
    }

    //更改狀態(結案) 要加結案時間
    public void  changeCaseStatus(String caseNumber, Long memberId) {
        Case petCase = caseRepository.findByCaseNumberAndMemberId(caseNumber, memberId)
                .orElseThrow(() -> new RuntimeException("無權限" + caseNumber));

        // 設定狀態為 4 (案件結束)
        CaseStatus closedStatus = caseStatusRepository.findById(4L)
                .orElseThrow(() -> new RuntimeException("找不到狀態 ID: 4"));

        petCase.setCaseStatus(closedStatus);

        // 設定結案時間 (自動加入當前時間)
        petCase.setCaseDateEnd(java.time.LocalDateTime.now());

        caseRepository.save(petCase);
    }

    //抓單一會員所有上傳走失案件+輕量dto
    public List<MissingInfoResponse> getMissingCaseInfo(Long memberId) {
        // 1. 透過 Repo 找出該會員的所有案件
        List<Case> caseList = caseRepository.findByMemberIdAndCaseTypeId(memberId, 1L);

        // 2. 將 Entity (Case) 轉換成 DTO (MissingInfoResponse)
        // 這裡使用 Stream API 來轉換，也可以用 for 迴圈
        return caseList.stream().map(petCase -> {
            MissingInfoResponse dto = new MissingInfoResponse();

            // 依照您的 DTO 欄位 進行塞值
            dto.setMemberId(petCase.getMember().getId());
            dto.setPetName(petCase.getPetInfo().getName());
            dto.setPetGender(petCase.getPetInfo().getGender());
            dto.setMissingDate(petCase.getPetDetail().getLostDate() != null ? petCase.getPetDetail().getLostDate().toString() : ""); // 轉字串

            // 注意：您的 DTO 拼寫是 spices，請對應 Entity 的 species 或 type
            dto.setSpecies(
                    "其他".equals(petCase.getPetInfo().getAnimalSpecies().getName()) // 記得加 .getName()
                    ? petCase.getPetInfo().getAnimalSpeciesOther()
                    : petCase.getPetInfo().getAnimalSpecies().getName() // 這裡也要加 .getName()
            );
            dto.setPetBreed(petCase.getPetInfo().getBreed());

            // 地區 (如果 DB 存 ID，這裡直接回傳 ID 字串，前端 JS 會轉中文)
            dto.setMissingDistrict(String.valueOf(petCase.getPetDetail().getLostAddr()));

            // 假設 CaseStatus 是物件，取其名稱或 ID
            dto.setCaseStatus(petCase.getCaseStatus() != null ? petCase.getCaseStatus().getName() : "未知");
            dto.setCaseNumber(petCase.getCaseNumber());

            // 圖片處理：假設 Case 有存圖片 byte[]，或是關聯到圖片表
            // 這裡示範直接取 Entity 內的圖片 (如果有的話)
            List<PetImage> images = petCase.getPetImage();

            if (images != null && !images.isEmpty()) {
                PetImage coverImage = images.stream()
                        .filter(img -> Integer.valueOf(1).equals(img.getSortOrder())) // 找權重為1的
                        .findFirst() // 找到第一筆符合的
                        .orElse(images.get(0)); // 如果沒找到權重1的，就直接拿陣列第0個當封面

                if (coverImage != null && coverImage.getPhoto() != null) {

                    // 1. 轉成 Base64 
                    String base64Str = Base64.getEncoder().encodeToString(coverImage.getPhoto());
                    dto.setPetImg("data:image/jpeg;base64," + base64Str);
                } else {
                    dto.setPetImg(null);
                }
            } else {
                dto.setPetImg(null); // 沒照片就是 null
            }

            return dto;
        }).toList();
    }

}
