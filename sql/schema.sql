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
                            image_url VARCHAR(50),
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
                             id Binary(16) PRIMARY KEY ,
                             content VARCHAR(50),
                             channel_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (channel_id) REFERENCES channel(id) ON DELETE CASCADE,
                             FOREIGN KEY (user_id) REFERENCES user(id)
    );

    CREATE TABLE dm (
                        id BINARY(16) PRIMARY KEY,
                        user1_id BIGINT NOT NULL,
                        user2_id BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user1_id) REFERENCES user(id),
                        FOREIGN KEY (user2_id) REFERENCES user(id)
    );

    CREATE TABLE dm_message (
                                id BINARY(16) PRIMARY KEY,
                                content VARCHAR(50),
                                dm_id BINARY(16) NOT NULL,
                                user_id BIGINT NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (dm_id) REFERENCES dm(id),
                                FOREIGN KEY (user_id) REFERENCES user(id)
    );


