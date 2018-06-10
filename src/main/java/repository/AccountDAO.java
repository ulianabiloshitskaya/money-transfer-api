package repository;

import entity.Account;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class AccountDAO extends AbstractDAO<Account> {
    public AccountDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Optional<Account> findByIdForUpdate(Long id){
        return Optional.ofNullable(this.currentSession().get(Account.class, id, LockMode.PESSIMISTIC_READ));
    }

    public Account create(Account account) {
        return persist(account);
    }

    public List<Account> findAll() {
        return list(namedQuery("entity.Account.findAll"));
    }
}
