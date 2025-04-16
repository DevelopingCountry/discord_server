package dev.discord_server.config.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisPubSubConfig {

    @Bean
    @Qualifier("pubSubConnectionFactory")
    public RedisConnectionFactory pubSubConnectionFactory() {
        // 기본 Redis와 분리된 Pub/Sub용 설정
        return new LettuceConnectionFactory("localhost", 6379); // 환경에 따라 수정
    }

    @Bean
    public StringRedisTemplate pubSubRedisTemplate(@Qualifier("pubSubConnectionFactory") RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean(name = "dmTopic")
    public ChannelTopic dmTopic() {
        return new ChannelTopic("chat.dm");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("pubSubConnectionFactory") RedisConnectionFactory factory,
            RedisSubscriber redisSubscriber,
            ChannelTopic dmTopic
    )
    {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(redisSubscriber, dmTopic);
        return container;
    }
}