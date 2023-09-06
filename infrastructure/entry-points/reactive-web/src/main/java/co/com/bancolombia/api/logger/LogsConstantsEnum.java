package co.com.bancolombia.api.logger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogsConstantsEnum {

    SERVICE_NAME("ds_ms_action_trail"),
    TIME_PATTERN("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
    CACHE_RESPONSE_BODY("CACHE_RESPONSE_BODY"),
    CACHE_RESPONSE_INSTANT("CACHE_RESPONSE_INSTANT"),
    CACHE_REQUEST_INSTANT("CACHE_REQUEST_INSTANT"),
    CACHE_REQUEST_BODY ("CACHE_REQUEST_BODY"),
    MESSAGE_ID("message-id");

    private final String name;
}