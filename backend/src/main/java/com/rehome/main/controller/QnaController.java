package com.rehome.main.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.QnaResponse;
import com.rehome.main.entity.Qna;
import com.rehome.main.repository.QnaRepo;

@RequestMapping("/api/qna")
@RestController
@CrossOrigin(origins = "*")
public class QnaController {
	
	@Autowired
    private QnaRepo qnaRepo;
        
    // 1. 取得所有問題 (回傳 ApiResponse)
    @GetMapping("/all")
    public ApiResponse<List<QnaResponse>> getAllQna() {
        
        // 從資料庫撈資料
        List<Qna> entities = qnaRepo.findAllWithQuestionType();
        
        // 轉換 DTO (呼叫下方的共用方法)
        List<QnaResponse> dtos = convertToDtoList(entities);
        
        // 包裝成 ApiResponse 回傳
        return ApiResponse.success("取得所有問題成功", dtos); 
    }

    // 2. 取得隨機 {num} 筆問題 (回傳 ApiResponse)
    @GetMapping("/random/{num}")
    public ApiResponse<List<QnaResponse>> getCustomQna(@PathVariable("num") Integer num) {
        

        List<Integer> randomIds = qnaRepo.findRandomIds(num);

        if (randomIds.isEmpty()) {
            return ApiResponse.success("查詢成功", new ArrayList<>());
        }

        // 第二步：拿這些 ID 去一次把資料 + 關聯資料 全部抓回來
        // 這時候只會產生 1 條 SQL，而且包含 JOIN
        List<Qna> entities = qnaRepo.findByIdsWithFetch(randomIds);

        // 轉換 DTO
        List<QnaResponse> dtos = convertToDtoList(entities);
        
        // 包裝成 ApiResponse 回傳
        return ApiResponse.success("取得隨機問題成功", dtos);
    }

    // --- Private Helper Method (抽離重複的轉換邏輯) ---
    private List<QnaResponse> convertToDtoList(List<Qna> entities) {
        List<QnaResponse> dtos = new ArrayList<>();
        
        for (Qna q : entities) {
            // 防呆：如果 QuestionType 是 null 給空字串
            String typeName = (q.getQuestionType() != null) ? q.getQuestionType().getName() : "";
            
            QnaResponse dto = new QnaResponse(
                q.getId(),
                q.getQuestion(),
                q.getAnswer(),
                typeName 
            );
            dtos.add(dto);
        }
        return dtos;
    }
}
