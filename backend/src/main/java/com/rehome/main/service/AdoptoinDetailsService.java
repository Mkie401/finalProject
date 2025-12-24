/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.rehome.main.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rehome.main.dto.response.AdoptoinDetailsDTO;
import com.rehome.main.entity.Case;
import com.rehome.main.entity.PetImage;
import com.rehome.main.entity.PetInfo;
import com.rehome.main.repository.CaseStatusRepo;
import com.rehome.main.repository.PetCaseRep;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 *會員中心 - 刊登送養詳情
 * @author user
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdoptoinDetailsService {
    
    private final PetCaseRep petCaseRep;
    private final CaseStatusRepo caseStatusRepo;



    //會員中心-刊登送養詳情
    public List<AdoptoinDetailsDTO> getAllAdoptionDetails(Long memberId) {

        Long ADOPTION_TYPE_ID = 2L;
        List<Case> cases =  petCaseRep.findByMember_IdAndCaseType_Id(memberId, ADOPTION_TYPE_ID);

        return cases.stream().map(this::toCaseDto).toList();//語法糖
    }

    private AdoptoinDetailsDTO toCaseDto(Case c) {
        AdoptoinDetailsDTO dto = new AdoptoinDetailsDTO();

        dto.setCaseId(c.getId());
        if (c.getMember() != null) {
            dto.setMemberId(c.getMember().getId());
        }
        dto.setCaseNumber(c.getCaseNumber());


        //寵物資訊
        PetInfo Info = c.getPetInfo();
        if(Info!=null){
            dto.setPetName(Info.getName());
            dto.setPetGender(Info.getGender());
            dto.setPetBreed(Info.getBreed());
            if (Info.getAnimalSpecies() != null) {
                dto.setPetType(Info.getAnimalSpecies().getName());
            }
        }
        //建檔時間
        if (c.getCaseDateStart() != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dto.setSubmitDate(c.getCaseDateStart().format(fmt));
        }

        //主要照片
        String photo = null;
        if (c.getPetImage() != null && !c.getPetImage().isEmpty()) {
            PetImage mainImage = c.getPetImage().get(0);  // sortOrder 已經 ASC 排好了
            if (mainImage.getPhoto() != null && mainImage.getPhoto().length > 0) {
                photo = java.util.Base64.getEncoder().encodeToString(mainImage.getPhoto());
            }
        }
        dto.setPhoto(photo);

        //案件狀態
        if(c.getCaseStatus()!=null){
            dto.setCaseStatusId(c.getCaseStatus().getId());
            dto.setCaseStatusName(c.getCaseStatus().getName());
        }

        

        return dto;
    }

    //會員終止送養案件

     public void terminateAdoptionCase(Long memberId, Long caseId) {
        //找會員案件
        Case petCase = petCaseRep.findByIdAndMember_Id(caseId, memberId)
                .orElseThrow(() -> new RuntimeException("找不到案件資訊 " + caseId));

        // 找狀態
        Long TERMINATED_STATUS_ID = 4L;
        var terminatedStatus = caseStatusRepo.findById(TERMINATED_STATUS_ID)
                .orElseThrow(() -> new RuntimeException("找不到案件狀態 " + TERMINATED_STATUS_ID));

        petCase.setCaseStatus(terminatedStatus);
        petCase.setCaseDateEnd(LocalDateTime.now());
        petCaseRep.save(petCase);
        }



}
