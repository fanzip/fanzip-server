-- H2 데이터베이스용 테스트 테이블 및 데이터

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    phone VARCHAR(20),
    social_type VARCHAR(20),
    social_id VARCHAR(255),
    address1 VARCHAR(255),
    address2 VARCHAR(255),
    zipcode VARCHAR(10),
    recipient_name VARCHAR(100),
    recipient_phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

-- 인플루언서 테이블
CREATE TABLE IF NOT EXISTS influencers (
    influencer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    influencer_name VARCHAR(100) NOT NULL,
    category VARCHAR(20),
    description TEXT,
    profile_image VARCHAR(500),
    cover_image VARCHAR(500),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 멤버십 등급 테이블
CREATE TABLE IF NOT EXISTS membership_grades (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    grade_name VARCHAR(50) NOT NULL,
    color VARCHAR(7) NOT NULL,
    benefits_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 멤버십 테이블
CREATE TABLE IF NOT EXISTS memberships (
    membership_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    influencer_id BIGINT NOT NULL,
    grade_id INT NOT NULL,
    subscription_start DATE NOT NULL,
    subscription_end DATE NOT NULL,
    monthly_amount DECIMAL(10, 2) NOT NULL,
    total_paid_amount DECIMAL(12, 2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    auto_renewal BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (influencer_id) REFERENCES influencers(influencer_id),
    FOREIGN KEY (grade_id) REFERENCES membership_grades(grade_id)
);

-- 사용자 기기 테이블
CREATE TABLE IF NOT EXISTS user_devices (
    device_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_identifier VARCHAR(255) UNIQUE NOT NULL,
    device_name VARCHAR(100),
    device_type VARCHAR(20),
    is_primary BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 팬카드 테이블
CREATE TABLE IF NOT EXISTS fan_cards (
    card_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    membership_id BIGINT NOT NULL,
    card_number VARCHAR(50) UNIQUE NOT NULL,
    qr_code VARCHAR(500) UNIQUE NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    card_design_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    registered_device_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (membership_id) REFERENCES memberships(membership_id),
    FOREIGN KEY (registered_device_id) REFERENCES user_devices(device_id)
);

-- 테스트 데이터 삽입

-- 멤버십 등급 데이터
INSERT INTO membership_grades (grade_name, color, benefits_description) VALUES
('SILVER', '#C0C0C0', '기본 혜택 제공'),
('GOLD', '#FFD700', '골드 등급 혜택'),
('PLATINUM', '#E5E4E2', '플래티넘 등급 특별 혜택'),
('VIP', '#8B008B', 'VIP 등급 최고 혜택');

-- 테스트용 사용자 데이터
INSERT INTO users (email, name, password, nickname, phone) 
SELECT 'test@example.com', '테스트 사용자', 'password123', 'testuser', '010-1234-5678'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'test@example.com');

INSERT INTO users (email, name, password, nickname, phone) 
SELECT 'influencer@example.com', '인플루언서', 'password123', 'influencer1', '010-9876-5432'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'influencer@example.com');

-- 테스트용 인플루언서 데이터
INSERT INTO influencers (user_id, influencer_name, category, description, is_verified) 
SELECT 2, '테스트 인플루언서', 'BEAUTY', '뷰티 인플루언서입니다.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM influencers WHERE user_id = 2);

-- 테스트용 멤버십 데이터
INSERT INTO memberships (user_id, influencer_id, grade_id, subscription_start, subscription_end, monthly_amount) 
SELECT 1, 1, 4, '2024-01-01', '2024-12-31', 10000.00
WHERE NOT EXISTS (SELECT 1 FROM memberships WHERE user_id = 1 AND influencer_id = 1);

-- 테스트용 사용자 기기 데이터
INSERT INTO user_devices (user_id, device_identifier, device_name, device_type, is_primary) 
SELECT 1, 'test-device-001', 'iPhone 15', 'MOBILE', TRUE
WHERE NOT EXISTS (SELECT 1 FROM user_devices WHERE device_identifier = 'test-device-001');

-- 테스트용 팬카드 데이터
INSERT INTO fan_cards (membership_id, card_number, qr_code, issue_date, expiry_date, card_design_url, registered_device_id) 
SELECT 1, 'FC-2024-000001', 'https://qr.fanzip.com/FC-2024-000001', '2024-01-01', '2024-12-31', 'https://cdn.fanzip.com/cards/design1.jpg', 1
WHERE NOT EXISTS (SELECT 1 FROM fan_cards WHERE card_number = 'FC-2024-000001');