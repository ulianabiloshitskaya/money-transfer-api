package api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Transfer {

    public long userId;

    public long senderAccountId;

    public long receiverAccountId;

    public BigDecimal amount;

    public Transfer(){};
}
