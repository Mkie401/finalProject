/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.rehome.main.dto.request;

import lombok.Data;

/**
 * 寵物照片 DTO
 */
@Data
public class PetPhotoDto {
 
    private Integer id;
    
    private String src;
    

    private String alt;

    /**
     * 從 base64 字串中提取純 base64 資料（去除前綴）
     * @return 純 base64 字串
     */
    public String getBase64Data() {
        if (src == null || !src.contains(",")) {
            return src;
        }
        // 去除 "data:image/jpeg;base64," 前綴
        return src.substring(src.indexOf(",") + 1);
    }
    
    /**
     * 檢查是否為有效的 base64 圖片
     * @return 是否為有效圖片
     */
    public boolean isValidImage() {
        return src != null && src.startsWith("data:image");
    }
}