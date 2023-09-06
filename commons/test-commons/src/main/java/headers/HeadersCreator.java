package headers;

import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@UtilityClass
public class HeadersCreator {

    private static final String ID = "8f86702e-f66b-4f76-9d2d-6f7b0650734d";
    private static final String SESSION_ID = "448589";
    private static final String CHANNEL = "D2B";
    private static final String REQUEST_DATE = "1669940362";
    private static final String APP_VERSION = "2.0";
    private static final String NAME = "Google Chrome";
    private static final String IP = "127.0.0.1";
    private static final String DEVICE_ID = "D001";
    private static final String IDENTIFICATION_TYPE = "CC";
    private static final String IDENTIFICATION_NUMBER = "00000009043";
    private static final String DEVICE_TYPE = "mobile";

    public static MultiValueMap<String, String> createTestHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("message-id", List.of(ID));
        headers.put("session-tracker", List.of(SESSION_ID));
        headers.put("channel", List.of(CHANNEL));
        headers.put("request-timestamp", List.of(REQUEST_DATE));
        headers.put("app-version", List.of(APP_VERSION));
        headers.put("user-agent", List.of(NAME));
        headers.put("identification-type", List.of(IDENTIFICATION_TYPE));
        headers.put("identification-number", List.of(IDENTIFICATION_NUMBER));
        headers.put("ip", List.of(IP));
        headers.put("device-id", List.of(DEVICE_ID));
        headers.put("platform-type", List.of(DEVICE_TYPE));

        return headers;
    }
}
