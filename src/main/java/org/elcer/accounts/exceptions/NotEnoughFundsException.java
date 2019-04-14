package org.elcer.accounts.exceptions;


import javax.ejb.ApplicationException;

@ApplicationException
public class NotEnoughFundsException extends AccountException {

    public NotEnoughFundsException(long accountId) {
        super(accountId);
    }
}
