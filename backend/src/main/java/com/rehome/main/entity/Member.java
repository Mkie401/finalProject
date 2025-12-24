package com.rehome.main.entity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 會員實體類別
 */
@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // 【新增】對應資料庫的 mediumblob，通常用 byte[] 存圖片
    @Lob
    @Column(name = "icon", columnDefinition = "MEDIUMBLOB")
    private byte[] icon;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "nick_name", length = 100)
    private String nickName;

    @Column(name = "gender")
    private Boolean gender;

    @Column(name = "phone", length = 10)
    private String phone;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "role", nullable = false)
    private String role = "member";

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp 
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @CreationTimestamp 
    private LocalDateTime updatedAt;

    @Column(name = "status")
    private String status;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (role == null) {
            role = "member";
        }
        if (status == null) {
            status = "active";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ---------------------
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderColumn(name = "id")
    @JsonManagedReference
    private List<AdoptionMember> adoptionMember;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Favorite> favorites;

}
