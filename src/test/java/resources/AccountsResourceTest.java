package resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import entity.Account;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnitRunner;
import repository.AccountDAO;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountsResourceTest {
    private static final AccountDAO PERSON_DAO = mock(AccountDAO.class);
    @ClassRule
    public static final ResourceTestRule RESOURCES = ResourceTestRule.builder()
            .addResource(new AccountsResource(PERSON_DAO))
            .build();
    @Captor
    private ArgumentCaptor<Account> accountCaptor;
    private Account account;

    @Before
    public void setUp() {
        account = new Account();
        account.setBalance(new BigDecimal("11.12"));
    }

    @After
    public void tearDown() {
        reset(PERSON_DAO);
    }

    @Test
    public void createAccount() throws JsonProcessingException {
        when(PERSON_DAO.create(any(Account.class))).thenReturn(account);
        final Response response = RESOURCES.target("/accounts")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON_TYPE));

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        verify(PERSON_DAO).create(accountCaptor.capture());
        assertThat(accountCaptor.getValue().getAccountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(accountCaptor.getValue().getBalance()).isEqualTo(account.getBalance());
    }

}
