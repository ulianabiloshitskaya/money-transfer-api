package repository;


import entity.Account;
import io.dropwizard.testing.junit.DAOTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountDAOTest {

        @Rule
        public DAOTestRule daoTestRule = DAOTestRule.newBuilder().addEntityClass(Account.class).build();

        private AccountDAO accountDAO;

        @Before
        public void setUp() {
            accountDAO = new AccountDAO(daoTestRule.getSessionFactory());
        }

        @Test
        public void createsAccount() {
            Account account = daoTestRule.inTransaction(() -> {
                return accountDAO.create(new Account(new BigDecimal("10.00")));
            });
            assertThat(account.getAccountNumber()).isGreaterThan(0);
            assertThat(account.getBalance()).isEqualTo(new BigDecimal("10.00"));
            assertThat(accountDAO.findById(account.getAccountNumber())).isEqualTo(Optional.of(account));
        }

        @Test
        public void findAll() {
            daoTestRule.inTransaction(() -> {
                accountDAO.create(new Account(new BigDecimal("1.11")));
                accountDAO.create(new Account(new BigDecimal("2.22")));
                accountDAO.create(new Account(new BigDecimal("3.33")));
            });

            final List<Account> accounts = accountDAO.findAll();
            assertThat(accounts).extracting("balance").containsOnly(new BigDecimal("1.11"), new BigDecimal("2.22"), new BigDecimal("3.33"));
        }

    }
