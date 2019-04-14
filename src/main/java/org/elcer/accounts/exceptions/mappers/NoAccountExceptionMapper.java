package org.elcer.accounts.exceptions.mappers;

import org.elcer.accounts.exceptions.NoAccountException;
import org.elcer.accounts.model.TransferResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class NoAccountExceptionMapper implements ExceptionMapper<NoAccountException> {
    @Override
    public Response toResponse(NoAccountException exception) {
        return Response.status(404)
                .entity(TransferResponse.noSuchAccount().addAccountId(exception.getAccountId()))
                .build();
    }
}
