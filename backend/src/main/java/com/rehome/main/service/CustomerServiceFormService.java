package com.rehome.main.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rehome.main.dto.request.CustomerServiceFormRequest;
import com.rehome.main.dto.request.ReplyRequest;
import com.rehome.main.dto.response.CustomerServiceFormResponse;
import com.rehome.main.entity.CustomerServiceForm;
import com.rehome.main.entity.QuestionType;
import com.rehome.main.repository.CustomerServiceFormRepo;
import com.rehome.main.repository.QuestionTypeRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceFormService {
    private final CustomerServiceFormRepo customerServiceFormRepo;
    private final QuestionTypeRepo questionTypeRepo;
    private final EmailService emailService;

    public void submitForm(CustomerServiceFormRequest request) {
        log.info("開始處理客服表單: {}", request); // 取代 System.out.println

        // 1. 找問題類型
        QuestionType type = questionTypeRepo.findById(request.getQuestionTypeId())
                .orElseThrow(() -> new RuntimeException("找不到該問題類型 ID: " + request.getQuestionTypeId()));

        // 2. 轉換 Entity (DTO -> Entity)
        CustomerServiceForm form = new CustomerServiceForm();
        form.setQuestionType(type);
        form.setQuestionTitle(request.getQuestionTitle());
        form.setQuestionInfo(request.getQuestionInfo());
        form.setCname(request.getCname());
        form.setCmail(request.getCmail());

        // 3. 存入資料庫
        customerServiceFormRepo.save(form);

        // 4. 寄送 Email (如果寄信失敗不影響存檔，這裡可以再包一層 try-catch)
        try {
            emailService.sendContactConfirmMail(request, type.getName());
        } catch (Exception e) {
            log.error("表單已存檔，但確認信發送失敗: {}", e.getMessage());
            // 這裡可以選擇要不要拋出例外，通常表單存檔成功就算成功，信沒寄出去可以記 Log 就好
        }
    }

// --- 新增功能 1: 獲取所有尚未回覆的表單 ---
    public List<CustomerServiceFormResponse> getAllUnprocessedForms() {
        // 從資料庫抓取 status 為 "unprocessed" 的資料
        List<CustomerServiceForm> entities = customerServiceFormRepo.findByStatusOrderByCreatedTime("unprocessed");
        
        // 轉換為 DTO
        List<CustomerServiceFormResponse> responseList = new ArrayList<>();
        for (CustomerServiceForm form : entities) {
            String typeName = (form.getQuestionType() != null) ? form.getQuestionType().getName() : "未知類型";
            
            responseList.add(new CustomerServiceFormResponse(
                form.getId(),
                typeName,
                form.getQuestionTitle(),
                form.getQuestionInfo(),
                form.getCname(),
                form.getCmail(),
                form.getCreatedTime(),
                form.getStatus()
            ));
        }
        return responseList;
    }


    public void processReply(Long id, ReplyRequest request) {
        // 1. 找表單
        CustomerServiceForm form = customerServiceFormRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到該表單 ID: " + id));

        // 2. 寄信 (這裡假設 EmailService 有一個 sendReplyMail 方法)
        // 如果還沒有，你需要去 EmailService 新增這個方法
        try {
            // 参数: 收件人Email, 標題(或問題標題), 回覆內容
            emailService.sendReplyMail( form.getCname(),form.getCmail(), form.getQuestionTitle(), request.getReplyContent());
        } catch (Exception e) {
            log.error("寄送回覆信件失敗: {}", e.getMessage());
            throw new RuntimeException("寄信失敗，狀態未更新"); 
        }

        // 3. 只有寄信成功才更新狀態
        form.setStatus("processed");
        form.setReply(request.getReplyContent());
        form.setReplyDate(new Date());
        customerServiceFormRepo.save(form);
        
        log.info("客服表單 ID: {} 已回覆並結案", id);
    }

 
    



}
