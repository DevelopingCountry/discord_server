package dev.discord_server.config.redis;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DmSessionTracker {
    // key: dmId, value: Set<userId>
    private final Map<String, Set<Long>> activeUsersPerDm = new ConcurrentHashMap<>();

    public void enterDm(String dmId, Long userId) {
        activeUsersPerDm
                .computeIfAbsent(dmId, k -> ConcurrentHashMap.newKeySet())
                .add(userId);
    }

    public void leaveDm(String dmId, Long userId) {
        Set<Long> users = activeUsersPerDm.get(dmId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) activeUsersPerDm.remove(dmId);
        }
    }

    public boolean isUserActiveInDm(String dmId, Long userId) {
        return activeUsersPerDm.getOrDefault(dmId, Set.of()).contains(userId);
    }
}
