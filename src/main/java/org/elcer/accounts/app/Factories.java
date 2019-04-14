package org.elcer.accounts.app;

import org.elcer.accounts.services.Synchronizer;
import org.elcer.accounts.services.synchronizers.ReentrantlockSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;


public class Factories {

    @Produces
    public Logger getLogger(InjectionPoint ip) {
        return LoggerFactory.getLogger(((Class) ip.getType()));
    }
//
//    @Produces
//    public Synchronizer<Long> synchronizer() {
//        return new ReentrantlockSynchronizer();
//    }


}
