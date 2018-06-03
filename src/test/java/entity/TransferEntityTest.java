package entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.math.BigDecimal;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferEntityTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializeToJSON() throws Exception{
        TransferEntity account  = new TransferEntity(1,2,new BigDecimal("12.35"));
        account.setId(1L);

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/transferResponse.json"), TransferEntity.class));

        assertThat(MAPPER.writeValueAsString(account)).isEqualTo(expected);

    }

}
