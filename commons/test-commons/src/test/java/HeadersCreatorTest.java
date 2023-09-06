import headers.HeadersCreator;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HeadersCreatorTest {

    @Test
    void shouldCreateHeadersWithTestData() {
        MultiValueMap<String, String> headers = HeadersCreator.createTestHeaders();
        assertThat(headers.get("message-id")).isNotNull();
        assertThat(headers.get("session-tracker")).isNotNull();
        assertThat(headers.get("channel")).isNotNull();
        assertThat(headers.get("request-timestamp")).isNotNull();
        assertThat(headers.get("app-version")).isNotNull();
        assertThat(headers.get("user-agent")).isNotNull();
        assertThat(headers.get("identification-type")).isNotNull();
        assertThat(headers.get("identification-number")).isNotNull();
        assertThat(headers.get("ip")).isNotNull();
        assertThat(headers.get("device-id")).isNotNull();
    }
}
