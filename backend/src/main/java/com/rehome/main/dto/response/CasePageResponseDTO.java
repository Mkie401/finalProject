package com.rehome.main.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CasePageResponseDTO {
    private CaseInfoResponseDTO caseInfo;
    private CaseContactResponseDTO caseContact;
    private PetInfoResponseDTO petInfo;
    
    private CaseDetailResponseDTO detail;
}
