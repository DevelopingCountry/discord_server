use discord_server;

-- 서버 2개 추가 (해당 유저가 host로 소유)
INSERT INTO Server (id, name, image_url, host_id, created_at, updated_at)
VALUES
    (1001, 'Server A', NULL, 567342956374134784, NOW(), NOW()),
    (1002, 'Server B', NULL, 567342956374134784, NOW(), NOW());

-- 서버-유저 관계도 추가 (해당 유저가 두 서버에 포함되어 있도록)
INSERT INTO Server_User (id, server_id, user_id, alarm, created_at, updated_at)
VALUES
    (2001, 1001, 567342956374134784, TRUE, NOW(), NOW()),
    (2002, 1002, 567342956374134784, TRUE, NOW(), NOW());

