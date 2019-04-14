package org.elcer.accounts.services;


import com.google.common.annotations.VisibleForTesting;
import org.elcer.accounts.exceptions.NoAccountException;
import org.elcer.accounts.model.Account;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Stateful
@Local
public class AccountRepository {


    @PostConstruct
    private void init() {
        System.out.println(this);
    }

    @PersistenceContext(unitName = "accounts")
    private EntityManager entityManager;

    @VisibleForTesting
    public Account createAccount(Account account) {
        entityManager.persist(account);
        return account;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<Account> getAllAccounts() {
        var builder = entityManager.getCriteriaBuilder();
        var q = builder.createQuery(Account.class);
        var root = q.from(Account.class);
        q.select(root);
        var query = entityManager.createQuery(q);
        return query.getResultList();

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Account retrieveAccountByIdNewTran(long id) {
        return retrieveAccountById(id);
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public Account retrieveAccountById(long id) {
        var builder = entityManager.getCriteriaBuilder();
        var q = builder.createQuery(Account.class);
        var root = q.from(Account.class);
        q.select(root).where(builder.equal(root.get("id"), id));
        TypedQuery<Account> query = entityManager.createQuery(q);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoAccountException(id);
        }

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void setBalance(Account account, BigDecimal amount) {
        account.setBalance(amount);
    }


}
