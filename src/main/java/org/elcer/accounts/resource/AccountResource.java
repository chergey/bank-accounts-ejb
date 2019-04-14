package org.elcer.accounts.resource;


import org.elcer.accounts.app.Required;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.model.TransferResponse;
import org.elcer.accounts.services.AccountService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;

@Path("/api/accounts")
@Produces({MediaType.APPLICATION_JSON})
public class AccountResource {

    @Inject
    private AccountService accountService;

    @Context
    private UriInfo uriInfo;

    @POST
    @Path("/accounts")
    public Response createAccount(Account account) {
        var builder = uriInfo.getAbsolutePathBuilder();
        accountService.createAccount(account);
        builder.path(Long.toString(account.getId()));
        return Response.created(builder.build()).build();
    }


    @GET
    @Path("/transfer")
    @Required({"from", "to", "amount"})
    public Response transfer(@QueryParam("from") long from, @QueryParam("to") long to,
                             @QueryParam("amount") BigDecimal amount) {


        if (from == to) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(TransferResponse.debitAccountIsCreditAccount()).build();
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Response.ok(TransferResponse.negativeAmount()).build();
        }
        accountService.transfer(from, to, amount);
        return Response.ok(TransferResponse.success()).build();
    }

}
