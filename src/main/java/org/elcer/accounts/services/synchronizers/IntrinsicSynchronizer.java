package org.elcer.accounts.services.synchronizers;


import org.elcer.accounts.services.CompareStrategy;
import org.elcer.accounts.services.Synchronizer;

import javax.enterprise.inject.Vetoed;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Synchronization mechanism on primitives
 */
@Vetoed
public class IntrinsicSynchronizer implements Synchronizer<Long> {
    private final Map<Long, Object> slots = new ConcurrentHashMap<>();

    private CompareStrategy<Long> compareStrategy =
            (candidate1, candidate2) -> candidate1.compareTo(candidate2) > 0;

    public void withLock(final Long one, final Long second, Runnable action) {
        Object o1 = slots.computeIfAbsent(one, (k) -> new Object()),
                o2 = slots.computeIfAbsent(second, (k) -> new Object()),
                firstToTake, secondToTake;
        if (compareStrategy.compare(one, second)) {
            firstToTake = o1;
            secondToTake = o2;
        } else {
            firstToTake = o2;
            secondToTake = o1;
        }

        synchronized (firstToTake) {
            synchronized (secondToTake) {
                action.run();
            }
        }
    }

}
