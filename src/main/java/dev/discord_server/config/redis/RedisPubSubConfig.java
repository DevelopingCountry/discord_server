package dev.discord_server.config.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisPubSubConfig {

    @Bean
    @Qualifier("pubSubConnectionFactory")
    public RedisConnectionFactory pubSubConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        config.setDatabase(1); // ✅ DM용 DB 1번
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public StringRedisTemplate pubSubRedisTemplate(@Qualifier("pubSubConnectionFactory") RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean(name = "dmTopic")
    public ChannelTopic dmTopic() {
        return new ChannelTopic("chat.dm");
    }


    @Bean(name = "channelCreatedTopic")
    public ChannelTopic channelCreatedTopic() {
        return new ChannelTopic("channel.created");
    }

    @Bean(name="msgTopic")
    public ChannelTopic msgTopic() {return new ChannelTopic("channel.msg");}

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("pubSubConnectionFactory") RedisConnectionFactory factory,
            DmRedisSubscriber dmSubscriber,
            ChannelCreatedSubscriber channelSubscriber,
            MessageRedisSubscriber messageRedisSubscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(dmSubscriber, new ChannelTopic("chat.dm"));
        container.addMessageListener(channelSubscriber, new PatternTopic("channel.created.*"));
        container.addMessageListener(messageRedisSubscriber, new PatternTopic("channel.msg.*"));
        return container;
    }

}