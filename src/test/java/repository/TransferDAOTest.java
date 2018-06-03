package repository;

import api.Transfer;
import entity.TransferEntity;
import io.dropwizard.testing.junit.DAOTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferDAOTest {

        @Rule
        public DAOTestRule daoTestRule = DAOTestRule.newBuilder().addEntityClass(TransferEntity.class).build();

        private TransferDAO transferDAO;

        @Before
        public void setUp() {
            transferDAO = new TransferDAO(daoTestRule.getSessionFactory());
        }

        @Test
        public void createsTransfer() {
            TransferEntity transfer = daoTestRule.inTransaction(() -> {
                return transferDAO.create(new TransferEntity(1,2,new BigDecimal("10.00")));

            });
            assertThat(transfer.getSenderAccountId()).isGreaterThan(0);
            assertThat(transfer.getAmount()).isEqualTo(new BigDecimal("10.00"));
            assertThat(transfer.getSenderAccountId()).isEqualTo(1);
            assertThat(transfer.getReceiverAccountId()).isEqualTo(2);
            assertThat(transferDAO.findById(transfer.getId())).isEqualTo(Optional.of(transfer));
        }

        @Test
        public void findAll() {
            daoTestRule.inTransaction(() -> {
                transferDAO.create(new TransferEntity(1L,2L,new BigDecimal("1.11")));
                transferDAO.create(new TransferEntity(3L,4L,new BigDecimal("2.22")));
                transferDAO.create(new TransferEntity(5L,6L,new BigDecimal("3.33")));
            });

            final List<TransferEntity> transfers = transferDAO.findAll();
            assertThat(transfers).extracting("amount").containsOnly(new BigDecimal("1.11"), new BigDecimal("2.22"), new BigDecimal("3.33"));
            assertThat(transfers).extracting("senderAccountId").containsOnly(1L,3L,5L);
            assertThat(transfers).extracting("receiverAccountId").containsOnly(2L,4L,6L);

        }

    }
