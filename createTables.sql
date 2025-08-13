-- Schema for Fan.zip Project
-- Using MySQL InnoDB, utf8mb4

-- 1. users
CREATE TABLE users (
                       user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       name VARCHAR(100),
                       phone VARCHAR(20),
                       role VARCHAR(20) DEFAULT 'USER',
                       social_type VARCHAR(20),
                       social_id VARCHAR(100),
                       address1 VARCHAR(255),
                       address2 VARCHAR(255),
                       zipcode VARCHAR(20),
                       recipient_phone VARCHAR(20),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       deleted_at TIMESTAMP NULL DEFAULT NULL,
                       UNIQUE KEY uq_social_user (social_type, social_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. influencers
CREATE TABLE influencers (
                             influencer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             user_id BIGINT NOT NULL,
                             influencer_name VARCHAR(255),
                             influencer_image VARCHAR(255),
                             category VARCHAR(50),
                             description TEXT,
                             profile_image VARCHAR(255),
                             fancard_image VARCHAR(255),
                             is_verified BOOLEAN DEFAULT FALSE,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. membership_grades
CREATE TABLE membership_grades (
                                   grade_id INT PRIMARY KEY,
                                   grade_name VARCHAR(100),
                                   color VARCHAR(50),
                                   benefits_description TEXT,
                                   monthly_amount DECIMAL(10, 2),
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. memberships
CREATE TABLE memberships (
                             membership_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             user_id BIGINT NOT NULL,
                             influencer_id BIGINT NOT NULL,
                             grade_id INT NOT NULL,
                             subscription_start DATE,
                             subscription_end DATE,
                             monthly_amount DECIMAL(10,2),
                             total_paid_amount DECIMAL(10,2),
                             status VARCHAR(50),
                             auto_renewal BOOLEAN DEFAULT FALSE,
                             FOREIGN KEY (user_id) REFERENCES users(user_id),
                             FOREIGN KEY (influencer_id) REFERENCES influencers(influencer_id),
                             FOREIGN KEY (grade_id) REFERENCES membership_grades(grade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. fan_cards
CREATE TABLE fan_cards (
                           card_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           membership_id BIGINT NOT NULL,
                           card_number VARCHAR(100) NOT NULL UNIQUE,
                           card_design_url VARCHAR(255),
                           is_active BOOLEAN DEFAULT TRUE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (membership_id) REFERENCES memberships(membership_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. fan_meetings
CREATE TABLE fan_meetings (
                              meeting_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              influencer_id BIGINT NOT NULL,
                              title VARCHAR(255),
                              description TEXT,
                              venue_name VARCHAR(255),
                              venue_address VARCHAR(255),
                              meeting_date DATETIME,
                              total_seats INT,
                              available_seats INT,
                              white_open_time DATETIME,
                              silver_open_time DATETIME,
                              gold_open_time DATETIME,
                              vip_open_time DATETIME,
                              general_open_time DATETIME,
                              status VARCHAR(50),
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              poster_image_url VARCHAR(500),
                              FOREIGN KEY (influencer_id) REFERENCES influencers(influencer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. fan_meeting_seats
CREATE TABLE fan_meeting_seats (
                                   seat_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   meeting_id BIGINT NOT NULL,
                                   seat_number VARCHAR(50),
                                   price DECIMAL(10,2),
                                   reserved BOOLEAN DEFAULT FALSE,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   version INT DEFAULT 0,
                                   FOREIGN KEY (meeting_id) REFERENCES fan_meetings(meeting_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. fan_meeting_reservations
CREATE TABLE fan_meeting_reservations (
                                          reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          meeting_id BIGINT NOT NULL,
                                          influencer_id BIGINT NOT NULL,
                                          user_id BIGINT NOT NULL,
                                          seat_id BIGINT NOT NULL,
                                          reservation_number VARCHAR(100) NOT NULL UNIQUE,
                                          status VARCHAR(50),
                                          reserved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          cancelled_at TIMESTAMP NULL,
                                          used_at TIMESTAMP NULL,
                                          FOREIGN KEY (meeting_id) REFERENCES fan_meetings(meeting_id),
                                          FOREIGN KEY (user_id) REFERENCES users(user_id),
                                          FOREIGN KEY (seat_id) REFERENCES fan_meeting_seats(seat_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. products
CREATE TABLE products (
                          product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          influencer_id BIGINT NOT NULL,
                          name VARCHAR(255),
                          description TEXT,
                          price DECIMAL(10,2),
                          group_buy_price DECIMAL(10,2),
                          discounted_price DECIMAL(10,2),
                          discount_rate DECIMAL(5,4) AS ((price - discounted_price) / price) VIRTUAL,
                          shipping_price DECIMAL(10,2),
                          stock INT,
                          thumbnail_image VARCHAR(255),
                          detail_images JSON,
                          description_images JSON,
                          white_open_time DATETIME,
                          silver_open_time DATETIME,
                          gold_open_time DATETIME,
                          vip_open_time DATETIME,
                          general_open_time DATETIME,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          categories JSON,
                          FOREIGN KEY (influencer_id) REFERENCES influencers(influencer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. cart
CREATE TABLE cart (
                      cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      user_id BIGINT NOT NULL UNIQUE,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. cart_items
CREATE TABLE cart_items (
                            cart_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            cart_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            quantity INT,
                            added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            is_selected BOOLEAN DEFAULT TRUE,
                            FOREIGN KEY (cart_id) REFERENCES cart(cart_id),
                            FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. orders
CREATE TABLE orders (
                        order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        user_id BIGINT NOT NULL,
                        final_amount DECIMAL(10,2),
                        status VARCHAR(50),
                        recipient_name VARCHAR(100),
                        recipient_phone VARCHAR(20),
                        shipping_address1 VARCHAR(255),
                        shipping_address2 VARCHAR(255),
                        zipcode VARCHAR(20),
                        ordered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        paid_at TIMESTAMP NULL,
                        delivered_at TIMESTAMP NULL,
                        cancelled_at TIMESTAMP NULL,
                        FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. order_items
CREATE TABLE order_items (
                             order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             order_id BIGINT NOT NULL,
                             cart_item_id BIGINT,
                             influencer_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             quantity INT,
                             unit_price DECIMAL(10,2),
                             shipping_price DECIMAL(10,2),
                             final_price DECIMAL(10,2),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (order_id) REFERENCES orders(order_id),
                             FOREIGN KEY (product_id) REFERENCES products(product_id),
                             FOREIGN KEY (cart_item_id) REFERENCES cart_items(cart_item_id),
                             FOREIGN KEY (influencer_id) REFERENCES influencers(influencer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. payments
CREATE TABLE payments (
                          payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          user_id BIGINT,
                          order_id BIGINT,
                          reservation_id BIGINT,
                          membership_id BIGINT,
                          influencer_id BIGINT,
                          payment_type VARCHAR(50),
                          payment_method VARCHAR(50),
                          amount DECIMAL(10,2),
                          status VARCHAR(50),
                          transaction_id VARCHAR(255),
                          paid_at TIMESTAMP NULL,
                          cancelled_at TIMESTAMP NULL,
                          refunded_at TIMESTAMP NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          settlement_status VARCHAR(20) DEFAULT 'PENDING',
                          settlement_date DATE NULL,
                          last_settlement_at TIMESTAMP NULL,
                          FOREIGN KEY (order_id) REFERENCES orders(order_id),
                          FOREIGN KEY (reservation_id) REFERENCES fan_meeting_reservations(reservation_id),
                          FOREIGN KEY (membership_id) REFERENCES memberships(membership_id),
                          FOREIGN KEY (influencer_id) REFERENCES influencers(influencer_id),
                          INDEX idx_settlement_target (settlement_status, settlement_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 17. user_push_token (NEW)
CREATE TABLE IF NOT EXISTS user_push_token (
                                               token_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               user_id BIGINT NOT NULL,
                                               push_token VARCHAR(512) NOT NULL,
                                               device_type VARCHAR(50) NOT NULL DEFAULT 'WEB', -- android / ios / web
                                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                               FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                               CONSTRAINT uq_user_device UNIQUE (user_id, device_type),
                                               CONSTRAINT uq_push_token UNIQUE (push_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 18. notifications (NEW)
CREATE TABLE IF NOT EXISTS notifications (
                                             notification_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             influencer_id   BIGINT NOT NULL,
                                             title           VARCHAR(255) NOT NULL,
                                             message         TEXT NOT NULL,
                                             target_url      VARCHAR(1000) DEFAULT NULL,
                                             created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                             FOREIGN KEY (influencer_id) REFERENCES influencers(influencer_id) ON DELETE CASCADE,
                                             INDEX idx_notif_influencer (influencer_id),
                                             INDEX idx_notif_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 19. feedbacks
CREATE TABLE feedbacks (
                           feedback_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '피드백 고유 ID',
                           user_id       BIGINT NOT NULL COMMENT 'FK users.user_id',
                           influencer_id BIGINT NOT NULL COMMENT 'FK influencers.influencer_id',
                           context_id    BIGINT NULL COMMENT 'membership_id, order_id, reservation_id 등',
                           rating        TINYINT NOT NULL COMMENT '만족도 (1~5)',
                           comment       TEXT NULL,
                           user_agent    VARCHAR(255) NULL,
                           page_path     VARCHAR(255) NULL,
                           is_public     BOOLEAN DEFAULT FALSE,
                           created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES users(user_id),
                           FOREIGN KEY (influencer_id) REFERENCES influencers(influencer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX ix_fb_infl_created ON feedbacks(influencer_id, created_at);
CREATE INDEX ix_fb_user_created ON feedbacks(user_id, created_at);
CREATE INDEX ix_fb_context_id   ON feedbacks(context_id);



-- 1) 인플루언서 더미 유저 선삽입 (user_id를 influencers와 맞춤)
INSERT INTO users
(user_id, email, name, phone, role, social_type, social_id, address1, address2, zipcode, recipient_phone)
VALUES
    (1,  'influencer001@fanzip.local', '침착맨',   '010-0000-0001', 'INFLUENCER', 'SEED', 'seed-0001', NULL, NULL, NULL, NULL),
    (2,  'influencer002@fanzip.local', '김승원빈', '010-0000-0002', 'INFLUENCER', 'SEED', 'seed-0002', NULL, NULL, NULL, NULL),
    (3,  'influencer003@fanzip.local', '안성재',   '010-0000-0003', 'INFLUENCER', 'SEED', 'seed-0003', NULL, NULL, NULL, NULL),
    (4,  'influencer004@fanzip.local', '여단오',   '010-0000-0004', 'INFLUENCER', 'SEED', 'seed-0004', NULL, NULL, NULL, NULL),
    (5,  'influencer005@fanzip.local', '레오제이', '010-0000-0005', 'INFLUENCER', 'SEED', 'seed-0005', NULL, NULL, NULL, NULL),
    (6,  'influencer006@fanzip.local', '속삭이는 몽자', '010-0000-0006', 'INFLUENCER', 'SEED', 'seed-0006', NULL, NULL, NULL, NULL),
    (7,  'influencer007@fanzip.local', '심으뜸',   '010-0000-0007', 'INFLUENCER', 'SEED', 'seed-0007', NULL, NULL, NULL, NULL),
    (8,  'influencer008@fanzip.local', '빠니보틀', '010-0000-0008', 'INFLUENCER', 'SEED', 'seed-0008', NULL, NULL, NULL, NULL),
    (9,  'influencer009@fanzip.local', '혜안',     '010-0000-0009', 'INFLUENCER', 'SEED', 'seed-0009', NULL, NULL, NULL, NULL),
    (10, 'influencer010@fanzip.local', '궤도',     '010-0000-0010', 'INFLUENCER', 'SEED', 'seed-0010', NULL, NULL, NULL, NULL),
    (11, 'influencer011@fanzip.local', '태요미네', '010-0000-0011', 'INFLUENCER', 'SEED', 'seed-0011', NULL, NULL, NULL, NULL),
    (12, 'influencer012@fanzip.local', '김종국',   '010-0000-0012', 'INFLUENCER', 'SEED', 'seed-0012', NULL, NULL, NULL, NULL),
    (13, 'influencer013@fanzip.local', '올리버쌤', '010-0000-0013', 'INFLUENCER', 'SEED', 'seed-0013', NULL, NULL, NULL, NULL);



INSERT INTO influencers (
    user_id,
    influencer_id,
    influencer_name,
    category,
    description,
    profile_image,
    fancard_image,
    is_verified
) VALUES
      (1, 1, '침착맨', 'DAILY', '안녕하세요, 침착맨입니다. 일상 이야기와 다양한 콘텐츠를 전하는 유튜버입니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/d4e62e73-7853-48d2-9156-95af8743324f.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/ChimChakMan.png', 1),
      (2, 2, '김승원빈', 'FASHION', '패션과 스타일링 팁을 공유하는 김승원빈입니다. 최신 트렌드와 코디를 소개합니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/0b2d03bc-18f0-4692-bc23-de7f4edca4cd.png', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/WonBean.png', 1),
      (3, 3, '안성재', 'COOKING', '맛있는 집밥과 특별한 레시피를 알려주는 안성재입니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/86193442-30c2-434f-8384-184070a45948.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/AnSungJae.png', 1),
      (4, 4, '여단오', 'ETC', '다양한 주제와 일상 브이로그를 전하는 여단오입니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/a72fb485-e5e1-43da-adc5-218f075e18bc.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/YeoDanO.png', 1),
      (5, 5, '레오제이', 'BEAUTY', '메이크업과 뷰티 팁을 전문적으로 알려주는 레오제이입니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/604fb1de-a856-4764-8286-75b086145358.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/LeoJ.png', 1),
      (6, 6, '속삭이는 몽자', 'PET', '귀여운 반려동물 몽자와의 일상을 담은 채널입니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/0c8d6d20-ffad-4817-93ea-33a8dac71d5e.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/WhisperingMongJa.png', 1),
      (7, 7, '심으뜸', 'HEALTH', '건강과 운동 루틴, 홈트레이닝 팁을 전하는 심으뜸입니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/e3d8f60b-808d-4186-96ef-8b9ec609699c.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/ShimEuDdem.png', 1),
      (8, 8, '빠니보틀', 'TRAVEL', '세계 곳곳을 여행하며 새로운 문화를 소개하는 빼니보틀입니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/6509fa56-ab11-47f8-80d2-f66d61634fde.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/BbanniBottle.png', 1),
      (9, 9, '혜안', 'GAME', '다양한 게임 플레이와 리뷰를 전하는 혜안입니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/2a63ee9f-a2ed-4ff8-84e8-73d6507d2540.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/HyeAn.png', 1),
      (10, 10, '궤도', 'EDUCATION', '과학과 우주, 흥미로운 지식을 쉽고 재미있게 설명하는 궤도입니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/e14524ff-21f8-4eaf-a175-103f74b186fd.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/GyeDo.png', 1),
      (11, 11, '태요미네', 'KIDS', '아이들과 함께 즐길 수 있는 교육적이고 재미있는 콘텐츠를 전합니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/4b415657-36b4-4662-aa6f-b1f44e349487.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/TaeYoMiNae.png', 1),
      (12, 12, '김종국', 'FITNESS', '운동과 건강 관리, 체력 향상 팁을 알려주는 김종국입니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/33852516-1184-4b28-8082-4af912c0d031.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/KimJongGuk.png', 1),
      (13, 13, '올리버쌤', 'LANGUAGE', '영어와 문화 이야기를 쉽고 재미있게 알려주는 올리버쌤입니다.', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/influencer_profile/0731ac95-fe24-4ec1-bf00-3a71ec545e5f.jpg', 'https://fanzip.s3.ap-northeast-2.amazonaws.com/fancard_image/OliverTeacher.png', 1);


INSERT INTO membership_grades
(grade_id, grade_name, color, benefits_description, monthly_amount, created_at)
VALUES
(1, 'WHITE',  '#EFEFEF', '상시 5% 할인',                          7900.00, NOW()),
    ( 2,'SILVER', '#D9D9D9', '상시 5% 할인 + 1시간 선오픈',           9900.00, NOW()),
    ( 3,'GOLD',   '#FFD633', '상시 10% 할인 + 2시간 선오픈',         11900.00, NOW()),
    ( 4,'VIP',    '#000000', '상시 20% 할인 + 3시간 선오픈 + 무료', 13900.00, NOW());



-- QR 검증 테스트용 더미 데이터 추가

-- 팬미팅 더미 데이터
INSERT INTO fan_meetings (meeting_id, influencer_id, title, description, venue_name, venue_address, meeting_date, total_seats, available_seats, white_open_time, silver_open_time, gold_open_time, vip_open_time, general_open_time, status, poster_image_url)
VALUES 
(1, 1, '침착맨 팬미팅 2025', '침착맨과 함께하는 특별한 시간', '올림픽공원 체조경기장', '서울특별시 송파구 올림픽로 424', '2025-12-25 19:00:00', 100, 50, 
'2025-12-20 10:00:00', '2025-12-20 11:00:00', '2025-12-20 12:00:00', '2025-12-20 13:00:00', '2025-12-20 14:00:00', 'ACTIVE', 
'https://fanzip.s3.ap-northeast-2.amazonaws.com/poster/chimchakman_fanmeeting.jpg');

-- 좌석 더미 데이터
INSERT INTO fan_meeting_seats (seat_id, meeting_id, seat_number, price, reserved)
VALUES 
(1, 1, 'A-1', 50000.00, true),
(2, 1, 'A-2', 50000.00, false),
(3, 1, 'A-3', 50000.00, false);

-- 예약 더미 데이터 (userId=18, reservationId=1)
INSERT INTO fan_meeting_reservations (reservation_id, meeting_id, influencer_id, user_id, seat_id, reservation_number, status, reserved_at)


VALUES
(1, 1, 1, 18, 1, 'RES20250812001', 'CONFIRMED', NOW());


SET FOREIGN_KEY_CHECKS = 0;

SET @tables = (
    SELECT GROUP_CONCAT(CONCAT('`', table_name, '`'))
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
);

SET @sql = CONCAT('DROP TABLE IF EXISTS ', @tables);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET FOREIGN_KEY_CHECKS = 1;