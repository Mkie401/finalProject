package com.rehome.main.dto.request;

import lombok.Data;

@Data
public class MissingPublishRequest {
    private String petName;
    private String petGender;
    private String petBreed; //品種
    private String petColor; 
    private String petFeature;  
    private String petEarClip;

    private String petType;
    private String petTypeOther; //詳細

    private String petChip;
    private String petChipNumber; //詳細


    private String croppedPetImage_1;
    private String croppedPetImage_2;
    private String croppedPetImage_3;
    private String croppedPetImage_4;

    private String missingDate;
    private String missingCity;
    private Long missingDistrict;
    private String lostLocation;
    private String lostLocationLat;
    private String lostLocationLng;
    private String missingStory; //經過
    private String missingNotes; //其他補充

    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String contactOther;
}
