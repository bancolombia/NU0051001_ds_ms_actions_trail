package co.com.bancolombia.event.search.dto.body;

import co.com.bancolombia.event.search.dto.commons.ErrorDTO;
import co.com.bancolombia.event.search.dto.commons.MetaDTO;
import co.com.bancolombia.event.search.dto.criteria.SearchCriteriaDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class BodyDataActionQueryFailureDTO implements Serializable {

    @NonNull private final MetaDTO meta;
    @NonNull private final Request request;
    @NonNull private final Response response;

    @Getter
    @RequiredArgsConstructor
    @EqualsAndHashCode
    public static class Request implements Serializable {
        @NonNull private final SearchCriteriaDTO searchCriteria;
    }

    @Getter
    @RequiredArgsConstructor
    @EqualsAndHashCode
    public static class Response implements Serializable {
        @NonNull private final ErrorDTO state;
    }
}