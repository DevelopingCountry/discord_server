use discord_server;

# INSERT INTO User (id,nickname, password, email, role)
# VALUES
#     (UUID_TO_BIN(UUID()),'admin', 'adminpass', 'admin@example.com','USER');
#


INSERT INTO server (id,name, image_url, host_id)
VALUES

    ( UUID_TO_BIN(UUID()),'server1', 'server1_image_url',UUID_TO_BIN('2616f85c-287e-4810-9cc6-c201217ed94d'))
;
# server의 host_id는 테스트하려면 User먼저 만들고 그 user의 uuid 복사해서 UUID_TO_BIN()에 넣어서 만들어보세요

INSERT INTO channel(id,server_id,creator_id,name,type)
    values (UUID_TO_BIN(UUID()),UUID_TO_BIN('57d7f838-18ef-11f0-a6da-3544b8cddce4'),UUID_TO_BIN('2616f85c-287e-4810-9cc6-c201217ed94d'),'천우희방','DM');


