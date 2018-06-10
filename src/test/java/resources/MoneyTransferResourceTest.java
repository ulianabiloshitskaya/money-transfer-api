package resources;

import api.Transfer;
import com.fasterxml.jackson.core.JsonProcessingException;
import entity.Account;
import entity.TransferEntity;
import gherkin.lexer.Ar;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import repository.AccountDAO;
import repository.TransferDAO;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MoneyTransferResourceTest {

    private static final AccountDAO accountDAO = mock(AccountDAO.class);
    private static final TransferDAO transferDAO = mock(TransferDAO.class);

    @Captor
    private ArgumentCaptor<Account> accountCaptor;
    @Captor
    private ArgumentCaptor<TransferEntity> transferCaptor;

    @ClassRule
    public static final ResourceTestRule RULE = ResourceTestRule.builder()
            .addResource(new AccountResource(accountDAO))
            .addResource(new MoneyTransferResource(accountDAO,transferDAO))
            .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
            .build();


    private Account originAccount;
    private Account destinationAccount;
    private Transfer transferRequest;
    private TransferEntity transferResponse;
    private Long originId;
    private Long destinationId;
    private BigDecimal amount;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        setupEntities();
    }

    @After
    public void tearDown() {
        reset(accountDAO);
        reset(transferDAO);
        validateMockitoUsage();
    }

    @Test
    public void createAccount() throws JsonProcessingException {

        when(accountDAO.findByIdForUpdate(originId)).thenReturn(Optional.of(originAccount));
        when(accountDAO.findByIdForUpdate(destinationId)).thenReturn(Optional.of(destinationAccount));
        when(transferDAO.create(any(TransferEntity.class))).thenReturn(transferResponse);

        final Response response = postRequest();

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

        verify(accountDAO).findByIdForUpdate(originId);
        verify(accountDAO).findByIdForUpdate(destinationId);

        verify(accountDAO, times(2)).create(accountCaptor.capture());
        List<Account> capturedAccounts = accountCaptor.getAllValues();
        assertThat(capturedAccounts.get(0).getAccountNumber()).isEqualTo(originAccount.getAccountNumber());
        assertThat(capturedAccounts.get(0).getBalance()).isEqualTo(new BigDecimal("9.59"));
        assertThat(capturedAccounts.get(1).getAccountNumber()).isEqualTo(destinationAccount.getAccountNumber());
        assertThat(capturedAccounts.get(1).getBalance()).isEqualTo(new BigDecimal("1.52"));

        verify(transferDAO).create(transferCaptor.capture());
        assertThat(transferCaptor.getValue().getSenderAccountId()).isEqualTo(originId);
        assertThat(transferCaptor.getValue().getReceiverAccountId()).isEqualTo(destinationId);
        assertThat(transferCaptor.getValue().getAmount()).isEqualTo(amount);
    }

    @Test
    public void originIdInvalid() throws JsonProcessingException {

        when(accountDAO.findById(originId)).thenReturn(Optional.ofNullable(null));
        when(accountDAO.findById(destinationId)).thenReturn(Optional.of(destinationAccount));
        when(transferDAO.create(any(TransferEntity.class))).thenReturn(transferResponse);

        final Response response = postRequest();

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);

        verify(accountDAO, times(0)).create(any(Account.class));
        verify(transferDAO, times(0)).create(transferCaptor.capture());
    }

    @Test
    public void destinationIdInvalid() throws JsonProcessingException {

        originAccount.setBalance(new BigDecimal("0"));

        when(accountDAO.findById(originId)).thenReturn(Optional.of(originAccount));
        when(accountDAO.findById(destinationId)).thenReturn(Optional.of(destinationAccount));
        when(transferDAO.create(any(TransferEntity.class))).thenReturn(transferResponse);

        final Response response = postRequest();

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);

        verify(accountDAO, times(0)).create(any(Account.class));
        verify(transferDAO, times(0)).create(transferCaptor.capture());
    }

    @Test
    public void insufficientFunds() throws JsonProcessingException {

        when(accountDAO.findById(originId)).thenReturn(Optional.of(originAccount));
        when(accountDAO.findById(destinationId)).thenReturn(Optional.ofNullable(null));
        when(transferDAO.create(any(TransferEntity.class))).thenReturn(transferResponse);

        final Response response = postRequest();

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);

        verify(accountDAO, times(0)).create(any(Account.class));
        verify(transferDAO, times(0)).create(transferCaptor.capture());
    }

    private void setupEntities(){
        originId = 1L;
        originAccount = new Account();
        originAccount.setAccountNumber(originId);
        originAccount.setBalance(new BigDecimal("11.11"));

        destinationId = 2L;
        destinationAccount = new Account();
        destinationAccount.setAccountNumber(destinationId);
        destinationAccount.setBalance(new BigDecimal("0"));

        amount = new BigDecimal("1.52");
        transferResponse = new TransferEntity();
        transferRequest = Transfer.builder()
                .senderAccountId(originId)
                .receiverAccountId(destinationId)
                .amount(amount).build();
    }

    private Response postRequest(){

        return RULE.target("/moneytransfer")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(transferRequest, MediaType.APPLICATION_JSON_TYPE));

    }

}
