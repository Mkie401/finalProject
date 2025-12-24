/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.rehome.main.dto.response;

import lombok.Data;

/**
 *
 * @author user
 */
@Data
public class PetReviewDto {

    // 寵物基本資訊
    private String petNickname;
    private String petGender;
    private String petBreed;
    private String petSize;
    private String petAge;
    private String petType;
   
    //案件編號
    private String caseNumber;
    private String submitDate;

    //照片
    private String photoBase64;
}
