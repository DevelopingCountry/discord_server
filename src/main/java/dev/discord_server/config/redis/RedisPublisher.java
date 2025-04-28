package dev.discord_server.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class RedisPublisher {
    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic dmTopic;
    private final ChannelTopic messageTopic;

    public RedisPublisher(StringRedisTemplate redisTemplate,
                          @Qualifier("dmTopic") ChannelTopic dmTopic,
                          @Qualifier("channelCreatedTopic") ChannelTopic messageTopic) {
        this.redisTemplate = redisTemplate;
        this.dmTopic = dmTopic;
        this.messageTopic = messageTopic;
    }

    public void publishDm(String message) {
        redisTemplate.convertAndSend(dmTopic.getTopic(), message);
    }

    public void publishMessage(String message) {
        redisTemplate.convertAndSend(messageTopic.getTopic(), message);
    }

}