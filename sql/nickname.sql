USE discord_server;

CREATE TABLE nicknames (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nickname VARCHAR(50) UNIQUE NOT NULL,
                           is_used BOOLEAN DEFAULT FALSE
);

INSERT INTO nicknames (nickname, is_used) VALUES
                                              ('칠링도치', false), ('민초깡패', false), ('알잘딱갈팁', false), ('플렉스짱구', false), ('킹받는햄찌', false),
                                              ('텐션업곰돌', false), ('무지성찐빵', false), ('선넘는망고', false), ('존버토끼', false), ('오마이갓뚜기', false),
                                              ('갓생사는자두', false), ('심쿵도리', false), ('회전초밥요정', false), ('감성버스정류장', false), ('별다줄냥이', false),
                                              ('폰꾸의신', false), ('반반머리단발좌', false), ('뇌절하지마', false), ('여친있찐남', false), ('얼죽아백곰', false),
                                              ('맘편한찐친', false), ('혼코노장인', false), ('백허그너구리', false), ('주접폭발러', false), ('크크루삥뽕', false),
                                              ('스밍요정', false), ('네버스탑감성', false), ('초코우유보이', false), ('공주님아님주의', false), ('이모티콘수집러', false);