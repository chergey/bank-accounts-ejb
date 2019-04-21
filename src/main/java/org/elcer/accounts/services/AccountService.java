package org.elcer.accounts.services;


import org.elcer.accounts.exceptions.NotEnoughFundsException;
import org.elcer.accounts.model.Account;
import org.slf4j.Logger;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Singleton
@Lock(LockType.READ)
public class AccountService {

    @Inject
    private AccountRepository accountRepository;

    @Inject
    private Logger logger;

    @Inject
    private Synchronizer<Long> synchronizer;


    @Inject
    private AccountService _this;


    public void transfer(long from, long to, BigDecimal amount) {
        logger.info("Begin transfer from {} to {} amount {}", from, to, amount);

        synchronizer.withLock(from, to, () -> _this._transfer(from, to, amount));
    }

    // INTERNAL. Due to limitations in proxying, self-injection is chosen as a solution.
    @Transactional(Transactional.TxType.REQUIRED)
    @SuppressWarnings("WeakerAccess")
    public void _transfer(long from, long to, BigDecimal amount) {

        Account debitAccount = accountRepository.retrieveAccountById(from),
                creditAccount = accountRepository.retrieveAccountById(to);

        if (debitAccount.getBalance().compareTo(amount) >= 0) {
            debitAccount.subtractBalance(amount);
            creditAccount.increaseBalance(amount);

            logger.info("Successfully transferred from {} to {} amount {}", from, to, amount);

        } else {
            throw new NotEnoughFundsException(debitAccount.getId());
        }


    }

    public void createAccount(Account account) {
        accountRepository.createAccount(account);

    }

    public List<Account> getAllAccounts(int page, int size) {
        return accountRepository.getAllAccounts(page, size);

    }

    public Account getAccount(long id) {
        return accountRepository.retrieveAccountById(id);

    }

    public void deleteAccount(long id) {
        accountRepository.deleteAccount(id);
    }

    public List<Account> getAccounts(String name, int page, int size) {
        return accountRepository.retrieveAccountsByName(name, page, size);

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Account replaceAccount(long id, Account account) {
        Account oldAccount = accountRepository.retrieveAccountById(id);
        if (oldAccount != null) {
            accountRepository.deleteAccount(id);
            account.setId(id);
            return accountRepository.createAccount(account);
        } else {
            account.setId(id);
            return accountRepository.createAccount(account);
        }
    }
}



