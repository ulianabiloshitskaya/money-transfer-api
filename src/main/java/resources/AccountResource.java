package resources;

import entity.Account;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import repository.AccountDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/account/{accountNumber}")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private final AccountDAO peopleDAO;

    public AccountResource(AccountDAO peopleDAO) {
        this.peopleDAO = peopleDAO;
    }

    @GET
    @UnitOfWork
    public Account getAccount(@PathParam("accountNumber") LongParam personId) {
        return findSafely(personId.get());
    }

    private Account findSafely(long personId) {
        return peopleDAO.findById(personId).orElseThrow(() -> new NotFoundException("No such user."));
    }

}
