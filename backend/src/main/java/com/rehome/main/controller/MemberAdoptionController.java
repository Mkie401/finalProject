package com.rehome.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.MemberAdoptionFormDTO;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.service.MemberAdoptionService;

@RestController
@RequestMapping("/api/members/adoption")
public class MemberAdoptionController {

    @Autowired
    private MemberAdoptionService memberAdoptionService;

    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<?>> postApplications(@RequestBody MemberAdoptionFormDTO dto) {
        Long memberId = 1L;

        return ResponseEntity.ok(
                memberAdoptionService.saveAdoptionForm(dto, memberId));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<?>> getApplications() {
        Long memberId = 1L;

        return ResponseEntity.ok(
                ApiResponse.success(memberAdoptionService.getAdoptionList(memberId)));
    }

    @DeleteMapping("/applications/{id}")
    public void deleteApplications(@PathVariable Long id) {

    }

}
