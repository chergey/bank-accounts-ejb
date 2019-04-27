package org.elcer.accounts.services;


import org.elcer.accounts.exceptions.NoAccountException;
import org.elcer.accounts.model.Account;

import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Stateful
@Local
@SuppressWarnings("WeakerAccess")
public class AccountRepository {


    @PersistenceContext(unitName = "accounts")
    private EntityManager entityManager;


    public Account save(Account account) {
        if (account.getId() == 0L) {
            entityManager.persist(account);
            return account;
        }
        return entityManager.merge(account);
    }


    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<Account> getAllAccounts(int page, int size) {
        var builder = entityManager.getCriteriaBuilder();
        var q = builder.createQuery(Account.class);
        var root = q.from(Account.class);
        q.select(root);
        var query = entityManager.createQuery(q);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();

    }


    @Transactional(Transactional.TxType.MANDATORY)
    public Account retrieveAccountById(long id) {
        Account account = entityManager.find(Account.class, id);

        if (account == null) {
            throw new NoAccountException(id);
        }
        return account;

    }

    public void deleteAccount(Account account) {
        entityManager.remove(entityManager.contains(account) ? account : entityManager.merge(account));
    }


    public void deleteAccount(long id) {
        var builder = entityManager.getCriteriaBuilder();
        CriteriaDelete<Account> delete = builder.createCriteriaDelete(Account.class);
        Root<Account> root = delete.from(Account.class);
        delete.where(builder.equal(root.get("id"), id));
        entityManager.createQuery(delete).executeUpdate();
    }

    public List<Account> retrieveAccountsByName(String name, int page, int size) {
        return CriteriaUtils.createQuery(entityManager, Account.class,
                (builder, root) -> builder.equal(root.get("name"), name))
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
}
