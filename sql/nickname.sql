USE discord_server;

CREATE TABLE nicknames (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nickname VARCHAR(50) UNIQUE NOT NULL,
                           is_used BOOLEAN DEFAULT FALSE
);

INSERT INTO nicknames (nickname) VALUES
                                     ('칠링도치'), ('민초깡패'), ('알잘딱갈팁'), ('플렉스짱구'), ('킹받는햄찌'),
                                     ('텐션업곰돌'), ('무지성찐빵'), ('선넘는망고'), ('존버토끼'), ('오마이갓뚜기'),
                                     ('갓생사는자두'), ('심쿵도리'), ('회전초밥요정'), ('감성버스정류장'), ('별다줄냥이'),
                                     ('폰꾸의신'), ('반반머리단발좌'), ('뇌절하지마'), ('여친있찐남'), ('얼죽아백곰'),
                                     ('맘편한찐친'), ('혼코노장인'), ('백허그너구리'), ('주접폭발러'), ('크크루삥뽕'),
                                     ('스밍요정'), ('네버스탑감성'), ('초코우유보이'), ('공주님아님주의'), ('이모티콘수집러');

