package entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NamedQueries(
        {
                @NamedQuery(
                        name = "entity.Account.findAll",
                        query = "SELECT p FROM Account p"
                )
        })
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountNumber;

    private BigDecimal balance;

    public Account(){

    }

    public Account(BigDecimal balance) {

        this.balance = balance;
    }

}
