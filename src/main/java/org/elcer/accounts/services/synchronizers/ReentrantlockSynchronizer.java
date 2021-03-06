package org.elcer.accounts.services.synchronizers;

import org.elcer.accounts.services.CompareStrategy;
import org.elcer.accounts.services.Synchronizer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Default synchronization making use of ReentrantLockS
 * @see Synchronizer
 */
public class ReentrantlockSynchronizer implements Synchronizer<Long> {
    private final Map<Long, Lock> slots = new ConcurrentHashMap<>();

    private CompareStrategy<Long> compareStrategy =
            (candidate1, candidate2) -> candidate1.compareTo(candidate2) > 0;

    @Override
    public void withLock(final Long one, final Long second, Runnable action) {
        final Lock o1 = slots.computeIfAbsent(one, (k) -> new ReentrantLock()),
                o2 = slots.computeIfAbsent(second, (k) -> new ReentrantLock());

        try {
            if (compareStrategy.compare(one, second)) {
                o1.lock();
                o2.lock();
            } else {
                o2.lock();
                o1.lock();
            }

            action.run();
        } finally {
            if (compareStrategy.compare(one, second)) {
                o1.unlock();
                o2.unlock();
            } else {
                o2.unlock();
                o1.unlock();
            }
        }
    }
}
