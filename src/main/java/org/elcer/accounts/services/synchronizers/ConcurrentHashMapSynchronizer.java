package org.elcer.accounts.services.synchronizers;

import org.elcer.accounts.services.CompareStrategy;
import org.elcer.accounts.services.Synchronizer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Alternative synchronization, making use of internal structure of CHM
 *
 * @see Synchronizer
 */
public class ConcurrentHashMapSynchronizer implements Synchronizer<Long> {

    private static final Object DUMMY_OBJECT = new Object();

    private final Map<Long, Object> slots = new ConcurrentHashMap<>();

    private CompareStrategy<Long> compareStrategy =
            (candidate1, candidate2) -> candidate1.compareTo(candidate2) > 0;


    private void runAtomically(Long firstToTake, Long secondToTake, Runnable action) {
        slots.compute(firstToTake, (t, o) -> {
            slots.compute(secondToTake, (t1, o1) -> {
                action.run();
                return DUMMY_OBJECT;
            });
            return DUMMY_OBJECT;
        });
    }

    @Override
    public void withLock(Long one, Long second, Runnable action) {
        if (compareStrategy.compare(one, second)) {
            runAtomically(one, second, action);
        } else {
            runAtomically(second, one, action);
        }
    }
}
