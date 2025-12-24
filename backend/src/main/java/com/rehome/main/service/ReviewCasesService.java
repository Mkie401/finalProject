package com.rehome.main.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rehome.main.dto.request.ChangeCaseStatusRequest;
import com.rehome.main.dto.response.ReviewCaseResponse;
import com.rehome.main.entity.Case;
import com.rehome.main.entity.CaseStatus;
import com.rehome.main.repository.CaseRepo;
import com.rehome.main.repository.CaseStatusRepo;

@Service
public class ReviewCasesService {

    @Autowired
    private CaseRepo caseRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CaseStatusRepo caseStatusRepo;

    //改變案號審核狀態
    public void changeCaseStatus(String caseNumber, ChangeCaseStatusRequest request) {
        Case existingCase = caseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new RuntimeException("找不到案件 ID: " + caseNumber));


        CaseStatus status = caseStatusRepo.findById(request.getStatusId())
                .orElseThrow(() -> new RuntimeException("找不到狀態 ID: " + request.getStatusId()));
        
        existingCase.setCaseStatus(status);

        // 審核原因
        if (request.getRejectReason() != null && !request.getRejectReason().isEmpty()) {
            // 附加在原本描述後面
            String originalDesc = existingCase.getDescription() == null ? "" : existingCase.getDescription();
            existingCase.setDescription(originalDesc + "\n[審核備註]: " + request.getRejectReason());
        }

        // 4. 存回資料庫
        caseRepository.save(existingCase);

        emailService.sendReviewResult(existingCase.getMember().getEmail(),
        existingCase.getMember().getName(),
        caseNumber,
        existingCase.getCaseType().getName(), 
        request);



    }

    public List<ReviewCaseResponse> findCasesByStatus(Long status) {
        List<Case> entities = caseRepository.findByCaseStatus_Id(Long.valueOf(status));
        return entities.stream().map(c -> {
        // 轉DTO 
        return new ReviewCaseResponse(
                c.getCaseNumber(),
                c.getCaseStatus().getId(),
                c.getCaseType().getName(),
                c.getCaseDateStart().toString(),
                c.getMember().getEmail(),
                c.getDescription()
        );
}).collect(Collectors.toList());
    }
}
