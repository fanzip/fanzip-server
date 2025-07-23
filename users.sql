drop table if exists Users;
create table Users(
    user_id bigint auto_increment PRIMARY KEY,
    email VARCHAR(255) not null unique,
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

)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

select * from Users;
