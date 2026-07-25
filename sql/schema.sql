DROP DATABASE IF EXISTS discord_server;
CREATE DATABASE IF NOT EXISTS discord_server;
USE discord_server;



-- User 테이블 생성
CREATE TABLE User (
                      id BIGINT PRIMARY KEY ,
                      nickname VARCHAR(10) NOT NULL UNIQUE,
                      password VARCHAR(20),
                      email VARCHAR(20) NOT NULL UNIQUE,
                      image_url VARCHAR(255),
                      role ENUM('USER', 'ADMIN') DEFAULT 'USER',
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Friend 테이블 생성
CREATE TABLE Friend (
                        id BIGINT PRIMARY KEY ,
                        from_user_id BIGINT NOT NULL,
                        to_user_id BIGINT NOT NULL,
                        status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (from_user_id) REFERENCES User(id),
                        FOREIGN KEY (to_user_id) REFERENCES User(id)
);

-- Server 테이블 생성
CREATE TABLE Server (
                        id BIGINT PRIMARY KEY ,
                        name VARCHAR(20) NOT NULL,
                        image_url TEXT,
                        host_id BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (host_id) REFERENCES User(id)
);

-- Server_User 테이블 생성 (서버 내 사용자 관계)
CREATE TABLE Server_User (
                             id BIGINT PRIMARY KEY ,
                             server_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             alarm boolean NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (server_id) REFERENCES Server(id) ON DELETE CASCADE,
                             FOREIGN KEY (user_id) REFERENCES User(id)
);

-- Channel 테이블 생성
CREATE TABLE Channel (
                         id BIGINT PRIMARY KEY,
                         server_id BIGINT NOT NULL,
                         creator_id BIGINT NOT NULL,
                         name VARCHAR(20) NOT NULL,
                         type ENUM('CHAT', 'VOICE') NOT NULL DEFAULT 'CHAT',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (server_id) REFERENCES Server(id),
                         FOREIGN KEY (creator_id) REFERENCES User(id)
);



-- Message 테이블 생성
CREATE TABLE message (
                         id BIGINT PRIMARY KEY ,
                         content VARCHAR(50),
                         channel_id BIGINT NOT NULL,
                         user_id BIGINT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (channel_id) REFERENCES channel(id) ON DELETE CASCADE,
                         FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE dm (
                    id BIGINT PRIMARY KEY ,
                    is_visible BOOLEAN NOT NULL DEFAULT TRUE,
                    user1_id BIGINT NOT NULL,
                    user2_id BIGINT NOT NULL,
                    user1_last_read_at TIMESTAMP NULL,
                    user2_last_read_at TIMESTAMP NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user1_id) REFERENCES user(id),
                    FOREIGN KEY (user2_id) REFERENCES user(id)
);

CREATE TABLE dm_message (
                            id BIGINT PRIMARY KEY ,
                            content VARCHAR(50),
                            dm_id BIGINT NOT NULL,
                            user_id BIGINT NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (dm_id) REFERENCES dm(id),
                            FOREIGN KEY (user_id) REFERENCES user(id)
);


CREATE TABLE Server_Invite (
                               id BIGINT PRIMARY KEY,
                               server_id BIGINT NOT NULL,
                               from_user_id BIGINT NOT NULL,
                               to_user_id BIGINT NOT NULL,
                               status ENUM('PENDING', 'ACCEPTED', 'DECLINED') DEFAULT 'PENDING',
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               FOREIGN KEY (server_id) REFERENCES Server(id),
                               FOREIGN KEY (from_user_id) REFERENCES User(id),
                               FOREIGN KEY (to_user_id) REFERENCES User(id)
);


CREATE TABLE nicknames (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nickname VARCHAR(50) NOT NULL UNIQUE,
                           is_used BOOLEAN DEFAULT FALSE
);

CREATE TABLE notification (
                              id BIGINT PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              type VARCHAR(30) NOT NULL,
                              payload TEXT NOT NULL,
                              is_read BOOLEAN NOT NULL DEFAULT FALSE,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES User(id)
);


INSERT INTO nicknames (nickname, is_used) VALUES
                                              ('칠링도치', false), ('민초깡패', false), ('알잘딱갈팁', false), ('플렉스짱구', false), ('킹받는햄찌', false),
                                              ('텐션업곰돌', false), ('무지성찐빵', false), ('선넘는망고', false), ('존버토끼', false), ('오마이갓뚜기', false),
                                              ('갓생사는자두', false), ('심쿵도리', false), ('회전초밥요정', false), ('감성버스정류장', false), ('별다줄냥이', false),
                                              ('폰꾸의신', false), ('반반머리단발좌', false), ('뇌절하지마', false), ('여친있찐남', false), ('얼죽아백곰', false),
                                              ('맘편한찐친', false), ('혼코노장인', false), ('백허그너구리', false), ('주접폭발러', false), ('크크루삥뽕', false),
                                              ('스밍요정', false), ('네버스탑감성', false), ('초코우유보이', false), ('공주님아님주의', false), ('이모티콘수집러', false);