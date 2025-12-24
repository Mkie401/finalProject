/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.rehome.main.dto.response;

import lombok.Data;

/**
 *會員中心-刊登送養詳情DTO
 * @author user
 */
@Data
public class AdoptoinDetailsDTO {
    private Long caseId;

    private Long memberId;
    private String caseNumber;

    private String petName;
    private String petGender;
    private String petBreed;
    private String petType;

    private String submitDate;//建檔時間

    private String photo; //主要照片[0]

    private Long caseStatusId;//案件狀態ID
    private String caseStatusName;//狀態名稱


}
