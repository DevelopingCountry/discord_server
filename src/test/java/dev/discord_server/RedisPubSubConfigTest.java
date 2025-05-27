package dev.discord_server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("prod")
public class RedisPubSubConfigTest {

    @Autowired
    RedisConnectionFactory pubSubConnectionFactory;

    @Test
    void redisConnectionTest() {
        Assertions.assertNotNull(pubSubConnectionFactory.getConnection());
        System.out.println("✅ Redis PubSub 연결 성공: " + pubSubConnectionFactory.getConnection().ping());
    }
}
