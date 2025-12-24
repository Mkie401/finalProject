package com.rehome.main.controller;



import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.request.CustomerServiceFormRequest;
import com.rehome.main.dto.request.ReplyRequest;
import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.CustomerServiceFormResponse;
import com.rehome.main.service.CustomerServiceFormService;

import lombok.RequiredArgsConstructor;






@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}, allowCredentials = "true")
@RequestMapping("/api/csf")

@RestController

@RequiredArgsConstructor

public class CsQAController {



 private final CustomerServiceFormService customerServiceFormService;



    @PostMapping("/send")

    public ApiResponse<Boolean> postQ2E(@RequestBody CustomerServiceFormRequest request) {

        try {

            customerServiceFormService.submitForm(request);

            

            return ApiResponse.success(true);



        } catch (Exception e) {

            e.printStackTrace(); // 正式環境建議改用 log.error

            // 修正錯誤訊息，不要只寫"寄信失敗"，因為也可能是資料庫存失敗

            return ApiResponse.fail("表單送出失敗: " + e.getMessage());

        }

    }

    

    @GetMapping("/all")

    // @PreAuthorize("hasRole('ADMIN')")

    public ApiResponse<List<CustomerServiceFormResponse>> getAllUnprocessed() {

        List<CustomerServiceFormResponse> list = customerServiceFormService.getAllUnprocessedForms();

        return ApiResponse.success("取得未回覆表單成功", list);

    }



    @PostMapping("/reply/{id}")

    // @PreAuthorize("hasRole('ADMIN')")

    public ApiResponse<Void> sendReply(@PathVariable Long id, @RequestBody ReplyRequest request) {

        customerServiceFormService.processReply(id, request);

        return ApiResponse.success("回覆成功，已寄送通知信", null);

    }





}