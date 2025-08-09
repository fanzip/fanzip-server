-- Schema for Fan.zip Project
-- Using MySQL InnoDB, utf8mb4

-- 1. users -- 수정완료
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
                       UNIQUE KEY uq_social_user(social_type,social_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. influencers -- 수정 완료
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

-- 4. membership_grades  -- 수정 완료
CREATE TABLE membership_grades (
                                   grade_id INT PRIMARY KEY,
                                   grade_name VARCHAR(100),
                                   color VARCHAR(50),
                                   benefits_description TEXT,
                                   monthly_amount DECIMAL(10, 2),
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. memberships -- 수정완료
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

-- 7. fan_cards
CREATE TABLE fan_cards (
                           card_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           membership_id BIGINT NOT NULL,
                           card_number VARCHAR(100) NOT NULL UNIQUE,
                           card_design_url VARCHAR(255),
                           is_active BOOLEAN DEFAULT TRUE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (membership_id) REFERENCES memberships(membership_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. fan_meetings --- 수정완료
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
                              posterImageUrl VARCHAR(500),
FOREIGN KEY (influencer_id) REFERENCES influencers(influencer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. fan_meeting_seats --- 수정완료
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

-- 10. fan_meeting_reservations --- 수정완료
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

-- 11. products
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
                          FOREIGN KEY (influencer_id) REFERENCES influencers(influencer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. cart
CREATE TABLE cart (
                      cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      user_id BIGINT NOT NULL UNIQUE,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. cart_items
CREATE TABLE cart_items (
                            cart_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            cart_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            quantity INT,
                            added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            is_selected BOOLEAN DEFAULT TRUE,
                            FOREIGN KEY (cart_id) REFERENCES cart(cart_id),
                            FOREIGN KEY (product_id) REFERENCES products(product_id)
--                             FOREIGN KEY (product_option_id) REFERENCES ProductOptions(option_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. orders
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

-- 16. order_items
CREATE TABLE order_items (
                             order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             order_id BIGINT NOT NULL,
                             cart_item_id BIGINT,
                             influencer_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             quantity INT,
                             unit_price DECIMAL(10,2),
                             discount_rate DECIMAL(5,2),
                             final_price DECIMAL(10,2),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (order_id) REFERENCES orders(order_id),
                             FOREIGN KEY (product_id) REFERENCES products(product_id),
                             FOREIGN KEY (cart_item_id) REFERENCES cart_items(cart_item_id),
                             FOREIGN KEY (influencer_id) REFERENCES influencers(influencer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 17. payments -- 수정완료
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
                          settlement_date DATE NULL,   -- 정산 대상 일자 (paid_at의 날짜 부분)
                          last_settlement_at TIMESTAMP NULL,
                          FOREIGN KEY (order_id) REFERENCES orders(order_id),
                          FOREIGN KEY (reservation_id) REFERENCES fan_meeting_reservations(reservation_id),
                          FOREIGN KEY (membership_id) REFERENCES memberships(membership_id),
                          FOREIGN KEY (influencer_id) REFERENCES influencers (influencer_id),
                          INDEX idx_settlement_target (settlement_status, settlement_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 18. pg_transactions
CREATE TABLE pg_transactions (
                                 pg_transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 transaction_id VARCHAR(255) NOT NULL UNIQUE,
                                 amount DECIMAL(10,2) NOT NULL,
                                 fee DECIMAL(10,2) DEFAULT 0,
                                 net_amount DECIMAL(10,2) NOT NULL,
                                 status VARCHAR(50) NOT NULL,
                                 settlement_date DATE NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 INDEX idx_transaction_id (transaction_id),
                                 INDEX idx_settlement_date (settlement_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 19. settlements
CREATE TABLE settlements (
                             settlement_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             payment_id BIGINT NOT NULL,
                             influencer_id BIGINT NOT NULL,
                             transaction_id VARCHAR(255),
                             payment_type VARCHAR(50),
                             payment_amount DECIMAL(10,2) NOT NULL,
                             pg_amount DECIMAL(10,2),
                             fee DECIMAL(10,2) DEFAULT 0,
                             settlement_amount DECIMAL(10,2),
                             status VARCHAR(20) NOT NULL,
                             settlement_date DATE NOT NULL,
                             remarks TEXT,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (payment_id) REFERENCES payments(payment_id),
                             INDEX idx_settlement_date (settlement_date),
                             INDEX idx_payment_id (payment_id),
                             INDEX idx_influencer_id (influencer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


drop table if exists settlements;
drop table if exists pg_transactions;
drop table if exists payments;
drop table if exists order_items;
drop table if exists ordrs;
drop table if exists orders;

drop table if exists cart_items;

drop table if exists cart;
drop table if exists products;

drop table if exists fan_cards;
drop table if exists fan_meeting_reservations;
drop table if exists fan_meeting_seats;
drop table if exists fan_meetings;


drop table if exists membership_grades;
drop table if exists influencers;
drop table if exists memberships;
drop table if exists membership_grades;
drop table if exists users;
drop table if exists settlement;