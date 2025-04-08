use discord_server;

INSERT INTO User (id,nickname, password, email, role)
VALUES
    (UUID_TO_BIN(UUID()),'admin', 'adminpass', 'admin@example.com','USER');



INSERT INTO server (id,name, image_url, host_id)
VALUES
    ( UUID_TO_BIN(UUID()),'server1', 'server1_image_url',UUID_TO_BIN('2ce693c0-1451-11f0-a6da-3544b8cddce4'))
;
# server의 host_id는 테스트하려면 User먼저 만들고 그 user의 uuid 복사해서 UUID_TO_BIN()에 넣어서 만들어보세요