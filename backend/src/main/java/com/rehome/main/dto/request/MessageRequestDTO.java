package com.rehome.main.dto.request;

import lombok.Data;

@Data


	public class MessageRequestDTO {
	    private Long senderId;    
	    private Long receiverId;  
	    private Long roomId;      
	    public String content;
	}