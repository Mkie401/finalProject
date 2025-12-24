package com.rehome.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.CaseFormRequestDTO;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.service.CaseService;

@RestController
@RequestMapping("/api/cases")
public class CaseController {
    @Autowired
    private CaseService caseService;

    @GetMapping("{type}/home")
    public ResponseEntity<ApiResponse<?>> getHome(@PathVariable String type) {
        if (!"adoption".equals(type) && !"missing".equals(type)) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("Invalid type. Only 'adoption' and 'missing' are allowed."));
        }

        Long memberId = 1L;
        Boolean isAdoption = "adoption".equals(type);

        return ResponseEntity.ok(
                ApiResponse.success(caseService.getHomeInfo(memberId, isAdoption)));
    }

    @GetMapping("/options")
    public ResponseEntity<ApiResponse<?>> getOptions() {

        return ResponseEntity.ok(
                ApiResponse.success(caseService.getOptions()));
    }

    @PostMapping("{type}/search")
    public ResponseEntity<ApiResponse<?>> postSearch(@RequestBody CaseFormRequestDTO caseFormRequestDTO, @PathVariable String type) {
        if (!"adoption".equals(type) && !"missing".equals(type)) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("Invalid type. Only 'adoption' and 'missing' are allowed."));
        }

        Long memberId = 1L;
        Boolean isAdoption = "adoption".equals(type);

        return ResponseEntity.ok(
                ApiResponse.success(caseService.getSearchCardList(caseFormRequestDTO, memberId, isAdoption)));
    }

    @GetMapping("/{caseNumber}")
    public ResponseEntity<ApiResponse<?>> getDetail(@PathVariable String caseNumber) {
        Long memberId = 1L;

        return ResponseEntity.ok(
                ApiResponse.success(caseService.getCasePage(caseNumber, memberId)));
    }
}
