use discord_server;
INSERT INTO server (id, name, image_url)
VALUES
    (1, 'server1', 'server1_image_url'),
    (2, 'server2', 'server2_image_url'),
    (3, 'server3', 'server3_image_url'),
    (4, 'server4', 'server4_image_url'),
    (5, 'server5', 'server5_image_url');


INSERT INTO server (name, image_url)
VALUES
    ( 'server1', 'server1_image_url'),
    ('server2', 'server2_image_url'),
    ( 'server3', 'server3_image_url'),
    ('server4', 'server4_image_url'),
    ( 'server5', 'server5_image_url');