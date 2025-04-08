use discord_server;

INSERT INTO User (id,nickname, password, email, role)
VALUES
    (UUID_TO_BIN(UUID()),'admin', 'adminpass', 'admin@example.com','USER');



INSERT INTO server (id,name, image_url, host_id)
VALUES
    ( UUID_TO_BIN(UUID()),'server1', 'server1_image_url',UUID_TO_BIN('2ce693c0-1451-11f0-a6da-3544b8cddce4'))
;