package dev.discord_server.config.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisPubSubConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    // 기본 빈을 제거하고 프로필별 빈만 유지합니다

    @Bean
    @Profile("dev")
    @Qualifier("pubSubConnectionFactory")
    public RedisConnectionFactory pubSubConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        config.setDatabase(1); // DM용 DB 1번
        return new LettuceConnectionFactory(config);
    }

    @Bean
    @Profile("prod")
    @Qualifier("pubSubConnectionFactory")
    public RedisConnectionFactory prodPubSubConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        config.setDatabase(1);
        return new LettuceConnectionFactory(config);
    }

    // 기본 프로필을 위한 빈 추가 (dev나 prod가 아닐 경우)
    @Bean
    @Profile("!dev & !prod")
    @Qualifier("pubSubConnectionFactory")
    public RedisConnectionFactory defaultPubSubConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        config.setDatabase(1);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public StringRedisTemplate pubSubRedisTemplate(@Qualifier("pubSubConnectionFactory") RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean(name = "notificationTopic")
    public ChannelTopic notificationTopic() {
        return new ChannelTopic("notifications"); // ✅ 변경
    }

    @Bean(name = "dmTopic")
    public ChannelTopic dmTopic() {
        return new ChannelTopic("chat.dm");
    }

    @Bean(name = "channelEventTopic")
    public ChannelTopic channelEventTopic() {
        return new ChannelTopic("channel.event");
    }

    @Bean(name="msgTopic")
    public ChannelTopic msgTopic() {return new ChannelTopic("channel.msg");}

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("pubSubConnectionFactory") RedisConnectionFactory factory,
            DmRedisSubscriber dmSubscriber,
            ChannelSubscriber channelSubscriber,
            MessageRedisSubscriber messageRedisSubscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(dmSubscriber, new ChannelTopic("chat.dm"));
        container.addMessageListener(channelSubscriber, new PatternTopic("channel.event.*"));
        container.addMessageListener(messageRedisSubscriber, new PatternTopic("channel.msg"));
        return container;
    }
}