package org.elcer.accounts.resource;


import org.elcer.accounts.app.Required;
import org.elcer.accounts.model.Account;
import org.elcer.accounts.model.AccountListResponse;
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

    @PUT
    @Path("/accounts/{id:\\d+}")
    public Response replaceAccount(@PathParam("id") long id, Account account) {
        var builder = uriInfo.getAbsolutePathBuilder();
        Account replaceAccount = accountService.replaceAccount(id, account);
        builder.path(Long.toString(replaceAccount.getId()));
        return Response.created(builder.build()).build();
    }

    @DELETE
    @Path("/accounts/{id:\\d+}")
    public Response deleteAccount(@PathParam("id") long id) {
        var builder = uriInfo.getAbsolutePathBuilder();
        accountService.deleteAccount(id);
        return Response.ok(builder.build()).build();
    }

    @GET
    @Path("/accounts/{name:[a-zA-Z]+}")
    public Response getAccountByName(@PathParam("name") String name,
                                     @DefaultValue("0") @QueryParam("page") int page,
                                     @DefaultValue("20") @QueryParam("size") int size) {
        var accounts = accountService.getAccounts(name, page, size);
        AccountListResponse accountListResponse = new AccountListResponse()
                .setAccounts(accounts)
                .setNoMore(accounts.size() < size);

        return Response.ok(accountListResponse).build();
    }

    @GET
    @Path("/accounts/")
    public Response getAllAccounts(
            @DefaultValue("0") @QueryParam("page") int page,
            @DefaultValue("20") @QueryParam("size") int size) {
        var accounts = accountService.getAllAccounts(page, size);
        AccountListResponse accountListResponse = new AccountListResponse()
                .setAccounts(accounts)
                .setNoMore(accounts.size() < size);
        return Response.ok(accountListResponse).build();
    }

    @GET
    @Path("/accounts/{id:\\d+}")
    public Response getAccount(@PathParam("id") Long id) {
        var account = accountService.getAccount(id);
        return Response.ok(account).build();

    }

    @POST
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
