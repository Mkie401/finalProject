package com.rehome.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.FavoritesAddRequestDTO;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.service.MemberFavoritesService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/members/favorites")
public class MemberFavoritesController {

    @Autowired
    private MemberFavoritesService memberFavoritesService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> putFavorites(@RequestBody FavoritesAddRequestDTO dto) {
        Long memberId = 1L;

        if (memberFavoritesService.putFavorites(dto, memberId)) {
            return ResponseEntity.ok(
                    ApiResponse.success("收藏成功"));
        } else {
            return ResponseEntity.ok(
                    ApiResponse.fail("您已收藏過這個案件"));
        }
    }

    @DeleteMapping("/{caseNumber}")
    public ResponseEntity<ApiResponse<?>> deleteFavorites(@PathVariable String caseNumber) {
        Long memberId = 1L;

        if (memberFavoritesService.deleteFavorites(caseNumber, memberId)) {
            return ResponseEntity.ok(ApiResponse.success("已取消收藏"));
        } else {
            return ResponseEntity.ok(ApiResponse.fail("收藏不存在"));
        }
    }

    @GetMapping("/{type}/list")
    public ResponseEntity<ApiResponse<?>> getAdoptionfavorites(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        if (List.of("adoption", "missing").indexOf(type) == -1) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("沒有此API"));
        }
        Long memberId = 1L;
        Boolean isAdoption = "adoption".equals(type);

        return ResponseEntity.ok(
                ApiResponse.success(memberFavoritesService.getFavoritesList(memberId, isAdoption, page, size)
            ));
    }

}
