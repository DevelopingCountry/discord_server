package dev.discord_server.config;

import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdGenerator {

    private final long nodeId;
    private final long epoch = 1609459200000L; // 2021-01-01 기준

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator() {
        this.nodeId = 1L; // 0~1023 범위
    }

    public synchronized long generateId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & 0xFFF; // 12bit mask = 4095
            if (sequence == 0) {
                timestamp = waitNextMillis(timestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << 22)
                | (nodeId << 12)
                | sequence;
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = System.currentTimeMillis();
        }
        return currentTimestamp;
    }
}
