package dev.discord_server.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisPublisher {
    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic dmTopic;
    private final ChannelTopic messageTopic;
    private final ChannelTopic notificationTopic; // ✅ 초대 알림용 채널 추가

    public RedisPublisher(StringRedisTemplate redisTemplate,
                          @Qualifier("dmTopic") ChannelTopic dmTopic,
                          @Qualifier("msgTopic") ChannelTopic messageTopic,
                          @Qualifier("notificationTopic") ChannelTopic notificationTopic) {
        this.redisTemplate = redisTemplate;
        this.dmTopic = dmTopic;
        this.messageTopic = messageTopic;
        this.notificationTopic = notificationTopic;
    }

    public void publishDm(String message) {
        redisTemplate.convertAndSend(dmTopic.getTopic(), message);
    }

    public void publishMessage(String message) {
        redisTemplate.convertAndSend(messageTopic.getTopic(), message);
    }

    public void publishNotification(String json) {
        log.info("Notification publish: {}", json);
        redisTemplate.convertAndSend(notificationTopic.getTopic(), json);
    }


}
