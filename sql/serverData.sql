use discord_server;

 INSERT INTO User (id,nickname, password, email, role)
 VALUES
     (412312312312312312,'admin', 'adminpass', 'admin@example.com','USER');

insert into Server(id, name, image_url, host_id) values (4123312312312662312,"이소연의 서번", null,412312312312312312);

insert into Channel(id, server_id, creator_id, name,type)  values
                                                               (4123312312312662345,4123312312312662312,412312312312312312,"움성채널",'VOICE')