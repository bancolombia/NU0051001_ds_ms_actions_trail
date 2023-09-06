package co.com.bancolombia.event.search.dto.body;

import co.com.bancolombia.event.search.dto.commons.ErrorDTO;
import co.com.bancolombia.event.search.dto.commons.MetaDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class BodyDataActionHistoryGenerationFailureDTO implements Serializable {
    @NonNull private final MetaDTO meta;
    @NonNull private final BodyDataActionHistoryGenerationFailureDTO.Request request;
    @NonNull private final BodyDataActionHistoryGenerationFailureDTO.Response response;

    @Getter
    @RequiredArgsConstructor
    @EqualsAndHashCode
    public static class Request implements Serializable {
        @NonNull private final String transactionId;
        @NonNull private final String format;
    }

    @Getter
    @RequiredArgsConstructor
    @EqualsAndHashCode
    public static class Response implements Serializable {
        @NonNull private final ErrorDTO state;
    }
}
