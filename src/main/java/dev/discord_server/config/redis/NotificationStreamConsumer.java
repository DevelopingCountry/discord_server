package dev.discord_server.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.domain.server.dto.WebSocketNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationStreamConsumer {

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    // 유저별로 실행되는 consumer pool
    private final Map<Long, Future<?>> activeConsumers = new ConcurrentHashMap<>();

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Async
    public void startConsumerForUser(Long userId) {
        if (activeConsumers.containsKey(userId)) return;

        Future<?> future = executor.submit(() -> {
            String streamKey = "notifications:" + userId;
            String group = "notification-group";
            String consumerName = "user-" + userId;

            StreamOperations<String, String, String> streamOps = redisTemplate.opsForStream();

            // 1. Consumer Group 생성 (이미 있으면 무시)
            try {
                if (!Boolean.TRUE.equals(redisTemplate.hasKey(streamKey))) {
                    redisTemplate.opsForStream().add(streamKey, Map.of("init", "true"));
                }
                streamOps.createGroup(streamKey, ReadOffset.latest(), group);
            } catch (Exception e) {
                String msg = e.getMessage() != null ? e.getMessage() : "";
                String cause = e.getCause() != null ? e.getCause().getMessage() : "";
                if (!msg.contains("BUSYGROUP") && !cause.contains("BUSYGROUP")) {
                    log.warn("⚠️ Consumer Group 생성 실패 (무시 가능): {} / {}", msg, cause);
                }
            }


            // 2. 무한 루프 - 알림 수신
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    List<MapRecord<String, String, String>> messages = streamOps.read(
                            Consumer.from(group, consumerName),
                            StreamReadOptions.empty().block(Duration.ofSeconds(5)).count(10),
                            StreamOffset.create(streamKey, ReadOffset.lastConsumed())
                    );

                    if (messages != null) {
                        for (MapRecord<String, String, String> record : messages) {
                            String action = record.getValue().get("action");
                            String payloadJson = record.getValue().get("payload");

                            WebSocketNotification notification = new WebSocketNotification(
                                    action,
                                    objectMapper.readValue(payloadJson, Object.class),
                                    userId
                            );

                            messagingTemplate.convertAndSendToUser(
                                    String.valueOf(userId),
                                    "/queue/notifications",
                                    notification
                            );

                            streamOps.acknowledge(streamKey, group, record.getId());
                        }
                    }
                } catch (Exception e) {
                    log.error("❌ Redis Stream 소비 중 오류 발생", e);
                }
            }
        });

        activeConsumers.put(userId, future);
    }

    public void stopConsumerForUser(Long userId) {
        Future<?> future = activeConsumers.remove(userId);
        if (future != null) future.cancel(true);
    }
}
