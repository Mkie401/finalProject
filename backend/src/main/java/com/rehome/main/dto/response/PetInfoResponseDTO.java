package com.rehome.main.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PetInfoResponseDTO {
    private String petName;
    private String species;
    private String breed;
    private String gender;
    private String size;
    private String age;
    private String color;
    private String feature;
    private Boolean isEarTipping;
    private Boolean isChip;
    private String chipNumber;
    private String region;          // 送養領養用
}
