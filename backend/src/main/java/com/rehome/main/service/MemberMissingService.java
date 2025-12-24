package com.rehome.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rehome.main.dto.request.LostNotificationFormDTO;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.PaginationResponseDTO;
import com.rehome.main.entity.Case;
import com.rehome.main.entity.LostNotification;
import com.rehome.main.repository.CaseRepository;
import com.rehome.main.repository.LostNotificationRepository;

@Service
public class MemberMissingService {

    @Autowired
    private LostNotificationRepository lostNotificationRepository;
    @Autowired
    private CaseRepository caseRepository;

    @Transactional
    public ApiResponse<?> saveLostNotification(LostNotificationFormDTO dto) {
        Case petCase = caseRepository.findByCaseNumber(dto.getCaseNumber())
                .orElseThrow(() -> new RuntimeException("Case not found"));
        if (petCase == null) {
            return ApiResponse.fail("寵物資訊不存在");
        }

        LostNotification lostNotification = new LostNotification();
        lostNotification.setPetCase(petCase);
        lostNotification.setMessage(dto.getMessage());

        lostNotificationRepository.save(lostNotification);

        return ApiResponse.success("走失訊息成功刊登");
    }

    @Transactional(readOnly = true)
    public PaginationResponseDTO<LostNotification> findLostNotification(Long memberId, String caseNumber, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : page, size, Sort.by("sendDate").descending());
        Page<LostNotification> result = lostNotificationRepository.findByPetCase_Member_IdAndPetCase_CaseNumber(
            memberId, caseNumber, pageable);

        return PaginationResponseDTO.fromPage(result);
    }
}
