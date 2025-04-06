DROP DATABASE IF EXISTS discord_server;
CREATE DATABASE IF NOT EXISTS discord_server;
USE discord_server;

-- User 테이블 생성
CREATE TABLE User (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      nickname VARCHAR(10) NOT NULL UNIQUE,
                      password VARCHAR(20),
                      email VARCHAR(20) NOT NULL UNIQUE,
                      social_id INT,
                      role ENUM('USER', 'ADMIN') DEFAULT 'USER',
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Friend 테이블 생성
CREATE TABLE Friend (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        from_user_id INT NOT NULL,
                        to_user_id INT NOT NULL,
                        status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (from_user_id) REFERENCES User(id),
                        FOREIGN KEY (to_user_id) REFERENCES User(id)
);

-- Server 테이블 생성
CREATE TABLE Server (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(20) NOT NULL,
                        image_url VARCHAR(50),
                        host_id INT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (host_id) REFERENCES User(id)
);

-- Server_User 테이블 생성 (서버 내 사용자 관계)
CREATE TABLE Server_User (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             server_id INT NOT NULL,
                             user_id INT NOT NULL,
                             alarm boolean NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (server_id) REFERENCES Server(id) ON DELETE CASCADE,
                             FOREIGN KEY (user_id) REFERENCES User(id)
);

-- Channel 테이블 생성
CREATE TABLE Channel (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         server_id INT NOT NULL,
                         creator_id INT NOT NULL,
                         name VARCHAR(20) NOT NULL,
                         type ENUM('CHAT', 'VOICE', 'DM') NOT NULL DEFAULT 'DM',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (server_id) REFERENCES Server(id),
                         FOREIGN KEY (creator_id) REFERENCES User(id)
);



-- Message 테이블 생성
CREATE TABLE message (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         content VARCHAR(50),
                         channel_id INT NOT NULL,
                         user_id INT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (channel_id) REFERENCES channel(id) ON DELETE CASCADE,
                         FOREIGN KEY (user_id) REFERENCES user(id)
);

