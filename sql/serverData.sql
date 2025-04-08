use discord_server;

INSERT INTO User (nickname, password, email, role)
VALUES
    ('admin', 'adminpass', 'admin@example.com','USER');



INSERT INTO server (id, name, image_url, host_id)
VALUES
    (1, 'server1', 'server1_image_url', 1),
    (2, 'server2', 'server2_image_url', 1),
    (3, 'server3', 'server3_image_url', 1),
    (4, 'server4', 'server4_image_url', 1),
    (5, 'server5', 'server5_image_url', 1);


