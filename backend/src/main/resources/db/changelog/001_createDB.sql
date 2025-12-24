-- Active: 1764514391160@@127.0.0.1@3306@rehome
-- MySQL Database Schema
-- Language: MySQL
-- 建立資料庫
CREATE DATABASE IF NOT EXISTS rehome
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE rehome;

-- ----------------------------
-- 1. 基礎字典與參考資料表 (No FK dependencies)
-- ----------------------------

-- 縣市 (city)
CREATE TABLE city (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '縣市id',
    name VARCHAR(20) NOT NULL COMMENT '縣市名稱'
);

-- 地區 (region)
CREATE TABLE region (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '地區id',
    city_id INT UNSIGNED NOT NULL COMMENT '縣市id (FK)',
    name VARCHAR(20) NOT NULL COMMENT '鄉鎮市區名稱',
    FOREIGN KEY (city_id) REFERENCES city(id)
);

-- 寵物種類 (animal_species)
CREATE TABLE animal_species (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '寵物種類id',
    name VARCHAR(10) NOT NULL COMMENT '動物種類名稱'
);

-- 案件種類 (case_type)
CREATE TABLE case_type (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '案件種類id',
    name VARCHAR(20) NOT NULL COMMENT '案件種類名稱'
);

-- 案件狀態 (case_status)
CREATE TABLE case_status (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '案件狀態id',
    name VARCHAR(20) NOT NULL COMMENT '案件狀態名稱'
);


-- 會員 (member)
CREATE TABLE member (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '使用者唯一 ID',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '帳號 (Email)，登入用',
    password_hash VARCHAR(255) NOT NULL COMMENT '加密後的密碼',
    icon MEDIUMBLOB COMMENT '圖片',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    nick_name VARCHAR(100) COMMENT '暱稱',
    gender BOOLEAN COMMENT '性別 (T:男/F:女)',
    phone VARCHAR(20) COMMENT '手機號碼',
    birth_date DATE COMMENT '生日',
    role ENUM('visitor','member','admin') DEFAULT 'member' COMMENT '權限角色',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '註冊時間',
    updated_at DATETIME COMMENT '更新時間',
    status ENUM('active','block') COMMENT '活躍Active /黑名單 Block',
    icon MEDIUMBLOB COMMENT '圖片'
);

-- 收容所 (shelter)
CREATE TABLE shelter (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '收容所id',
    region_id INT UNSIGNED NOT NULL COMMENT '所屬地區id (FK)',
    name VARCHAR(100) NOT NULL COMMENT '收容所名稱',
    address VARCHAR(500) COMMENT '收容所地址',
    phone VARCHAR(50) COMMENT '收容所電話',
    lng DECIMAL(10,6) COMMENT '經度',
    lat DECIMAL(10,6) COMMENT '緯度',
    FOREIGN KEY (region_id) REFERENCES region(id)
);

-- 問題種類 (question_type)
CREATE TABLE question_type (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '問題種類id',
    name VARCHAR(20) NOT NULL COMMENT '問題種類名稱'
);

-- 問卷題目 (question)
CREATE TABLE question (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '問卷題目id',
    question VARCHAR(200) NOT NULL COMMENT '題目',
    content VARCHAR(500) COMMENT '內容框',
    sort_order INT COMMENT '題目排序',
    question_category ENUM('adoption','surrender') COMMENT '問卷分類',
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最後更新時間'
);

-- 輪播圖 (banner)
CREATE TABLE banner (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '輪播圖id',
    banner_lg MEDIUMBLOB COMMENT '輪播圖 大',
    banner_sm MEDIUMBLOB COMMENT '輪播圖 小',
    title VARCHAR(100) COMMENT '標題',
    image_url VARCHAR(255) NOT NULL COMMENT '圖片路徑',
    link_url VARCHAR(255) COMMENT '點擊跳轉連結',
    sort_order INT DEFAULT 0 COMMENT '排序權重',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否顯示',
    created_at DATETIME COMMENT '建立時間',
    update_at DATETIME COMMENT '更新時間'
);

-- 罐頭訊息 (template_message)
CREATE TABLE template_message (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '訊息id',
    mesg VARCHAR(300) NOT NULL COMMENT '訊息'
);

-- 貼圖庫 (sticker)
CREATE TABLE sticker (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '圖庫id',
    image MEDIUMBLOB COMMENT '圖'
);

-- ----------------------------
-- 2. 核心交易資料表 (Dependent on Section 1)
-- ----------------------------

-- 案件編號 (cases)
CREATE TABLE cases (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '案號id',
    case_number VARCHAR(50) UNIQUE NOT NULL COMMENT '案件編號',
    case_type_id INT UNSIGNED NOT NULL COMMENT '案件種類id (FK)',
    case_status_id INT UNSIGNED NOT NULL COMMENT '案件狀態id (FK)',
    member_id INT UNSIGNED NOT NULL COMMENT '建案會員id (FK)',
    case_date_start DATETIME COMMENT '立案時間',
    case_date_end DATETIME COMMENT '結案時間',
    description  VARCHAR(200) COMMENT '案件描述',
    FOREIGN KEY (case_type_id) REFERENCES case_type(id),
    FOREIGN KEY (case_status_id) REFERENCES case_status(id),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 寵物資訊 (pet_info)
CREATE TABLE pet_info (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '寵物id',
    case_id INT UNSIGNED NOT NULL COMMENT '案號id (FK)',
    name VARCHAR(50) COMMENT '寵物名稱',
    animal_species_id INT UNSIGNED NOT NULL COMMENT '寵物種類id(FK)',
    animal_species_other VARCHAR(20) COMMENT '其他寵物種類',
    gender ENUM('male','female','unknown') COMMENT '寵物性別',
    breed VARCHAR(20) COMMENT '寵物品種',
    color VARCHAR(20) COMMENT '寵物毛色',
    size ENUM('small','medium','big') COMMENT '寵物體型',
    age ENUM('child','adult','old') COMMENT '寵物年紀',
    feature VARCHAR(20) COMMENT '寵物特徵',
    is_ear_tipping BOOLEAN COMMENT '寵物有無剪耳/結紮',
    is_chip BOOLEAN COMMENT '寵物有無晶片',
    chip_number VARCHAR(20) COMMENT '寵物晶片號碼',
    region_id INT UNSIGNED COMMENT '所在地區id (FK)',
    FOREIGN KEY (case_id) REFERENCES cases(id),
    FOREIGN KEY (animal_species_id) REFERENCES animal_species(id),
    FOREIGN KEY (region_id) REFERENCES region(id)
);

-- 寵物詳細資訊 (pet_detail)
CREATE TABLE pet_detail (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '寵物詳細id',
    case_id INT UNSIGNED NOT NULL COMMENT '案號id (FK)',
    lost_date DATE COMMENT '遺失日期',
    lost_region_id INT UNSIGNED COMMENT '鄉鎮區(FK)',
    lost_addr VARCHAR(100) COMMENT '街道地址/地標描述',
    lng DECIMAL(10,6) COMMENT '經度',
    lat DECIMAL(10,6) COMMENT '緯度',
    lost_process VARCHAR(500) COMMENT '遺失經過',
    is_follow_ager BOOLEAN COMMENT '後須追蹤',
    is_family_ager BOOLEAN COMMENT '家人同意',
    is_age_limit BOOLEAN COMMENT '須滿20',
    adoption_requ VARCHAR(500) COMMENT '領養條件',
    medical_info VARCHAR(500) COMMENT '醫療狀態說明',
    found_place VARCHAR(50) COMMENT '動物尋獲地',
    entry_date DATE COMMENT '入所日期',
    description VARCHAR(500) COMMENT '領養說明',
    FOREIGN KEY (case_id) REFERENCES cases(id),
    FOREIGN KEY (lost_region_id) REFERENCES region(id)
);

-- 聯絡人 (contact)
CREATE TABLE contact (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '聯絡人id',
    case_id INT UNSIGNED NOT NULL COMMENT '案號id (FK)',
    name VARCHAR(50) COMMENT '聯絡稱謂',
    tel VARCHAR(50) COMMENT '連絡電話',
    mail VARCHAR(50) COMMENT '聯絡mail',
    other_contact VARCHAR(50) COMMENT '其他聯絡方式',
    is_phone_display BOOLEAN COMMENT '電話顯示/不顯示',
    is_email_display BOOLEAN COMMENT 'email顯示/不顯示',
    shelter_id INT UNSIGNED COMMENT '收容所id (FK)',
    other_contact VARCHAR(50) COMMENT '其他聯絡方式',
    FOREIGN KEY (case_id) REFERENCES cases(id),
    FOREIGN KEY (shelter_id) REFERENCES shelter(id)
);

-- 寵物圖片 (pet_image)
CREATE TABLE pet_image (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '圖片id',
    case_id INT UNSIGNED NOT NULL COMMENT '案號id (FK)',
    photo MEDIUMBLOB COMMENT '寵物圖片',
    sort_order INT COMMENT '排序權重',
    FOREIGN KEY (case_id) REFERENCES cases(id)
);

-- 走失站內信 (lost_notification)
CREATE TABLE lost_notification (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '站內信id',
    case_id INT UNSIGNED NOT NULL COMMENT '案號id (FK)',
    message VARCHAR(500) COMMENT '訊息內容',
    send_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '訊息日期',
    FOREIGN KEY (case_id) REFERENCES cases(id)
);

-- 可送養地區 (adoption_pet_area) - 複合主鍵
CREATE TABLE adoption_pet_area (
    case_id INT UNSIGNED NOT NULL COMMENT '案號id (FK, PK)',
    city_id INT UNSIGNED NOT NULL COMMENT '縣市id (FK, PK)',
    PRIMARY KEY (case_id, city_id),
    FOREIGN KEY (case_id) REFERENCES cases(id),
    FOREIGN KEY (city_id) REFERENCES city(id)
);

-- 收藏管理 (favorite)
CREATE TABLE favorite (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '收藏id',
    member_id INT UNSIGNED NOT NULL COMMENT '使用者ID(FK)',
    case_id INT UNSIGNED NOT NULL COMMENT '案號id (FK)',
    favorites_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏時間',
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (case_id) REFERENCES cases(id)
);

ALTER TABLE favorite
ADD CONSTRAINT unique_member_case UNIQUE (member_id, case_id);

-- 領養狀態 (adoption_status)
CREATE TABLE adoption_status (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '領養狀態id',
    name VARCHAR(20) NOT NULL COMMENT '領養狀態名稱'
    -- 範例：1.確認資料、2.媒合期-1、3.媒合期-2、4.同意送養、5.不同意
);

-- 案件與領養者 (adoption_member)
CREATE TABLE adoption_member (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '領養申請id',
    case_id INT UNSIGNED NOT NULL COMMENT '案號id (FK)',
    member_id INT UNSIGNED NOT NULL COMMENT '領養者id (FK)',
    adoption_status_id INT UNSIGNED NOT NULL COMMENT '領養狀態id (FK)',
    marital_status ENUM('single','married') COMMENT '婚姻狀態',
    employment_status ENUM('student','employed','unemployed') COMMENT '工作狀態',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申請領養時間',
    end_at DATETIME COMMENT '結束時間',
    FOREIGN KEY (case_id) REFERENCES cases(id), -- 引用 cases 表 (假設 cases 表已存在)
    FOREIGN KEY (member_id) REFERENCES member(id), -- 引用 member 表 (假設 member 表已存在)
    FOREIGN KEY (adoption_status_id) REFERENCES adoption_status(id),
    UNIQUE `uq_member_case` (`case_id`, `member_id`) USING BTREE
);

-- 領養問卷資料 (adoption_question)
CREATE TABLE adoption_question (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '領養問卷回答id',
    adoption_member_id INT UNSIGNED NOT NULL COMMENT '案件與領養者id (FK)',
    question_id INT NOT NULL COMMENT '問卷題目id (FK)',
    answer VARCHAR(500) COMMENT '問題回答',
    
    FOREIGN KEY (adoption_member_id) REFERENCES adoption_member(id),
    FOREIGN KEY (question_id) REFERENCES question(id) -- 引用 question 表 (假設 question 表已存在)
);

-- 常見問題 (qna)
CREATE TABLE qna (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '常見問題id',
    question VARCHAR(300) NOT NULL COMMENT '問題敘述',
    answer VARCHAR(300) NOT NULL COMMENT '問題解答',
    question_type_id INT UNSIGNED COMMENT '問題種類id (FK)',
    FOREIGN KEY (question_type_id) REFERENCES question_type(id)
);

-- 客服表單 (customer_service_form)
CREATE TABLE customer_service_form (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '客服表單id',
    question_title VARCHAR(100) NOT NULL COMMENT '問題標題',
    question_info VARCHAR(500) COMMENT '問題描述',
    cname VARCHAR(20) COMMENT '稱謂',
    cmail VARCHAR(100) COMMENT '聯絡mail',
    question_type_id INT UNSIGNED COMMENT '問題種類id (FK)',
    form_date DATETIME COMMENT '表單時間',
    reply VARCHAR(500) COMMENT '客服回覆',
    reply_date DATETIME COMMENT '客服回覆時間',
    status ENUM('unprocessed','processed') COMMENT '表單狀態',
    reply VARCHAR(500) COMMENT '客服回覆',
    reply_date DATETIME COMMENT '客服回覆時間',
    FOREIGN KEY (question_type_id) REFERENCES question_type(id)
);

-- ----------------------------
-- 3. 聊天室相關資料表 (Circular FK dependency)
-- ----------------------------

-- 聊天室對話 (chatroom) - 暫時不加 last_mesg_id 的外鍵
CREATE TABLE chatroom (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '聊天室id',
    room_type ENUM('USER_TO_USER', 'CS_TO_USER') COMMENT '聊天類型',
    user_a_id INT UNSIGNED NOT NULL COMMENT 'A方 (FK) - 送養人',
    user_b_id INT UNSIGNED NOT NULL COMMENT 'B方 (FK) - 領養人',
    create_date DATETIME COMMENT '創建時間',
    last_mesg_id INT COMMENT '最後訊息id', -- 暫時不設 FK
    last_mesg_date DATETIME COMMENT '最後訊息時間',
    description VARCHAR(500) COMMENT '客服描述',
    FOREIGN KEY (user_a_id) REFERENCES member(id),
    FOREIGN KEY (user_b_id) REFERENCES member(id)
);

-- 對話儲存 (store_mesg)
CREATE TABLE store_mesg (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '訊息id',
    chatroom_id INT NOT NULL COMMENT '聊天室id (FK)',
    sender_id INT UNSIGNED NOT NULL COMMENT '發送者id (FK)',
    text VARCHAR(500) COMMENT '對話類型(文字)',
    img MEDIUMBLOB COMMENT '對話類型(圖片)',
    time DATETIME COMMENT '發送時間',
    is_read BOOLEAN COMMENT '是否已讀',
    FOREIGN KEY (chatroom_id) REFERENCES chatroom(id),
    FOREIGN KEY (sender_id) REFERENCES member(id)
);

-- 12/3 建立 Email OTP 驗證表
CREATE TABLE email_otp (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'OTP紀錄id',
    email VARCHAR(100) NOT NULL COMMENT '驗證的Email',
    otp_code VARCHAR(6) NOT NULL COMMENT '6位數驗證碼',
    created_at DATETIME NOT NULL COMMENT '建立時間',
    expires_at DATETIME NOT NULL COMMENT '過期時間',
    verified BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已驗證',
    INDEX idx_email (email),
    INDEX idx_expires_at (expires_at)
    ) COMMENT='Email OTP驗證表';

-- 建立重設密碼 Token 資料表 12/9
CREATE TABLE IF NOT EXISTS password_reset_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_token (token),
    FOREIGN KEY (email) REFERENCES member(email)
);