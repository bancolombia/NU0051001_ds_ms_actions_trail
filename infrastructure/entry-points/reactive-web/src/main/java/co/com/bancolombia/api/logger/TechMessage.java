package co.com.bancolombia.api.logger;

import co.com.bancolombia.logging.technical.message.ObjectTechMsg;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static co.com.bancolombia.api.logger.LogsConstantsEnum.SERVICE_NAME;

@UtilityClass
public class TechMessage {

    private static final String EMPTY_STRING = "";
    private static final String CHANNEL = "channel";
    private static final String REQUEST = "request";
    private static final String RESPONSE = "response";
    private static final String BODY = "body";
    private static final String HEADERS = "headers";
    private static final String TIMESTAMP = "timestamp";
    private static final String APP_VERSION = "app-version";
    private static final String TRACE = "trace";
    private static final String CAUSE = "cause";

    public static ObjectTechMsg<Map<String, Object>> getErrorTechMessage(@NonNull Throwable error,
                                                                         ServerRequest request) {

        return new ObjectTechMsg<>(
                SERVICE_NAME.getName(),
                getFirstHeader(request, LogsConstantsEnum.MESSAGE_ID.getName()),
                request.path(),
                SERVICE_NAME.getName(),
                WebExceptionHandler.class.getSimpleName(),
                getTagList(getFirstHeader(request, CHANNEL), getFirstHeader(request, APP_VERSION)),
                Map.of(TRACE, error.getStackTrace(),
                        CAUSE, Optional.ofNullable(error.getCause()).orElse(new Throwable()))
        );
    }

    public static ObjectTechMsg<Object> getInfoTechMessage(ServerWebExchange webExchange) {
        return new ObjectTechMsg<>(SERVICE_NAME.getName(),getTransactionId(webExchange),
                webExchange.getRequest().getPath().value(),
                SERVICE_NAME.getName(), WriteInfoLogFilter.class.getSimpleName(),
                getTagList(getFirstHeader(webExchange, CHANNEL), getFirstHeader(webExchange, APP_VERSION)),
                getMessage(webExchange));
    }

    private static String getFirstHeader(ServerRequest request, String name) {
        return Optional.ofNullable(request)
                .map(ServerRequest::headers)
                .map(headers -> headers.firstHeader(name))
                .orElse(EMPTY_STRING);
    }

    private static String getFirstHeader(ServerWebExchange exchange, String name) {
        return Optional.ofNullable(exchange)
                .map(ServerWebExchange::getRequest)
                .map(HttpMessage::getHeaders)
                .map(headers -> headers.getFirst(name))
                .orElse(EMPTY_STRING);
    }

    private static List<String> getTagList(String channel, String appVersion) {
        return List.of(channel, formatAppVersion(appVersion));
    }

    private static String formatAppVersion(String appVersion) {
        return String.join(" ", APP_VERSION, appVersion);
    }

    private static String getTransactionId(ServerWebExchange webExchange) {
        return Optional.ofNullable(webExchange)
                .map(ServerWebExchange::getRequest)
                .map(HttpMessage::getHeaders)
                .map(HttpHeaders::toSingleValueMap)
                .orElse(Collections.emptyMap())
                .getOrDefault(LogsConstantsEnum.MESSAGE_ID.getName(), EMPTY_STRING);
    }

    private static Map<String, Object> getMessage(ServerWebExchange exchange) {
        return Map.of(REQUEST, getRequest(exchange),
                RESPONSE, getResponse(exchange));
    }

    private static Map<String, Object> getRequest(ServerWebExchange exchange) {
        return Map.of(TIMESTAMP, getAttributeFromExchange(exchange, LogsConstantsEnum.CACHE_REQUEST_INSTANT.getName()),
                HEADERS, getRequestHeader(exchange),
                BODY, getAttributeFromExchange(exchange, LogsConstantsEnum.CACHE_REQUEST_BODY.getName()));
    }

    private static Object getAttributeFromExchange(ServerWebExchange exchange, String name) {
        return Optional.ofNullable(exchange)
                .map(ex -> ex.getAttribute(name))
                .orElse(EMPTY_STRING);
    }

    private static Map<String, String> getResponseHeader(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange)
                .map(ServerWebExchange::getResponse)
                .map(HttpMessage::getHeaders)
                .map(HttpHeaders::toSingleValueMap)
                .orElse(Map.of(EMPTY_STRING, EMPTY_STRING));
    }

    private static Map<String, String> getRequestHeader(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange)
                .map(ServerWebExchange::getRequest)
                .map(HttpMessage::getHeaders)
                .map(HttpHeaders::toSingleValueMap)
                .orElse(Map.of(EMPTY_STRING, EMPTY_STRING));
    }

    private static Map<String, Object> getResponse(ServerWebExchange exchange) {
        return Map.of(
                TIMESTAMP, getTimeStampFormatted(System.currentTimeMillis()),
                HEADERS, getResponseHeader(exchange),
                BODY, getAttributeFromExchange(exchange, LogsConstantsEnum.CACHE_RESPONSE_BODY.getName())
        );
    }

    private static String getTimeStampFormatted(Long currentTimeMillis) {
        var dateFormat = new SimpleDateFormat(LogsConstantsEnum.TIME_PATTERN.getName());
        return dateFormat.format(Date.from(Instant.ofEpochMilli(currentTimeMillis)));
    }
}