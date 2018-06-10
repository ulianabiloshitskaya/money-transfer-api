import api.Transfer;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Account;
import entity.TransferEntity;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;


public class MoneyTransferAPIIntegrationTest {

    private Response transferResponse;
    private String moneyTransfer;
    private String createAccountEndpoint;
    private Transfer transferRequest;
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    private Account account1;
    private Long account1Number;
    private Account account2;
    private Long account2Number;


    @ClassRule
    public static final DropwizardAppRule<MoneyTransferConfiguration> SUPPORT =
            new DropwizardAppRule<MoneyTransferConfiguration>(MoneyTransferApplication.class, ResourceHelpers.resourceFilePath("config.yaml"));

    @BeforeClass
    public static void migrateDb() throws Exception {
        SUPPORT.getApplication().run("db", "migrate", ResourceHelpers.resourceFilePath("config.yaml"));
    }

    @Before
    public void setup() throws Exception {
        moneyTransfer = String.format("http://localhost:%d/moneytransfer", SUPPORT.getLocalPort());
        createAccountEndpoint = String.format("http://localhost:%d/accounts", SUPPORT.getLocalPort());
    }

    @Test
    public void concurrentRead() throws InterruptedException {

        account1  = new Account(new BigDecimal("100"));
        Response response = SUPPORT.client().target(createAccountEndpoint).request().post(Entity.json(account1));
        account1Number = response.readEntity(Account.class).getAccountNumber();

        account2  = new Account(new BigDecimal("0"));
        response = SUPPORT.client().target(createAccountEndpoint).request().post(Entity.json(account2));
        account2Number = response.readEntity(Account.class).getAccountNumber();

        transferRequest = Transfer.builder().senderAccountId(account1Number).receiverAccountId(account2Number).amount(new BigDecimal("1")).build();


        ExecutorService executor = Executors.newFixedThreadPool(100);
        Callable<Response> task = () -> {
             return transferResponse = SUPPORT.client().target(moneyTransfer)
                    .request()
                    .post(Entity.json(transferRequest));

        };
        List<Callable<Response>> tasks = Collections.nCopies(100, task);
                executor.invokeAll(tasks)
                .stream()
                .map(future -> {
                    try {
                        assertTransfer(future.get());
                        return future.get();
                    }
                    catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });

        assertNewBalance(account1Number,"0");
        assertNewBalance(account2Number, "100");

        executor.shutdownNow();

    }

    private void assertTransfer(Response response) {
        assertThat(transferResponse.getStatus()).isEqualTo(200);
    }


    public void assertNewBalance(Long accountNumber, String balance){
        Object[] args = new Object[] { SUPPORT.getLocalPort(), accountNumber };
        String getAccountEndpoint = String.format("http://localhost:%d/account/%d", args);
        Response accountResponse = SUPPORT.client().target(getAccountEndpoint).request().get();
        Account account = accountResponse.readEntity(Account.class);
        assertThat(account.getBalance()).isEqualTo(new BigDecimal(balance));
    }




}