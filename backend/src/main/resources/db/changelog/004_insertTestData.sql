-- Active: 1765184100588@@mysql-rehome-rehome.c.aivencloud.com@19110@rehome
-- 004_insertTestData.sql
-- 清爽的一致測試資料檔 (MySQL)
-- 目標：走失(ZS)、送養(SY)、收容所(GL) 各 20 筆 (共 60 筆) + 支援表格
-- 注意：不修改 001/002/003 基本結構與資料 (region, case_type, case_status, adoption_status 等)

-- ================================
-- 1) 會員 (member) - 10 人
-- ================================
INSERT INTO member (email, password_hash, name, nick_name, gender, phone, birth_date, role, status, created_at) VALUES
('user01@example.com','$2a$10$hash01','王小明','user01','1','0912000001','1990-01-01','member','active','2025-11-01 09:00:00'),
('user02@example.com','$2a$10$hash02','李美麗','user02','0','0912000002','1991-02-02','member','active','2025-11-02 09:00:00'),
('user03@example.com','$2a$10$hash03','陳建國','user03','1','0912000003','1992-03-03','member','active','2025-11-03 09:00:00'),
('user04@example.com','$2a$10$hash04','黃心怡','user04','0','0912000004','1993-04-04','member','active','2025-11-04 09:00:00'),
('user05@example.com','$2a$10$hash05','吳世豪','user05','1','0912000005','1994-05-05','member','active','2025-11-05 09:00:00'),
('user06@example.com','$2a$10$hash06','李小華','user06','0','0912000006','1995-06-06','member','active','2025-11-06 09:00:00'),
('user07@example.com','$2a$10$hash07','王小美','user07','1','0912000007','1996-07-07','member','active','2025-11-07 09:00:00'),
('user08@example.com','$2a$10$hash08','陳小玲','user08','0','0912000008','1997-08-08','member','active','2025-11-08 09:00:00'),
('user09@example.com','$2a$10$hash09','林志明','user09','1','0912000009','1998-09-09','member','active','2025-11-09 09:00:00'),
('user10@example.com','$2a$10$hash10','張惠玲','user10','0','0912000010','1999-10-10','member','active','2025-11-10 09:00:00'),
('admin@rehome.com','$2a$10$SUzO/cDU68l3b/lIu545Juv6LMpfAWICY54Sa.mqkqsjUteWxTMq6','admin','admin','0','0912000011','1999-11-11','admin','active','2025-11-10 09:00:00');
-- ================================
-- 2) cases (60 筆) 走失 ZS (1..20), 送養 SY (21..40), 收容所 GL (41..60)
-- 請注意：case_type_id: 1=走失,2=送養,3=收容所 (來自 003_insertBasicData_type.sql)
-- case_status_id 使用 1/2/3 的範例值 (等待審核 / 審核成功 / 審核失敗)
-- ================================

-- 2.1 ZS 1..20
INSERT INTO cases (case_number, case_type_id, case_status_id, member_id, case_date_start, description) VALUES
('ZS202511010001',1,1,1,'2025-11-01 08:00:00','走失：小米在松山區'),
('ZS202511010002',1,1,2,'2025-11-02 08:30:00','走失：胖虎在信義區'),
('ZS202511010003',1,2,3,'2025-11-03 09:00:00','走失：小狸在中正區'),
('ZS202511010004',1,1,4,'2025-11-04 09:30:00','走失：短尾犬在大安區'),
('ZS202511010005',1,2,5,'2025-11-05 10:00:00','走失：小灰在板橋區'),
('ZS202511010006',1,1,6,'2025-11-06 10:30:00','走失：小米2在北投區'),
('ZS202511010007',1,1,7,'2025-11-07 11:00:00','走失：小柚在內湖區'),
('ZS202511010008',1,2,8,'2025-11-08 11:30:00','走失：虎斑在文山區'),
('ZS202511010009',1,1,9,'2025-11-09 12:00:00','走失：小黃在新店區'),
('ZS202511010010',1,1,10,'2025-11-10 12:30:00','走失：小藍在三重區'),
('ZS202511010011',1,1,1,'2025-11-11 13:00:00','走失：小綠在汐止區'),
('ZS202511010012',1,2,2,'2025-11-12 13:30:00','走失：小紅在土城區'),
('ZS202511010013',1,1,3,'2025-11-13 14:00:00','走失：三色貓在淡水區'),
('ZS202511010014',1,2,4,'2025-11-14 14:30:00','走失：小白在瑞芳區'),
('ZS202511010015',1,1,5,'2025-11-15 15:00:00','走失：小黑在八里區'),
('ZS202511010016',1,1,6,'2025-11-16 15:30:00','走失：小花在宜蘭區'),
('ZS202511010017',1,2,7,'2025-11-17 16:00:00','走失：小虎在花蓮區'),
('ZS202511010018',1,1,8,'2025-11-18 16:30:00','走失：小咪在高雄區'),
('ZS202511010019',1,1,9,'2025-11-19 17:00:00','走失：小寶在台南區'),
('ZS202511010020',1,2,10,'2025-11-20 17:30:00','走失：小子在彰化區');

-- 2.2 SY 21..40
INSERT INTO cases (case_number, case_type_id, case_status_id, member_id, case_date_start, description) VALUES
('SY202511010021',2,1,1,'2025-11-01 09:00:00','送養：幼貓，室內優先'),
('SY202511010022',2,1,2,'2025-11-02 09:30:00','送養：中型犬，友善可教導'),
('SY202511010023',2,2,3,'2025-11-03 09:45:00','送養：母犬帶小狗，優先合適家庭'),
('SY202511010024',2,1,4,'2025-11-04 10:10:00','送養：安靜成貓'),
('SY202511010025',2,3,5,'2025-11-05 10:30:00','送養：需有飼養經驗者'),
('SY202511010026',2,1,6,'2025-11-06 10:50:00','送養：親人小狗，已結紮'),
('SY202511010027',2,1,7,'2025-11-07 11:10:00','送養：幼犬兩隻，已施打疫苗'),
('SY202511010028',2,1,8,'2025-11-08 11:30:00','送養：幼貓一隻，室內優先'),
('SY202511010029',2,2,9,'2025-11-09 11:55:00','送養：需要穩定家庭'),
('SY202511010030',2,1,10,'2025-11-10 12:10:00','送養：溫馴小型犬'),
('SY202511010031',2,1,1,'2025-11-11 12:40:00','送養：兩隻兄弟貓一起送'),
('SY202511010032',2,2,2,'2025-11-12 13:00:00','送養：園區救援犬，需社會化'),
('SY202511010033',2,1,3,'2025-11-13 13:20:00','送養：安靜成年貓'),
('SY202511010034',2,1,4,'2025-11-14 13:40:00','送養：家庭無法持續飼養之犬'),
('SY202511010035',2,1,5,'2025-11-15 14:00:00','送養：幼貓可單獨或配對'),
('SY202511010036',2,1,6,'2025-11-16 14:20:00','送養：訓練過的中型犬'),
('SY202511010037',2,2,7,'2025-11-17 14:40:00','送養：適合小套房的貓'),
('SY202511010038',2,1,8,'2025-11-18 15:00:00','送養：幼犬親人'),
('SY202511010039',2,2,9,'2025-11-19 15:20:00','送養：母貓帶兩隻幼貓'),
('SY202511010040',2,1,10,'2025-11-20 15:40:00','送養：老年犬需要穩定環境');

-- 2.3 GL 41..60
INSERT INTO cases (case_number, case_type_id, case_status_id, member_id, case_date_start, description) VALUES
('GL202511010041',3,1,1,'2025-11-01 11:00:00','收容：流浪犬需要救援'),
('GL202511010042',3,2,2,'2025-11-02 11:30:00','收容：受傷母貓'),
('GL202511010043',3,1,3,'2025-11-03 12:00:00','收容：小型可領養犬'),
('GL202511010044',3,1,4,'2025-11-04 12:30:00','收容：幼貓團體'),
('GL202511010045',3,1,5,'2025-11-05 13:00:00','收容：康復中犬'),
('GL202511010046',3,2,6,'2025-11-06 13:30:00','收容：需醫療貓'),
('GL202511010047',3,1,7,'2025-11-07 14:00:00','收容：園區入所犬'),
('GL202511010048',3,1,8,'2025-11-08 14:30:00','收容：幼犬群等待認養'),
('GL202511010049',3,2,9,'2025-11-09 15:00:00','收容：迷路貓需辨識'),
('GL202511010050',3,1,10,'2025-11-10 15:30:00','收容：母犬可領養'),
('GL202511010051',3,1,1,'2025-11-11 16:00:00','收容：多隻分配中'),
('GL202511010052',3,2,2,'2025-11-12 16:30:00','收容：老犬照護'),
('GL202511010053',3,1,3,'2025-11-13 17:00:00','收容：幼貓群待認養'),
('GL202511010054',3,1,4,'2025-11-14 17:30:00','收容：術後復原貓'),
('GL202511010055',3,2,5,'2025-11-15 18:00:00','收容：需長期照護犬'),
('GL202511010056',3,1,6,'2025-11-16 18:30:00','收容：活潑幼犬'),
('GL202511010057',3,1,7,'2025-11-17 19:00:00','收容：長期無主犬'),
('GL202511010058',3,2,8,'2025-11-18 19:30:00','收容：術後貓待觀察'),
('GL202511010059',3,1,9,'2025-11-19 20:00:00','收容：中型犬等待領養'),
('GL202511010060',3,1,10,'2025-11-20 20:30:00','收容：需長期醫療貓');

-- ================================
-- 3) pet_info / pet_detail / contact / pet_image (簡化樣本)
-- ================================

-- pet_info (one row per case)
INSERT INTO pet_info (case_id, name, animal_species_id, gender, breed, color, size, age, feature, is_ear_tipping, is_chip, chip_number, region_id) VALUES
(1,'小米',1,'male','混種','白','small','adult','短尾',FALSE,FALSE,NULL,1),
(2,'胖虎',1,'male','米克斯','黑','medium','adult','友善',TRUE,TRUE,'CHIP002',2),
(3,'小狸',2,'female','短毛貓','灰','small','child','溫馴',FALSE,FALSE,NULL,3),
(4,'短尾',1,'male','短尾犬','咖啡','small','adult','忠實',TRUE,TRUE,'CHIP004',4),
(5,'小灰',2,'female','三花貓','三色','small','adult','害羞',FALSE,FALSE,NULL,5),
(6,'小米2',1,'male','混種','黑棕','medium','adult','活潑',TRUE,TRUE,'CHIP006',6),
(7,'小柚',1,'male','柯基','棕白','small','adult','短腿',FALSE,TRUE,'CHIP007',7),
(8,'虎斑',2,'female','虎斑貓','虎斑','small','adult','好動',FALSE,FALSE,NULL,8),
(9,'小黃',1,'female','混種','黃','small','child','黏人',TRUE,FALSE,NULL,9),
(10,'小藍',2,'male','波斯','白','small','child','藍眼',FALSE,TRUE,'CHIP010',10),
(11,'小綠',1,'male','老犬','灰','big','old','穩重',TRUE,TRUE,'CHIP011',11),
(12,'小紅',2,'female','家貓','黑白','small','adult','安靜',FALSE,FALSE,NULL,12),
(13,'三色',2,'female','三色貓','三色','small','adult','膽小',FALSE,FALSE,NULL,13),
(14,'白白',2,'female','幼貓','橘','small','child','活潑',FALSE,FALSE,NULL,14),
(15,'黑黑',2,'female','家貓','黑','small','adult','親人',FALSE,FALSE,NULL,15),
(16,'米米',1,'male','混種','白','medium','adult','調皮',FALSE,TRUE,'CHIP016',16),
(17,'虎虎2',2,'male','虎斑','灰黑','small','adult','怕生',FALSE,FALSE,NULL,17),
(18,'小米3',1,'male','混種','咖啡','small','child','溫馴',FALSE,TRUE,'CHIP018',18),
(19,'灰灰2',2,'female','短毛貓','灰','small','adult','膽小',FALSE,FALSE,NULL,19),
(20,'園犬',1,'male','混種','黑棕','medium','adult','社交化中',TRUE,TRUE,'CHIP020',20),
(21,'送養貓1',2,'female','家貓','白','small','child','親人',FALSE,TRUE,'SY021',1),
(22,'送養犬1',1,'male','米克斯','棕','medium','adult','溫順',TRUE,TRUE,'SY022',2),
(23,'送養犬2',1,'female','混種','黑','big','adult','安靜',TRUE,TRUE,'SY023',3),
(24,'送養貓2',2,'male','短毛貓','虎斑','small','adult','乖巧',FALSE,FALSE,NULL,4),
(25,'送養犬3',1,'male','拉布拉多','金','big','adult','友好',TRUE,TRUE,'SY025',5),
(26,'送養貓3',2,'female','幼貓','橘','small','child','活潑',FALSE,TRUE,'SY026',6),
(27,'送養犬4',1,'male','幼犬','淺棕','small','child','好動',FALSE,TRUE,'SY027',7),
(28,'送養貓4',2,'female','室內貓','灰白','small','adult','愛撒嬌',FALSE,FALSE,NULL,8),
(29,'送養貓5',2,'male','成貓','深灰','small','adult','害羞',FALSE,FALSE,NULL,9),
(30,'送養犬5',1,'male','小型犬','白點','small','adult','親人',TRUE,TRUE,'SY030',10),
(31,'送養貓6',2,'female','幼貓A','黑白','small','child','兄弟',FALSE,TRUE,'SY031',1),
(32,'送養犬6',1,'male','園區犬','灰褐','big','adult','需訓練',TRUE,TRUE,'SY032',2),
(33,'送養貓7',2,'female','成年貓B','灰白','small','adult','安靜',FALSE,FALSE,NULL,3),
(34,'送養犬7',1,'female','中型犬','金黃','medium','adult','溫馴',TRUE,TRUE,'SY034',4),
(35,'送養貓8',2,'male','幼貓C','橘白','small','child','兄弟',FALSE,FALSE,NULL,5),
(36,'送養犬8',1,'male','訓練犬','黑','big','adult','訓練良好',TRUE,TRUE,'SY036',6),
(37,'送養貓9',2,'female','室內貓2','白','small','adult','適合套房',FALSE,FALSE,NULL,7),
(38,'送養犬9',1,'male','幼犬B','淺黃','small','child','活潑',FALSE,TRUE,'SY038',8),
(39,'送養貓10',2,'female','母貓帶崽','灰','small','adult','帶幼貓',FALSE,FALSE,NULL,9),
(40,'送養犬10',1,'male','老犬2','灰白','big','old','溫和',TRUE,TRUE,'SY040',10),
(41,'收容犬1',1,'male','米克斯','黑棕','medium','adult','受傷恢復',TRUE,TRUE,'GL041',11),
(42,'收容貓1',2,'female','家貓','花色','small','adult','需觀察',FALSE,FALSE,NULL,12),
(43,'領養犬1',1,'male','小型犬','黃','small','child','親人',TRUE,TRUE,'GL043',13),
(44,'幼貓團',2,'female','幼貓','橘白','small','child','社會化',FALSE,FALSE,NULL,14),
(45,'外傷犬2',1,'female','流浪犬','灰','big','adult','康復中',FALSE,FALSE,NULL,15),
(46,'醫療貓',2,'male','家貓','黑','small','adult','術後觀察',FALSE,FALSE,NULL,16),
(47,'園區犬1',1,'male','混種','深褐','big','adult','需訓練',TRUE,TRUE,'GL047',17),
(48,'幼犬團',1,'female','幼犬','白','small','child','活潑',FALSE,TRUE,'GL048',18),
(49,'迷路貓1',2,'female','家貓','灰','small','adult','保守',FALSE,FALSE,NULL,19),
(50,'母犬1',1,'female','家犬','棕','medium','adult','親人',TRUE,TRUE,'GL050',20),
(51,'多隻1',1,'male','混種','多色','big','adult','多隻',FALSE,FALSE,NULL,11),
(52,'老人犬1',1,'female','老犬','灰白','big','old','需照護',TRUE,TRUE,'GL052',12),
(53,'幼貓群3',2,'female','幼貓','淺灰','small','child','等待認養',FALSE,FALSE,NULL,13),
(54,'治療貓2',2,'male','家貓','花色','small','adult','已治療',FALSE,FALSE,NULL,14),
(55,'特殊犬2',1,'male','混種','黑','big','adult','需照護',TRUE,FALSE,NULL,15),
(56,'幼犬3',1,'female','幼犬','淺棕','small','child','可愛',FALSE,TRUE,'GL056',16),
(57,'無主犬1',1,'male','混種','深褐','big','adult','怕生',TRUE,FALSE,NULL,17),
(58,'術後觀察貓1',2,'female','家貓','白','small','adult','術後觀察',FALSE,FALSE,NULL,18),
(59,'中型犬1',1,'male','中型犬','棕黑','medium','adult','健康',TRUE,TRUE,'GL059',19),
(60,'需照護貓1',2,'female','成貓','灰白','small','adult','醫療需追蹤',FALSE,FALSE,NULL,20);

-- pet_detail（根據案件型態適當填欄）
INSERT INTO pet_detail (case_id, lost_date, lost_region_id, lost_addr, lng, lat, lost_process, is_follow_ager, is_family_ager, is_age_limit, adoption_requ, medical_info, entry_date, description) VALUES
(1,'2025-11-01',1,'松山某巷',121.5549,25.0512,'牽繩脫落',TRUE,TRUE,FALSE,NULL,NULL,NULL,NULL),
(2,'2025-11-02',2,'信義路口',121.5650,25.0334,'外出不見',TRUE,FALSE,FALSE,NULL,NULL,NULL,NULL),
(3,'2025-11-03',3,'中正站周邊',121.5130,25.0320,'外出未歸',TRUE,TRUE,FALSE,NULL,NULL,NULL,NULL),
(4,'2025-11-04',4,'大安公園',121.5334,25.0269,'散步時走失',TRUE,TRUE,FALSE,NULL,NULL,NULL,NULL),
(5,'2025-11-05',5,'板橋夜市',121.4567,25.0123,'夜間走失',TRUE,FALSE,FALSE,NULL,NULL,NULL,NULL),
(6,'2025-11-06',6,'北投溫泉街',121.5123,25.1432,'失聯',TRUE,TRUE,FALSE,NULL,NULL,NULL,NULL),
(7,'2025-11-07',7,'內湖公園',121.5801,25.0621,'被聲響嚇跑',FALSE,TRUE,FALSE,NULL,NULL,NULL,NULL),
(8,'2025-11-08',8,'景美夜市',121.5477,24.9991,'門縫逃出',TRUE,TRUE,FALSE,NULL,NULL,NULL,NULL),
(9,'2025-11-09',9,'新店商圈',121.4956,24.9612,'外出未歸',TRUE,FALSE,FALSE,NULL,NULL,NULL,NULL),
(10,'2025-11-10',10,'三重捷運站',121.4723,25.0601,'離開後失聯',FALSE,FALSE,FALSE,NULL,NULL,NULL,NULL),
(11,'2025-11-11',11,'汐止公園',121.6600,25.0640,'驚嚇逃逸',TRUE,TRUE,FALSE,NULL,NULL,NULL,NULL),
(12,'2025-11-12',12,'土城中山路',121.4433,24.9600,'不見了',FALSE,FALSE,FALSE,NULL,NULL,NULL,NULL),
(13,'2025-11-13',13,'淡水老街',121.4499,25.1642,'找不到回家路',TRUE,FALSE,FALSE,NULL,NULL,NULL,NULL),
(14,'2025-11-14',14,'瑞芳山區',121.8012,25.1066,'外出未歸',FALSE,FALSE,FALSE,NULL,NULL,NULL,NULL),
(15,'2025-11-15',15,'八里濱海',121.3973,25.1420,'夜間失蹤',TRUE,TRUE,FALSE,NULL,NULL,NULL,NULL),
(16,'2025-11-16',16,'宜蘭市區',121.7500,24.7543,'散步迷路',FALSE,FALSE,FALSE,NULL,NULL,NULL,NULL),
(17,'2025-11-17',17,'花蓮港',121.4804,23.9702,'離家未回',TRUE,FALSE,FALSE,NULL,NULL,NULL,NULL),
(18,'2025-11-18',18,'高雄港邊',120.3123,22.6162,'被誤帶走',TRUE,TRUE,FALSE,NULL,NULL,NULL,NULL),
(19,'2025-11-19',19,'台南市中心',120.2176,22.9976,'突然失蹤',FALSE,TRUE,FALSE,NULL,NULL,NULL,NULL),
(20,'2025-11-20',20,'彰化公園',120.5401,24.0701,'遊玩時不見',TRUE,FALSE,FALSE,NULL,NULL,NULL,NULL),
(21,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'需室內飼養','已接種1劑',NULL,'幼貓，室內優先'),
(22,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'需庭院','已結紮',NULL,'中型犬，需經驗者'),
(23,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'帶小狗者優先','已結紮',NULL,'母犬帶幼犬'),
(24,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'室內優先','健康',NULL,'安靜成年貓'),
(25,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'年長者需穩定環境','慢性病需管理',NULL,'老年犬'),
(26,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'幼貓需陪伴','第1劑已接種',NULL,'需要時間適應'),
(27,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'需社會化','已接種',NULL,'幼犬需陪養'),
(28,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'室內優先','健康',NULL,'室內貓'),
(29,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'適合新手','健康',NULL,'害羞但可教'),
(30,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'適合小家庭','已接種疫苗',NULL,'小型犬親人'),
(31,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'雙領養優先','健康',NULL,'兄弟一起'),
(32,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'需訓練空間','健康',NULL,'園區犬'),
(33,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'室內安靜環境','健康',NULL,'成年貓安靜'),
(34,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'需每日運動','已接種',NULL,'中型犬'),
(35,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'兄弟一起優先','已接種',NULL,'幼貓一對'),
(36,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'需訓練環境','健康',NULL,'有基礎訓練'),
(37,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'僅限室內','健康',NULL,'套房適合'),
(38,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'需活動時間','已接種',NULL,'幼犬活潑'),
(39,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'母貓帶幼貓優先','健康',NULL,'帶幼崽'),
(40,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,'需穩定環境','慢性病需觀察',NULL,'老犬需關懷'),
(41,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,'需觀察寄生蟲','2025-11-01','收容後治療'),
(42,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-02','被民眾送入'),
(43,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-03','收容後準備領養'),
(44,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,'已接種','2025-11-04','幼貓群入所'),
(45,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,'已處理外傷','2025-11-05','康復中'),
(46,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,'術後需追蹤','2025-11-06','觀察中'),
(47,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-07','園區接收'),
(48,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-08','幼犬需社會化'),
(49,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-09','夜間送入'),
(50,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-10','母犬狀況良好'),
(51,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-11','多隻分配中'),
(52,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-12','老人犬休養中'),
(53,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-13','幼貓群待認養'),
(54,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-14','已康復待出院'),
(55,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-15','特殊需求'),
(56,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-16','幼犬可領養'),
(57,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-17','無主犬需辨識'),
(58,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-18','術後觀察'),
(59,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-19','等待認養'),
(60,NULL,NULL,NULL,NULL,NULL,NULL,FALSE,FALSE,FALSE,NULL,NULL,'2025-11-20','需長期照護');

-- contact (聯絡人資訊，走失、送養、收容所案件各有聯絡人)
INSERT INTO contact (case_id, name, tel, mail, is_phone_display, is_email_display, shelter_id) VALUES
(1,'王小明','0912000001','user01@example.com',TRUE,TRUE,NULL),
(2,'李美麗','0912000002','user02@example.com',TRUE,FALSE,NULL),
(3,'陳建國','0912000003','user03@example.com',FALSE,TRUE,NULL),
(4,'黃心怡','0912000004','user04@example.com',TRUE,TRUE,NULL),
(5,'吳世豪','0912000005','user05@example.com',TRUE,TRUE,NULL),
(6,'李小華','0912000006','user06@example.com',FALSE,FALSE,NULL),
(7,'王小美','0912000007','user07@example.com',TRUE,TRUE,NULL),
(8,'陳小玲','0912000008','user08@example.com',TRUE,FALSE,NULL),
(9,'林志明','0912000009','user09@example.com',FALSE,TRUE,NULL),
(10,'張惠玲','0912000010','user10@example.com',TRUE,TRUE,NULL),
(11,'王小明','0912000001','user01@example.com',TRUE,TRUE,NULL),
(12,'李美麗','0912000002','user02@example.com',TRUE,FALSE,NULL),
(13,'陳建國','0912000003','user03@example.com',FALSE,TRUE,NULL),
(14,'黃心怡','0912000004','user04@example.com',TRUE,TRUE,NULL),
(15,'吳世豪','0912000005','user05@example.com',TRUE,TRUE,NULL),
(16,'李小華','0912000006','user06@example.com',FALSE,FALSE,NULL),
(17,'王小美','0912000007','user07@example.com',TRUE,TRUE,NULL),
(18,'陳小玲','0912000008','user08@example.com',TRUE,FALSE,NULL),
(19,'林志明','0912000009','user09@example.com',FALSE,TRUE,NULL),
(20,'張惠玲','0912000010','user10@example.com',TRUE,TRUE,NULL),
(21,'王小明','0912000001','user01@example.com',TRUE,TRUE,NULL),
(22,'李美麗','0912000002','user02@example.com',TRUE,FALSE,NULL),
(23,'陳建國','0912000003','user03@example.com',FALSE,TRUE,NULL),
(24,'黃心怡','0912000004','user04@example.com',TRUE,TRUE,NULL),
(25,'吳世豪','0912000005','user05@example.com',TRUE,TRUE,NULL),
(26,'李小華','0912000006','user06@example.com',FALSE,FALSE,NULL),
(27,'王小美','0912000007','user07@example.com',TRUE,TRUE,NULL),
(28,'陳小玲','0912000008','user08@example.com',TRUE,FALSE,NULL),
(29,'林志明','0912000009','user09@example.com',FALSE,TRUE,NULL),
(30,'張惠玲','0912000010','user10@example.com',TRUE,TRUE,NULL),
(31,'王小明','0912000001','user01@example.com',TRUE,TRUE,NULL),
(32,'李美麗','0912000002','user02@example.com',TRUE,FALSE,NULL),
(33,'陳建國','0912000003','user03@example.com',FALSE,TRUE,NULL),
(34,'黃心怡','0912000004','user04@example.com',TRUE,TRUE,NULL),
(35,'吳世豪','0912000005','user05@example.com',TRUE,TRUE,NULL),
(36,'李小華','0912000006','user06@example.com',FALSE,FALSE,NULL),
(37,'王小美','0912000007','user07@example.com',TRUE,TRUE,NULL),
(38,'陳小玲','0912000008','user08@example.com',TRUE,FALSE,NULL),
(39,'林志明','0912000009','user09@example.com',FALSE,TRUE,NULL),
(40,'張惠玲','0912000010','user10@example.com',TRUE,TRUE,NULL),
(41,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(42,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(43,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(44,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(45,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(46,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(47,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(48,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(49,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(50,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(51,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(52,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(53,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(54,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(55,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(56,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(57,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(58,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(59,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3),
(60,'新北市板橋動物之家','02-89662158','shelter-banqiao@example.com',TRUE,TRUE,3);

-- pet_image (每個案件至少一張示意)
INSERT INTO pet_image (case_id, sort_order) VALUES
(1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(10,1),
(11,1),(12,1),(13,1),(14,1),(15,1),(16,1),(17,1),(18,1),(19,1),(20,1),
(21,1),(22,1),(23,1),(24,1),(25,1),(26,1),(27,1),(28,1),(29,1),(30,1),
(31,1),(32,1),(33,1),(34,1),(35,1),(36,1),(37,1),(38,1),(39,1),(40,1),
(41,1),(42,1),(43,1),(44,1),(45,1),(46,1),(47,1),(48,1),(49,1),(50,1),
(51,1),(52,1),(53,1),(54,1),(55,1),(56,1),(57,1),(58,1),(59,1),(60,1);

-- ================================
-- 4) adoption_pet_area (示例, 部分送養案件允許多個地區)
-- ================================
INSERT INTO adoption_pet_area (case_id, city_id) VALUES
(21,1),(21,2),(22,3),(23,4),(24,1),(25,5),(31,7),(34,11),(36,6),(39,3);

-- ================================
-- 5) favorite / lost_notification
-- ================================
INSERT INTO favorite (member_id, case_id, favorites_date) VALUES
(1,22,'2025-11-02 12:00:00'),(2,21,'2025-11-02 12:30:00'),(3,23,'2025-11-03 13:00:00'),(4,41,'2025-11-05 09:00:00'),(5,41,'2025-11-05 10:00:00');

INSERT INTO lost_notification (case_id, message, send_date) VALUES
(1,'您的走失案件已上線，請留意回報','2025-11-02'),(3,'有民眾回報疑似目擊位置','2025-11-04'),(5,'系統提醒：擴大搜尋範圍','2025-11-06');

-- ================================
-- 6) chatroom / store_mesg（示例）
-- ================================
INSERT INTO chatroom (room_type, user_a_id, user_b_id, create_date, last_mesg_date, description) VALUES
('USER_TO_USER',1,2,'2025-11-01 10:00:00','2025-11-01 10:05:00','詢問走失案件 #1'),
('USER_TO_USER',3,4,'2025-11-02 11:00:00','2025-11-02 11:10:00','洽詢送養 #22'),
('CS_TO_USER',5,6,'2025-11-03 09:00:00','2025-11-03 09:05:00','客服處理照片上傳問題');

INSERT INTO store_mesg (chatroom_id, sender_id, text, time, is_read) VALUES
(1,1,'您好，請問這隻狗最後出現在哪裡？','2025-11-01 10:02:00',TRUE),
(1,2,'在市民大道與信義路口附近。','2025-11-01 10:05:00',TRUE),
(2,3,'請問幼貓健康狀況？','2025-11-02 11:02:00',TRUE),
(2,4,'已施打第1劑疫苗，狀況良好。','2025-11-02 11:10:00',TRUE),
(3,5,'請提供詳細問題與截圖。','2025-11-03 09:01:00',TRUE);

-- ================================
-- 7) qna / customer_service_form / banner / template_message
-- ================================
INSERT INTO qna (question, answer, question_type_id) VALUES
('領養需要什麼條件？','年滿18，願配合家訪',1),
('如何發布走失？','按發布走失並上傳照片',1);

INSERT INTO customer_service_form (question_title, question_info, cname, cmail, question_type_id, form_date, status) VALUES
('無法上傳圖片','上傳時發生錯誤','王先生','cs1@example.com',1,'2025-11-05 08:00:00','unprocessed');

INSERT INTO banner (title, image_url, link_url, sort_order, is_active) VALUES
('歡迎來到 Re-Home','/images/banner_welcome.jpg','/home',1,TRUE);

INSERT INTO template_message (mesg) VALUES
('系統：您有新的案件回報'),('系統：請確認案件狀態');

-- ================================
-- 8) adoption_member + adoption_question（至少 5 筆領養申請）
--    5 位 member (1..5) 申請領養 case 21..25
--    用 subquery 對應 adoption_member.id 用於插入問卷
-- ================================
INSERT INTO adoption_member (case_id, member_id, adoption_status_id, marital_status, employment_status) VALUES
(21,1,1,'married','employed'),
(22,2,2,'single','employed'),
(23,3,3,'married','employed'),
(24,4,4,'single','student'),
(25,5,1,'married','employed');

INSERT INTO adoption_question (adoption_member_id, question_id, answer)
SELECT id, 1, '有飼養經驗，家中環境適合' FROM adoption_member WHERE case_id = 21 AND member_id = 1;
INSERT INTO adoption_question (adoption_member_id, question_id, answer)
SELECT id, 2, '已準備圍欄與適當空間' FROM adoption_member WHERE case_id = 21 AND member_id = 1;

INSERT INTO adoption_question (adoption_member_id, question_id, answer)
SELECT id, 1, '第一次養貓，已閱讀領養規定' FROM adoption_member WHERE case_id = 22 AND member_id = 2;
INSERT INTO adoption_question (adoption_member_id, question_id, answer)
SELECT id, 3, '會提供穩定陪伴與訓練' FROM adoption_member WHERE case_id = 22 AND member_id = 2;

INSERT INTO adoption_question (adoption_member_id, question_id, answer)
SELECT id, 1, '有多年照顧經驗，家人無反對' FROM adoption_member WHERE case_id = 23 AND member_id = 3;
INSERT INTO adoption_question (adoption_member_id, question_id, answer)
SELECT id, 4, '可配合家訪與領養後追蹤' FROM adoption_member WHERE case_id = 23 AND member_id = 3;

INSERT INTO adoption_question (adoption_member_id, question_id, answer)
SELECT id, 2, '套房已佈置為貓咪友善環境' FROM adoption_member WHERE case_id = 24 AND member_id = 4;
INSERT INTO adoption_question (adoption_member_id, question_id, answer)
SELECT id, 3, '會將新成員視為家庭一員' FROM adoption_member WHERE case_id = 24 AND member_id = 4;

INSERT INTO adoption_question (adoption_member_id, question_id, answer)
SELECT id, 1, '可負擔醫療與生活費用' FROM adoption_member WHERE case_id = 25 AND member_id = 5;
INSERT INTO adoption_question (adoption_member_id, question_id, answer)
SELECT id, 4, '同意配合領養前家訪' FROM adoption_member WHERE case_id = 25 AND member_id = 5;

-- 結束：清潔、完整的測試資料集（60 筆案件 + 支援與 5 筆領養申請）
