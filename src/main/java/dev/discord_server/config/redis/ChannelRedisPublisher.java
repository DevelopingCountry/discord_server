// ✅ ChannelRedisPublisher.java 리팩터링
package dev.discord_server.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.domain.channel.dto.ChannelCreatedMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelRedisPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(ChannelCreatedMessageResponse message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            String topicName = "channel.created." + message.getServerId(); // ✅ 서버별 topic 분리
            redisTemplate.convertAndSend(topicName, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}