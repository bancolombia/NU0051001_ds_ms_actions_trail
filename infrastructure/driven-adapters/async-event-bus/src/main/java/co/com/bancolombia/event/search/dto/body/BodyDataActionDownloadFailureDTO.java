package co.com.bancolombia.event.search.dto.body;

import co.com.bancolombia.event.search.dto.commons.ErrorDTO;
import co.com.bancolombia.event.search.dto.commons.MetaDTO;
import co.com.bancolombia.model.search.AnySearchCriterion;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class BodyDataActionDownloadFailureDTO implements Serializable{
    @NonNull
    private final MetaDTO meta;
    @NonNull private final BodyDataActionDownloadFailureDTO.Request request;
    @NonNull private final BodyDataActionDownloadFailureDTO.Response response;

    @Getter
    @RequiredArgsConstructor
    @EqualsAndHashCode
    public static class Request implements Serializable {
        @NonNull private final String format;
        @NonNull private final Map<String, String> searchCriteria;

        public Request(String format, Set<AnySearchCriterion<?>> searchCriteria){
            this.format = format;
            this.searchCriteria = toMap(searchCriteria);
        }
        private Map<String, String> toMap(Set<AnySearchCriterion<?>> searchCriteria) {

            return searchCriteria.stream()
                    .collect(
                            Collectors.toMap(AnySearchCriterion::getCode,
                                    criterion-> criterion.getSearchValue().isSingleValue() ?
                                            criterion.getSearchValue().getValue().toString() :
                                            criterion.getSearchValue().getRangeValue().toString()
                            )
                    );
        }
    }

    @Getter
    @RequiredArgsConstructor
    @EqualsAndHashCode
    public static class Response implements Serializable {
        @NonNull private final ErrorDTO state;
    }
}
