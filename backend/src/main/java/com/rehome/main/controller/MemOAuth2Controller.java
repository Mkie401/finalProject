package com.rehome.main.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rehome.main.entity.Member;
import com.rehome.main.repository.MemberRepository;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/mem/oauth2")
public class MemOAuth2Controller {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/login-success")
    public void oauth2loginsuccess(OAuth2AuthenticationToken authenticationToken, HttpServletResponse response) throws Exception {

        // 取得使用者資訊
        Map<String, Object> attributes = authenticationToken.getPrincipal().getAttributes();

        // 取得需要的資料
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        System.out.println("Email : " + email);
        System.out.println("Name : " + name);

        // 檢查會員是否存在於資料庫中
        boolean memberExists = memberRepository.existsByEmail(email);

        if(memberExists){
            System.out.println("會員已存在 : " + email);
        }else{
            System.out.println("新會員 : " + email);
            
            // 建立新會員
            Member newMember = new Member();
            newMember.setEmail(email);
            newMember.setName(name);
            newMember.setNickName(null);
            String randomPassword = UUID.randomUUID().toString();
            newMember.setPasswordHash(passwordEncoder.encode(randomPassword));
            newMember.setGender(null);
            newMember.setPhone(null);
            newMember.setBirthDate(null);

            // 存進資料庫
            memberRepository.save(newMember);
            System.out.println("新會員建立成功");
        }
        
        // 導向登入頁面
        response.sendRedirect("/index.html#login");
    }

    

}
