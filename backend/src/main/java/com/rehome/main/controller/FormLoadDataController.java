package com.rehome.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.dto.response.ApiResponse;
import com.rehome.main.dto.response.FormLoadDataResponse;
import com.rehome.main.service.FormLoadDataService;



@RestController
@RequestMapping("/api/public")
public class FormLoadDataController {
    @Autowired
    private FormLoadDataService formLoadDataService;

    @GetMapping("/options")
    public ApiResponse<FormLoadDataResponse> getFormOptions() {
        
        FormLoadDataResponse data = formLoadDataService.getAllFormOptions();

        return ApiResponse.success("取得選項資料成功", data);
    }
}
