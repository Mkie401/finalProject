package com.rehome.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.LostNotificationFormDTO;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.service.MemberMissingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/members/missing")
public class MemberMissingController {

    @Autowired
    private MemberMissingService memberMissingService;

    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<?>> postApplications(@RequestBody LostNotificationFormDTO dto) {

        return ResponseEntity.ok(
                memberMissingService.saveLostNotification(dto));
    }

    @GetMapping("/applications/{caseNumber}")
    public ResponseEntity<ApiResponse<?>> getApplications(
            @PathVariable String caseNumber,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long memberId = 1L;

        return ResponseEntity.ok(
                ApiResponse.success(
                        memberMissingService.findLostNotification(memberId, caseNumber, page, size)));
    }

}
