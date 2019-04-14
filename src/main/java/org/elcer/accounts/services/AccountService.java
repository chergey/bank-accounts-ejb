package org.elcer.accounts.services;


import org.elcer.accounts.exceptions.NotEnoughFundsException;
import org.elcer.accounts.model.Account;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;

@Stateless
public class AccountService {

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private Logger logger;

    @Inject
    private Synchronizer<Long> synchronizer;


    @Transactional(Transactional.TxType.REQUIRED)
    public void transfer(long from, long to, BigDecimal amount) {
        logger.info("Begin transfer from {} to {} amount {}", from, to, amount);

        synchronizer.withLock(from, to, () -> {
            Account debitAccount = accountRepository.retrieveAccountById(from),
                    creditAccount = accountRepository.retrieveAccountById(to);

            if (debitAccount.getBalance().compareTo(amount) >= 0) {
                accountRepository.setBalance(debitAccount,
                        debitAccount.getBalance().subtract(amount));
                accountRepository.setBalance(creditAccount,
                        creditAccount.getBalance().add(amount));

                logger.info("Successfully transferred from {} to {} amount {}", from, to, amount);

            } else {
                throw new NotEnoughFundsException(debitAccount.getId());
            }

        });


    }

    public void createAccount(Account account) {

    }
}



