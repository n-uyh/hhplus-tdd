package io.hhplus.tdd.point;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

@Component
public class LockManager {

    private final ConcurrentHashMap<Long, Lock> lock = new ConcurrentHashMap<>();

    public Lock getLock(long userId) {
        return lock.computeIfAbsent(userId, id -> new ReentrantLock(true));
    }

}
