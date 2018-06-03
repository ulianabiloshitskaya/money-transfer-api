package repository;

import entity.TransferEntity;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class TransferDAO extends AbstractDAO<TransferEntity> {
    public TransferDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<TransferEntity> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public TransferEntity create(TransferEntity transferEntity) {
        return persist(transferEntity);
    }

    public List<TransferEntity> findAll() {
        return list(namedQuery("entity.TransferEntity.findAll"));
    }
}

