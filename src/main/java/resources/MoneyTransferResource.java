package resources;

import api.Transfer;
import entity.Account;
import entity.TransferEntity;
import io.dropwizard.hibernate.UnitOfWork;
import repository.AccountDAO;
import repository.TransferDAO;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Path("/moneytransfer")
@Produces(MediaType.APPLICATION_JSON)
public class MoneyTransferResource {

    private final AccountDAO accountDAO;
    private final TransferDAO transferDAO;

    public MoneyTransferResource(AccountDAO accountDAO, TransferDAO transferDAO) {
        this.accountDAO = accountDAO;
        this.transferDAO = transferDAO;
    }

    @POST
    @UnitOfWork
    public TransferEntity createMoneyTransfer(Transfer moneytransfer) {
        return createTransfer(moneytransfer);
    }

    public TransferEntity createTransfer(Transfer transferRequest){

        Account sender = accountDAO.findByIdForUpdate(transferRequest.getSenderAccountId()).orElseThrow(() -> new BadRequestException("Invalid origin account."));
        Account receiver = accountDAO.findByIdForUpdate(transferRequest.getReceiverAccountId()).orElseThrow(() -> new BadRequestException("Invalid destination account."));

        BigDecimal senderBalance = sender.getBalance();
        BigDecimal receiverBalance = receiver.getBalance();
        BigDecimal transferAmount = transferRequest.getAmount();

        if(senderBalance.compareTo(transferAmount) < 0 ) throw new BadRequestException("Insufficient funds in origin account.");

        sender.setBalance(senderBalance.subtract(transferRequest.getAmount()));
        receiver.setBalance(receiverBalance.add(transferRequest.getAmount()));

        TransferEntity transfer = new TransferEntity(transferRequest.getSenderAccountId(),transferRequest.getReceiverAccountId(),transferAmount);

        accountDAO.create(sender);
        accountDAO.create(receiver);
        return transferDAO.create(transfer);

    }

}
