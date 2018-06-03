package api;

import static io.dropwizard.testing.FixtureHelpers.*;
        import static org.assertj.core.api.Assertions.assertThat;
        import io.dropwizard.jackson.Jackson;
        import org.junit.Test;
        import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

public class TransferTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void deserializesFromJSON() throws Exception {
        final Transfer transfer = Transfer.builder().userId(12345).senderAccountId(1).receiverAccountId(2).amount(new BigDecimal("11.00")).build();
        System.out.println(transfer.toString());
        assertThat(MAPPER.readValue(fixture("fixtures/transferRequest.json"), Transfer.class).equals(transfer));
    }
}