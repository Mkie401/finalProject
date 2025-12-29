/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.rehome.main.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.rehome.main.dto.request.PetPhotoDto;
import com.rehome.main.dto.request.PetinfoConDto;
import com.rehome.main.entity.CaseType;
import com.rehome.main.entity.City;
import com.rehome.main.entity.Contact;
import com.rehome.main.entity.Member;
import com.rehome.main.entity.AdoptionPetArea;
import com.rehome.main.entity.AdoptionPetAreaId;
import com.rehome.main.entity.AnimalSpecies;
import com.rehome.main.entity.Case;
import com.rehome.main.entity.CaseStatus;
import com.rehome.main.entity.PetDetail;
import com.rehome.main.entity.PetImage;
import com.rehome.main.entity.PetInfo;
import com.rehome.main.entity.Region;
import com.rehome.main.repository.MemberRepository;
import com.rehome.main.repository.PetAnimalSpeciesRep;
import com.rehome.main.repository.PetCaseRep;
import com.rehome.main.repository.PetCaseStatusRep;
import com.rehome.main.repository.PetCasetypeRep;
import com.rehome.main.repository.PetCityRep;
import com.rehome.main.repository.PetContactRep;
import com.rehome.main.repository.PetDetailRep;
import com.rehome.main.repository.PetInfoRep;
import com.rehome.main.repository.PetRegionsRep;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
/**
 *
 * @author user
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PetIofnServer {
    private final PetAnimalSpeciesRep petAnimalSpeciesRep;
    private final PetCaseRep petCaseRep;
    private final PetCityRep petCityRep;
    private final PetContactRep petContactRep;
    private final PetDetailRep petDetailRep;
    private final PetInfoRep petInfoRep;
    private final PetRegionsRep petRegionsRep; 
    private final PetCaseStatusRep petCaseStatusRep;
    private final PetCasetypeRep petCasetypeRep;
    private final MemberRepository memberRepository;


    public Case create(PetinfoConDto infDto , Long memberId){
        //會員假ID
        Member member = memberRepository.findById(memberId).orElse(null);

        // 建立新的 PetCase
        Case petCase = new Case();
        petCase.setMember(member);
        
        String caseNumber = generateCaseNumber();
        petCase.setCaseNumber(caseNumber);
        petCase.setCaseDateStart(LocalDate.now().atTime(LocalTime.now()));
        
        //狀態 審核
        CaseStatus caseStatus = petCaseStatusRep.findById(1L).orElseThrow();
        //案件類型 送養
        CaseType  caseType = petCasetypeRep.findById(2L).orElseThrow();

        petCase.setCaseStatus(caseStatus);
        petCase.setCaseType(caseType);
        
        // 保存到資料庫
        petCaseRep.save(petCase);


        // 建立 PetInfo
        PetInfo petInfo = new PetInfo();
        petInfo.setPetCase(petCase);
        petInfo.setName(infDto.getPetNickname());
        petInfo.setGender(infDto.getPetGender());
        petInfo.setSize(infDto.getPetSize());
        petInfo.setBreed(infDto.getPetBreed());
        petInfo.setAge(infDto.getPetAge());
        petInfo.setIsEarTipping(Boolean.TRUE.equals(infDto.getPetNeutered()));
        petInfo.setChipNumber(infDto.getPetMicrochipNumber());
        petInfo.setIsChip(Boolean.TRUE.equals(infDto.getPetMicrochip()));
        petInfo.setAnimalSpeciesOther(infDto.getPetTypeOther());
        
        // 設定動物種類（使用 ID）
        AnimalSpecies animalSpecies = petAnimalSpeciesRep.findById(infDto.getPetTypeId()).orElseThrow();
        petInfo.setAnimalSpecies(animalSpecies);
        
        // 設定地區（使用 ID）
        Region regions = petRegionsRep.findById(infDto.getPetDistrictId()).orElseThrow();
        petInfo.setRegion(regions);

        petInfoRep.save(petInfo);

        //領養要求 -補充說明
        PetDetail petDetail = new PetDetail();
        petDetail.setPetCase(petCase);
        
        petDetail.setIsFollowAger("agree".equalsIgnoreCase(infDto.getFollowUp()));
        petDetail.setIsFamilyAger("required".equalsIgnoreCase(infDto.getFamilyConsent()));
        petDetail.setIsAgeLimit("required".equalsIgnoreCase(infDto.getAgeLimit()));
    
        petDetail.setAdoptionRequ(infDto.getAdoptionRequirements());
        petDetail.setMedicalInfo(infDto.getMedicalInfo());
        petDetail.setDescription(infDto.getAdditionalInfo());
    
        petDetailRep.save(petDetail);

        //聯絡人資訊
        Contact petContact = new Contact();
        petContact.setPetCase(petCase);
        petContact.setName(infDto.getContactName());
        petContact.setTel(infDto.getContactPhone());
        petContact.setMail(infDto.getContactEmail()); 
    
        petContact.setIsPhoneDisplay(Boolean.TRUE.equals(infDto.getPhoneDisplay()));
        petContact.setIsEmailDisplay(Boolean.TRUE.equals(infDto.getEmailDisplay()));
        petContactRep.save(petContact);

        // 送養範圍 
        List<Long> adoptCityIds = infDto.getAdoptCityIds();
        if(adoptCityIds != null && !adoptCityIds.isEmpty()){
            Set<Long> uniqueCityIds = new HashSet<>(adoptCityIds);
            if (petCase.getAdoptionPetAreas() == null) {
                petCase.setAdoptionPetAreas(new ArrayList<>());
            }
            for (Long cityId : uniqueCityIds){
                City city = petCityRep.findById(cityId).orElseThrow(() -> new RuntimeException("送養城市不存在: " + cityId));
                //建立中介物件
                AdoptionPetArea adoptionPetArea = new AdoptionPetArea();
                adoptionPetArea.setPetCase(petCase);
                adoptionPetArea.setCity(city);

                // Initialize ID
                AdoptionPetAreaId id = new AdoptionPetAreaId(petCase.getId(), city.getId());
                adoptionPetArea.setId(id);

                // Add to Case
                petCase.getAdoptionPetAreas().add(adoptionPetArea);
            }
            petCaseRep.save(petCase);
        }

        //處理寵物照片
        List<PetPhotoDto> photoDtos = infDto.getPhotos();
        if (photoDtos != null && !photoDtos.isEmpty()) {
            petCase.setPetImage(new ArrayList<PetImage>());

            Integer sort = 1;
            //無效圖
            for(PetPhotoDto photoDto : photoDtos) {
                if(photoDto == null ||!photoDto.isValidImage()) {
                    continue; 
                }
                String base64Data = photoDto.getBase64Data();
                byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
                PetImage image = new PetImage();
                image.setPetCase(petCase);
                image.setPhoto(imageBytes);
                image.setSortOrder(sort++);
                
                petCase.getPetImage().add(image);
                
            }   
            


        }
       

    Case savedCase = petCaseRep.save(petCase);
        //case傳回
    return savedCase;


    }

    
    /**
     * 
     * 格式：SY + YYYYMMDD + HHMM + 01-99 (總共16碼)
     */
    private String generateCaseNumber() {
       
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
       
        String timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"));
        
        // 生成01-99的
        Random random = new Random();
        int sequenceNumber = 1 + random.nextInt(99);
        String sequenceStr = String.format("%02d", sequenceNumber); 
        
        // 組合案件編號：SY + 日期(8碼) + 時分(4碼) + 序號(2碼) = 16碼
        return "SY" + dateStr + timeStr + sequenceStr;
    }



        
}

