package org.elcer.accounts.exceptions;


import javax.ejb.ApplicationException;

@ApplicationException
public class NoAccountException extends AccountException {

    public NoAccountException(long accountId) {
        super(accountId);
    }
}
