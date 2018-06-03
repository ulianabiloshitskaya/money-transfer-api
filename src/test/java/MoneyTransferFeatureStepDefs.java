import api.Transfer;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import entity.Account;
import entity.TransferEntity;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.ResourceHelpers;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


public class MoneyTransferFeatureStepDefs {

    private Response transferResponse;
    private String moneyTransfer;
    private String createAccountEndpoint;
    private Transfer transferRequest;
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    private Account account1;
    private Long account1Number;
    private Account account2;
    private Long account2Number;
    private Long invalidAccountNumber = 0L;
    private TransferEntity responseEntity;
    private Client client;

//    //TODO use DAO to setup test data
//    private AccountDAO accountDAO;
//    private TransferDAO transferDAO;

    private final HibernateBundle<MoneyTransferConfiguration> hibernate = new HibernateBundle<MoneyTransferConfiguration>(Account.class, TransferEntity.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(MoneyTransferConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }

    };

    public static final DropwizardTestSupport<MoneyTransferConfiguration> SUPPORT =
            new DropwizardTestSupport<MoneyTransferConfiguration>(MoneyTransferApplication.class,
                    ResourceHelpers.resourceFilePath("config.yaml")
            );

    @Before
    public void setup() throws Exception {
        SUPPORT.before();
        SUPPORT.getApplication().run("db", "migrate", ResourceHelpers.resourceFilePath("config.yaml"));
        client = new JerseyClientBuilder(SUPPORT.getEnvironment()).build("test client");
        moneyTransfer = String.format("http://localhost:%d/moneytransfer", SUPPORT.getLocalPort());
        createAccountEndpoint = String.format("http://localhost:%d/accounts", SUPPORT.getLocalPort());

//        //TODO use DAO to setup test data
//        accountDAO = new AccountDAO(hibernate.getSessionFactory());
//        transferDAO = new TransferDAO(hibernate.getSessionFactory());
    }

    @After
    public void afterClass() {
        SUPPORT.after();
    }

    @Given("^account1 has a balance of ([^\\\"]*)$")
    public void account1_has_a_balance_of(String balance) throws Throwable {

        account1  = new Account(new BigDecimal(balance));
        Response response = client.target(createAccountEndpoint).request().post(Entity.json(account1));
        account1Number = response.readEntity(Account.class).getAccountNumber();

//        //TODO use DAO to setup test data
//        account1 = accountDAO.create(account1);
    }

    @Given("^account2 has a balance of ([^\\\"]*)$")
    public void account2_has_a_balance_of(String balance) throws Throwable {

        account2  = new Account(new BigDecimal(balance));
        Response response = client.target(createAccountEndpoint).request().post(Entity.json(account2));
        account2Number = response.readEntity(Account.class).getAccountNumber();
    }

    @When("^I request to transfer ([^\\\"]*) from account1 to account2$")
    public void i_request_to_transfer_from_account_to_account(String amount) throws Throwable {

        transferRequest = Transfer.builder().senderAccountId(account1Number).receiverAccountId(account2Number).amount(new BigDecimal(amount)).build();

        transferResponse = client.target(moneyTransfer)
                .request()
                .post(Entity.json(transferRequest));
    }

    @Then("^response status code is (\\d+)$")
    public void response_status_code_is(int code) throws Throwable {

        int statusCode = transferResponse.getStatus();
        assertThat(transferResponse.getStatus()).isEqualTo(code);
    }

    @Then("^response body is the transfer with id$")
    public void response_body_is_the_transfer_with_id() throws Throwable {

        responseEntity = transferResponse.readEntity(TransferEntity.class);

        assertThat(responseEntity.getSenderAccountId()).isEqualTo(transferRequest.getSenderAccountId());
        assertThat(responseEntity.getReceiverAccountId()).isEqualTo(transferRequest.getReceiverAccountId());
        assertThat(responseEntity.getAmount()).isEqualTo(transferRequest.amount);
        assertThat(responseEntity.getId()).isNotNull();
    }

    @Then("^account1 has new balance of ([^\\\"]*)$")
    public void account1_has_new_balance_of(String balance) throws Throwable {

        assertNewBalance(account1Number, balance);
    }

    public void assertNewBalance(Long accountNumber, String balance){
        Object[] args = new Object[] { SUPPORT.getLocalPort(), accountNumber };
        String getAccountEndpoint = String.format("http://localhost:%d/account/%d", args);
        Response accountResponse = client.target(getAccountEndpoint).request().get();
        Account account = accountResponse.readEntity(Account.class);
        assertThat(account.getBalance()).isEqualTo(new BigDecimal(balance));
    }

    @Then("^account2 has new balance of ([^\\\"]*)$")
    public void account2_has_new_balance_of(String balance) throws Throwable {

        assertNewBalance(account2Number, balance);
    }

    @Given("^account1 does not exist$")
    public void account1_does_not_exist() throws Throwable {

        account1Number = invalidAccountNumber;
    }

    @Given("^account2 does not exist$")
    public void account2_does_not_exist() throws Throwable {

        account2Number = invalidAccountNumber;
    }

    @Then("^response message is \"([^\"]*)\"$")
    public void response_message_is(String arg1) throws Throwable {

        ErrorMessage errorMessage = transferResponse.readEntity(ErrorMessage.class);
        assertThat(errorMessage.getMessage()).isEqualTo(arg1);
    }

}