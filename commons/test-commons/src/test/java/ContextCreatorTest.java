import co.com.bancolombia.model.commons.Context;
import context.ContextCreator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ContextCreatorTest {

    @Test
    void shouldCreateContextWithTestData() {
        Context testContext = ContextCreator.createTestContext();
        assertThat(testContext.getId()).isNotEmpty();
        assertThat(testContext.getSessionId()).isNotEmpty();
        assertThat(testContext.getChannel()).isNotEmpty();
        assertThat(testContext.getDomain()).isNotEmpty();
        assertThat(testContext.getRequestDate()).isNotEmpty();
        assertThat(testContext.getAgent()).isNotNull();
        assertThat(testContext.getAgent().getAppVersion()).isNotEmpty();
        assertThat(testContext.getAgent().getName()).isNotEmpty();
        assertThat(testContext.getDevice()).isNotNull();
        assertThat(testContext.getDevice().getIp()).isNotEmpty();
        assertThat(testContext.getDevice().getId()).isNotEmpty();
        assertThat(testContext.getCustomer()).isNotNull();
        assertThat(testContext.getCustomer().getIdentification()).isNotNull();
        assertThat(testContext.getCustomer().getIdentification().getType()).isNotEmpty();
        assertThat(testContext.getCustomer().getIdentification().getNumber()).isNotEmpty();
    }
}
