package resources;

import entity.Account;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import repository.AccountDAO;

import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class AccountResourceTest {
    private static final AccountDAO DAO = mock(AccountDAO.class);
    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .addResource(new AccountResource(DAO))
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .build();
    private Account person;

    @Before
    public void setup() {
        person = new Account();
        person.setAccountNumber(1L);
    }

    @After
    public void tearDown() {
        reset(DAO);
    }

    @Test
    public void getAccountSuccess() {
        when(DAO.findById(1L)).thenReturn(Optional.of(person));

        Account foundAccount = RULE.target("/account/1").request().get(Account.class);

        assertThat(foundAccount.getAccountNumber()).isEqualTo(person.getAccountNumber());
        verify(DAO).findById(1L);
    }

    @Test
    public void getAccountNotFound() {
        when(DAO.findById(2L)).thenReturn(Optional.empty());
        final Response response = RULE.target("/account/2").request().get();

        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        verify(DAO).findById(2L);
    }
}
