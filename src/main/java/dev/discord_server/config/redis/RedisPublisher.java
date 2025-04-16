package dev.discord_server.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {
    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic dmTopic;

    public void publish(String message) {
        redisTemplate.convertAndSend(dmTopic.getTopic(), message); // 채널명 chat 고정
    }
}