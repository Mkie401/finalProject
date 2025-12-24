package com.rehome.main.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CustomerServiceFormRequest {
	private Long questionTypeId; // 前端傳送選單選到的 ID (例如 1)
    private String questionTitle;
    private String questionInfo;
    private String cname;
    private String cmail;
}
