package co.com.bancolombia.event.search.dto.body;

import co.com.bancolombia.event.search.dto.commons.ErrorDTO;
import co.com.bancolombia.event.search.dto.commons.MetaDTO;
import co.com.bancolombia.event.search.dto.criteria.SearchCriteriaPaginationDTO;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class BodyDataActionQueryPageFailureDTO implements Serializable {

    @NonNull private final MetaDTO meta;
    @NonNull private final Request request;
    @NonNull private final Response response;

    @Getter
    @RequiredArgsConstructor
    public static class Request implements Serializable {
        @NonNull private final SearchCriteriaPaginationDTO searchCriteria;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Response implements Serializable {
        @NonNull private final ErrorDTO state;
    }
}
