package entity;

import api.Transfer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "transfer")
@NamedQueries(
        {
                @NamedQuery(
                        name = "entity.TransferEntity.findAll",
                        query = "SELECT p FROM TransferEntity p"
                )
        })
public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    public long id;

    public TransferEntity(long senderAccountId, long receiverAccountId, BigDecimal amount) {
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
    }

    public long senderAccountId;

    public long receiverAccountId;

    public BigDecimal amount;

    public TransferEntity(){

    }


}
