package org.elcer.accounts.services;


/**
 * Synchcronizer used to manage concurrent operations
 * @param <T>
 */
public interface Synchronizer<T> {
    void withLock(T one, T second, Runnable action);
}
