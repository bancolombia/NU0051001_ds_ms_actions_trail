package co.com.bancolombia.source.actions.mapper;

import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.source.config.PersistenceProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static co.com.bancolombia.exception.technical.message.TechnicalErrorMessage.ERROR_GETTING_SEARCH_CODE_MAPPING;

@Component
public class SearchCriteriaCodeMapper {
    private final Map<String, String> searchCriteriaCodeMap;

    public SearchCriteriaCodeMapper(PersistenceProperties persistenceProperties) {
        this.searchCriteriaCodeMap = persistenceProperties.getSearchCriteriaCodesMapping();
    }

    public String getField(String searchCriterionCode) {
        return Optional.ofNullable(searchCriteriaCodeMap.get(searchCriterionCode))
                .orElseThrow(() -> new TechnicalException(null, ERROR_GETTING_SEARCH_CODE_MAPPING));
    }
}
