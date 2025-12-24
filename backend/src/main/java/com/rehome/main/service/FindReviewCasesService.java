/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.rehome.main.service;

import java.util.Base64;

import org.springframework.stereotype.Service;

import com.rehome.main.dto.response.PetReviewDto;
import com.rehome.main.entity.Case;
import com.rehome.main.entity.PetImage;
import com.rehome.main.entity.PetInfo;
import com.rehome.main.repository.PetCaseRep;
import com.rehome.main.repository.PetInfoRep;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author user
 */
@Service
@RequiredArgsConstructor
public class FindReviewCasesService {

    private final PetCaseRep petCaseRep;
    private final PetInfoRep petInfoRep;


    public PetReviewDto getCaseDetail(Long caseId) {
       
        Case petCase = petCaseRep.findById(caseId)
                .orElseThrow(() -> new RuntimeException("找不到案件資訊 " + caseId));

        PetInfo petInfo = petInfoRep.findFirstByPetCase_Id(caseId)
                .orElseThrow(() -> new RuntimeException("找不到寵物資訊"));

        PetReviewDto dto = new PetReviewDto();

        dto.setCaseNumber(petCase.getCaseNumber());

        if (petCase.getCaseDateStart() != null) {
            dto.setSubmitDate(petCase.getCaseDateStart().toString());
        }
        dto.setPetNickname(petInfo.getName());
        dto.setPetGender(petInfo.getGender());
        dto.setPetBreed(petInfo.getBreed());
        dto.setPetSize(petInfo.getSize());
        dto.setPetAge(petInfo.getAge());
         if (petInfo.getAnimalSpecies() != null) {
             dto.setPetType(petInfo.getAnimalSpecies().getName());
        }

       if (petCase.getPetImage() != null && !petCase.getPetImage().isEmpty()) {
            PetImage mainImage = petCase.getPetImage().get(0);  // sortOrder 已經 ASC 排好了

            if (mainImage.getPhoto() != null && mainImage.getPhoto().length > 0) {
                String base64 = Base64.getEncoder().encodeToString(mainImage.getPhoto());
                dto.setPhotoBase64(base64);
            } else {
                dto.setPhotoBase64(null);
            }
        } else {
            dto.setPhotoBase64(null);
        }

        return dto;
    }


}
