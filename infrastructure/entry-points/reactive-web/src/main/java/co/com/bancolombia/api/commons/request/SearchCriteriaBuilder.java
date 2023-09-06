package co.com.bancolombia.api.commons.request;

import co.com.bancolombia.api.actionsearch.dto.request.SearchCriteriaRequestDTO.DateRange;
import co.com.bancolombia.api.actionsearch.dto.request.SearchCriteriaRequestDTO.SearchCriteria;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.criteria.DateCriterion;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SearchCriteriaBuilder {

    private final ObjectMapper objectMapper;

    private static final String STATE = "state";
    private static final String TYPE = "type";
    private static final String TRANSACTION_NAME = "name";
    private static final String BANK_ENTITY = "bankEntity";
    private static final String PRODUCT_TYPE = "productType";
    private static final String PRODUCT_NUMBER = "productNumber";
    private static final String USER_IDENTIFICATION_TYPE = "userIdentificationType";
    private static final String USER_IDENTIFICATION_NUMBER = "userIdentificationNumber";
    private static final String USER_FULL_NAME = "userFullName";
    private static final String DATE_RANGE = "dateRange";
    private static final String STATE_CRITERION_CLASS = "StateCriterion";
    private static final String TYPE_CRITERION_CLASS = "TypeCriterion";
    private static final String TRANSACTION_NAME_CRITERION_CLASS = "TransactionNameCriterion";
    private static final String BANK_ENTITY_CRITERION_CLASS = "BankEntityCriterion";
    private static final String PRODUCT_TYPE_CRITERION_CLASS = "ProductTypeCriterion";
    private static final String PRODUCT_NUMBER_CRITERION_CLASS = "ProductNumberCriterion";
    private static final String USER_IDENTIFICATION_TYPE_CRITERION_CLASS = "UserIdentificationTypeCriterion";
    private static final String USER_IDENTIFICATION_NUMBER_CRITERION_CLASS = "UserIdentificationNumberCriterion";
    private static final String USER_FULL_NAME_CRITERION_CLASS = "UserFullNameCriterion";
    private static final String CLASS_PACKAGE = "co.com.bancolombia.model.search.criteria.";


    public <T extends AnySearchCriterion<?>> Mono<Set<T>> getSearchCriteria(SearchCriteria searchCriteria) {

        Map<String, Object> mapSearchCriteria = objectMapper.convertValue(searchCriteria, Map.class);
        if(searchCriteria.getProduct()!=null){
            mapSearchCriteria.put(PRODUCT_TYPE, searchCriteria.getProduct().getType());
            mapSearchCriteria.put(PRODUCT_NUMBER, searchCriteria.getProduct().getNumber());
        }
        if(searchCriteria.getUser() != null) {
            mapSearchCriteria.put(USER_FULL_NAME, searchCriteria.getUser().getName());
            if(searchCriteria.getUser().getIdentification() != null) {
                mapSearchCriteria.put(USER_IDENTIFICATION_TYPE, searchCriteria.getUser().getIdentification().getType());
                mapSearchCriteria.put(USER_IDENTIFICATION_NUMBER,
                        searchCriteria.getUser().getIdentification().getNumber());
            }
        }
        mapSearchCriteria.values().removeAll(Collections.singleton(null));

        return Mono.just((Set<T>) getSearchCriteria(mapSearchCriteria));
    }

    private Set<AnySearchCriterion<?>> getSearchCriteria(Map<String, Object> criteria) {

        var setCriteria = new HashSet<AnySearchCriterion<?>>();
        criteria.forEach((key, value) -> setCriteria.addAll(produceCriteria(key, value)));
        return setCriteria;
    }

    @SuppressWarnings("fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    private <T> HashSet<AnySearchCriterion<T>> produceCriteria(String key, Object value) {
        var set = new HashSet<AnySearchCriterion<T>>();
        Map<String, String> searchCriterionMap = createSearchCriterionMap();

        if(key.equals(DATE_RANGE)) {
            var dateRange = getDateRange(value);
            set.add((AnySearchCriterion<T>) new DateCriterion(dateRange.getStartDate(), dateRange.getEndDate()));
        } else {
            try {
                var criterionClass = searchCriterionMap.get(key);
                if(criterionClass != null) {
                    set.add((AnySearchCriterion<T>) Class.forName(CLASS_PACKAGE + criterionClass)
                            .getDeclaredConstructor(String.class).newInstance((String) value));
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException | InvocationTargetException e) {
                throw new TechnicalException(e, TechnicalErrorMessage.UNEXPECTED_EXCEPTION);
            }
        }
        return set;
    }

    private Map<String, String> createSearchCriterionMap() {
        Map<String, String> searchCriterionMap = new HashMap<>() {};
        searchCriterionMap.put(STATE, STATE_CRITERION_CLASS);
        searchCriterionMap.put(TYPE, TYPE_CRITERION_CLASS);
        searchCriterionMap.put(TRANSACTION_NAME, TRANSACTION_NAME_CRITERION_CLASS);
        searchCriterionMap.put(BANK_ENTITY, BANK_ENTITY_CRITERION_CLASS);
        searchCriterionMap.put(PRODUCT_TYPE, PRODUCT_TYPE_CRITERION_CLASS);
        searchCriterionMap.put(PRODUCT_NUMBER, PRODUCT_NUMBER_CRITERION_CLASS);
        searchCriterionMap.put(USER_IDENTIFICATION_TYPE, USER_IDENTIFICATION_TYPE_CRITERION_CLASS);
        searchCriterionMap.put(USER_IDENTIFICATION_NUMBER, USER_IDENTIFICATION_NUMBER_CRITERION_CLASS);
        searchCriterionMap.put(USER_FULL_NAME, USER_FULL_NAME_CRITERION_CLASS);
        return searchCriterionMap;
    }

    private DateRange getDateRange(Object value) {
        return objectMapper.convertValue(value, DateRange.class);
    }

}