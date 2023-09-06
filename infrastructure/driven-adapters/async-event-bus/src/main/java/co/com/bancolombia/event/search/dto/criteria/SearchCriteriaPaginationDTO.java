package co.com.bancolombia.event.search.dto.criteria;

import co.com.bancolombia.event.search.dto.commons.PageRequestDTO;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.search.AnySearchCriterion;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class SearchCriteriaPaginationDTO implements Serializable {

    @NonNull private final PageRequestDTO pagination;
    @NonNull private final Map<String, String> criteria;

    public SearchCriteriaPaginationDTO(Set<AnySearchCriterion<?>> searchCriteria, PageRequest pageRequest) {
        this.pagination = new PageRequestDTO(pageRequest);
        this.criteria = toMap(searchCriteria);
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