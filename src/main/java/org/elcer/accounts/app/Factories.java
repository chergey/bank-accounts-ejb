package org.elcer.accounts.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;


public class Factories {

    @Produces
    public Logger getLogger(InjectionPoint ip) {
        return LoggerFactory.getLogger((Class) ip.getType());
    }


}
