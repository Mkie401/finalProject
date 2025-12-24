package com.rehome.main.entity;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;


// 對應資料庫表
@Data
@Entity
@Table(name = "member")
public class MemEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name="email")
    private String email;
    
    @Column(name = "password_hash")
    private String password;
    
    @Column(name="name")
    private String name;
    
    @Column(name = "nick_name")
    private String nickName;
    
    @Column(name="gender")
    private Boolean gender; // true=男性, false=女性
    
    @Column(name="phone")
    private String phone;
    
    @Column(name = "birth_date")
    private Date birthDate;
    
    @Column(name = "created_at")
    @CreationTimestamp  // Hibernate 自動設定建立時間
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp  // Hibernate 自動設定更新時間
    private LocalDateTime updatedAt;

    @Lob // 大型物件
    @Column(name= "icon")
    private byte[] icon;
}