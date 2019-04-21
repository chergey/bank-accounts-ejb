package org.elcer.accounts.app;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.services.AccountRepository;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.math.BigDecimal;

//@ApplicationScoped (OWB can't inject EJB into CDI)
//@Eager
@Startup
@Singleton
public class SampleDataInitializer {

    private static final int ACCOUNTS_TO_CREATE = 1000;

    @Inject
    private AccountRepository accountRepository;

    private boolean init;


    @PostConstruct
    private void init() {
        if (init) return;
        try {
            for (int i = 1; i < ACCOUNTS_TO_CREATE; i++) {
                var account = new Account(RandomStringUtils.randomAlphabetic(5),
                        BigDecimal.valueOf(RandomUtils.nextLong(100, 10000)));

                accountRepository.createAccount(account);
            }
        } finally {
            init = true;
        }
    }
}
