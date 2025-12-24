package com.rehome.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.entity.Case;
import com.rehome.main.repository.CaseRepository;

@RestController
@RequestMapping("/api")
public class TestController {
    @Autowired
    private CaseRepository caseRepository;

    @GetMapping("/test/{id}")
    public ResponseEntity<ApiResponse<?>> getMethodName(@PathVariable Long id) {
        int page = 1;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size); // 可以不用在這裡指定排序，因為方法已寫 OrderBy
        Page<Case> pageData = caseRepository.findByCaseTypeIdAndCaseDateEndIsNullOrderByCaseDateStartDesc(id, pageable);
        return ResponseEntity.ok(ApiResponse.success(pageData));
    }
}
