package resources;

import entity.Account;
import io.dropwizard.hibernate.UnitOfWork;
import repository.AccountDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountsResource {

    private final AccountDAO accountDAO;

    public AccountsResource(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @POST
    @UnitOfWork
    public Account createPerson(Account account) {
        return accountDAO.create(account);
    }

}
