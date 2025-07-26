-- Schema for Fan.zip Project
-- Using MySQL InnoDB, utf8mb4

-- 1. Users
CREATE TABLE Users (
                       user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       name VARCHAR(100),
                       phone VARCHAR(20),
                       social_type VARCHAR(20),
                       social_id VARCHAR(100),
                       address1 VARCHAR(255),
                       address2 VARCHAR(255),
                       zipcode VARCHAR(20),
                       recipient_name VARCHAR(100),
                       recipient_phone VARCHAR(20),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       deleted_at TIMESTAMP NULL DEFAULT NULL,
                       UNIQUE KEY uq_social_user(social_type,social_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. UserDevices
CREATE TABLE UserDevices (
                             device_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             user_id BIGINT NOT NULL,
                             device_identifier VARCHAR(255) NOT NULL UNIQUE,
                             device_name VARCHAR(100),
                             device_type VARCHAR(50),
                             is_primary BOOLEAN DEFAULT FALSE,
                             is_active BOOLEAN DEFAULT TRUE,
                             registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             last_used_at TIMESTAMP NULL,
                             FOREIGN KEY (user_id) REFERENCES Users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Influencers
CREATE TABLE Influencers (
                             influencer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             user_id BIGINT NOT NULL,
                             influencer_name VARCHAR(255),
                             category VARCHAR(50),
                             description TEXT,
                             profile_image VARCHAR(255),
                             cover_image VARCHAR(255),
                             is_verified BOOLEAN DEFAULT FALSE,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES Users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. MembershipGrades
CREATE TABLE MembershipGrades (
                                  grade_id INT PRIMARY KEY,
                                  grade_name VARCHAR(100),
                                  color VARCHAR(50),
                                  benefits_description TEXT,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Benefits
CREATE TABLE Benefits (
                          benefit_id INT PRIMARY KEY AUTO_INCREMENT,
                          grade_id INT NOT NULL,
                          benefit_type VARCHAR(50),
                          benefit_name VARCHAR(100),
                          benefit_value VARCHAR(100),
                          description TEXT,
                          is_active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (grade_id) REFERENCES MembershipGrades(grade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Memberships
CREATE TABLE Memberships (
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
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES Users(user_id),
                             FOREIGN KEY (influencer_id) REFERENCES Influencers(influencer_id),
                             FOREIGN KEY (grade_id) REFERENCES MembershipGrades(grade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. FanCards
CREATE TABLE FanCards (
                          card_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          membership_id BIGINT NOT NULL,
                          card_number VARCHAR(100) NOT NULL UNIQUE,
                          qr_code VARCHAR(255) NOT NULL UNIQUE,
                          issue_date DATE,
                          expiry_date DATE,
                          card_design_url VARCHAR(255),
                          is_active BOOLEAN DEFAULT TRUE,
                          registered_device_id BIGINT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (membership_id) REFERENCES Memberships(membership_id),
                          FOREIGN KEY (registered_device_id) REFERENCES UserDevices(device_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. FanMeetings
CREATE TABLE FanMeetings (
                             meeting_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             influencer_id BIGINT NOT NULL,
                             title VARCHAR(255),
                             description TEXT,
                             venue_name VARCHAR(255),
                             venue_address VARCHAR(255),
                             meeting_date DATETIME,
                             total_seats INT,
                             available_seats INT,
                             silver_open_time DATETIME,
                             gold_open_time DATETIME,
                             platinum_open_time DATETIME,
                             vip_open_time DATETIME,
                             general_open_time DATETIME,
                             status VARCHAR(50),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             FOREIGN KEY (influencer_id) REFERENCES Influencers(influencer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. FanMeetingSeats
CREATE TABLE FanMeetingSeats (
                                 seat_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 meeting_id BIGINT NOT NULL,
                                 seat_number VARCHAR(50),
                                 seat_type VARCHAR(50),
                                 price DECIMAL(10,2),
                                 is_reserved BOOLEAN DEFAULT FALSE,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (meeting_id) REFERENCES FanMeetings(meeting_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. FanMeetingReservations
CREATE TABLE FanMeetingReservations (
                                        reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        meeting_id BIGINT NOT NULL,
                                        user_id BIGINT NOT NULL,
                                        seat_id BIGINT NOT NULL,
                                        reservation_number VARCHAR(100) NOT NULL UNIQUE,
                                        qr_code VARCHAR(255) NOT NULL UNIQUE,
                                        status VARCHAR(50),
                                        verified_device_id BIGINT,
                                        reserved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        cancelled_at TIMESTAMP NULL,
                                        used_at TIMESTAMP NULL,
                                        FOREIGN KEY (meeting_id) REFERENCES FanMeetings(meeting_id),
                                        FOREIGN KEY (user_id) REFERENCES Users(user_id),
                                        FOREIGN KEY (seat_id) REFERENCES FanMeetingSeats(seat_id),
                                        FOREIGN KEY (verified_device_id) REFERENCES UserDevices(device_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. Products
CREATE TABLE Products (
                          product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          influencer_id BIGINT NOT NULL,
                          name VARCHAR(255),
                          description TEXT,
                          price DECIMAL(10,2),
                          group_buy_price DECIMAL(10,2),
                          discounted_price DECIMAL(10,2),
                          discount_rate DECIMAL(5,4) AS ((price - discounted_price) / price) VIRTUAL,
                          shipping_price DECIMAL(10,2),
                          min_quantity INT,
                          max_quantity INT,
                          current_quantity INT,
                          stock INT,
                          thumbnail_image VARCHAR(255),
                          detail_images JSON,
                          description_images JSON,
                          white_open_time DATETIME,
                          silver_open_time DATETIME,
                          gold_open_time DATETIME,
                          vip_open_time DATETIME,
                          general_open_time DATETIME,
                          white_discount_rate DECIMAL(5,2),
                          silver_discount_rate DECIMAL(5,2),
                          gold_discount_rate DECIMAL(5,2),
                          vip_discount_rate DECIMAL(5,2),
                          status VARCHAR(50),
                          group_buy_end_date DATETIME,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (influencer_id) REFERENCES Influencers(influencer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. ProductOptions
CREATE TABLE ProductOptions (
                                option_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                product_id BIGINT NOT NULL,
                                option_name_1 VARCHAR(255),
                                option_name_2 VARCHAR(255),
                                stock INT,
                                is_active BOOLEAN DEFAULT TRUE,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (product_id) REFERENCES Products(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. Cart
CREATE TABLE Cart (
                      cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      user_id BIGINT NOT NULL UNIQUE,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      FOREIGN KEY (user_id) REFERENCES Users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. CartItems
CREATE TABLE CartItems (
                           cart_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           cart_id BIGINT NOT NULL,
                           product_id BIGINT NOT NULL,
                           product_option_id BIGINT,
                           quantity INT,
                           added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           is_selected BOOLEAN DEFAULT TRUE,
                           FOREIGN KEY (cart_id) REFERENCES Cart(cart_id),
                           FOREIGN KEY (product_id) REFERENCES Products(product_id),
                           FOREIGN KEY (product_option_id) REFERENCES ProductOptions(option_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. Orders
CREATE TABLE Orders (
                        order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        user_id BIGINT NOT NULL,
                        order_number VARCHAR(100) NOT NULL UNIQUE,
                        total_amount DECIMAL(10,2),
                        discount_amount DECIMAL(10,2),
                        final_amount DECIMAL(10,2),
                        status VARCHAR(50),
                        recipient_name VARCHAR(100),
                        recipient_phone VARCHAR(20),
                        shipping_address1 VARCHAR(255),
                        shipping_address2 VARCHAR(255),
                        zipcode VARCHAR(20),
                        shipping_memo TEXT,
                        ordered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        paid_at TIMESTAMP NULL,
                        shipped_at TIMESTAMP NULL,
                        delivered_at TIMESTAMP NULL,
                        cancelled_at TIMESTAMP NULL,
                        FOREIGN KEY (user_id) REFERENCES Users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 16. OrderItems
CREATE TABLE OrderItems (
                            order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            order_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            product_option_id BIGINT,
                            quantity INT,
                            unit_price DECIMAL(10,2),
                            discount_rate DECIMAL(5,2),
                            final_price DECIMAL(10,2),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (order_id) REFERENCES Orders(order_id),
                            FOREIGN KEY (product_id) REFERENCES Products(product_id),
                            FOREIGN KEY (product_option_id) REFERENCES ProductOptions(option_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 17. Payments
CREATE TABLE Payments (
                          payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          user_id BIGINT,
                          order_id BIGINT,
                          reservation_id BIGINT,
                          membership_id BIGINT,
                          payment_type VARCHAR(50),
                          payment_method VARCHAR(50),
                          amount DECIMAL(10,2),
                          status VARCHAR(50),
                          transaction_id VARCHAR(255),
                          paid_at TIMESTAMP NULL,
                          cancelled_at TIMESTAMP NULL,
                          refunded_at TIMESTAMP NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (order_id) REFERENCES Orders(order_id),
                          FOREIGN KEY (reservation_id) REFERENCES FanMeetingReservations(reservation_id),
                          FOREIGN KEY (membership_id) REFERENCES Memberships(membership_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
