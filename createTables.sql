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


DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS user_push_token;
DROP TABLE IF EXISTS settlements;
DROP TABLE IF EXISTS pg_transactions;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS fan_meeting_reservations;
DROP TABLE IF EXISTS fan_meeting_seats;
DROP TABLE IF EXISTS fan_meetings;
DROP TABLE IF EXISTS memberships;
DROP TABLE IF EXISTS membership_grades;
DROP TABLE IF EXISTS influencers;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS feedbacks;