package co.com.bancolombia.event.search.dto.criteria;

import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchCriteriaPaginationDTOTest {

    @Test
    void shouldCreateSearchCriteriaPaginationDTOSuccessfully() {

        Integer pageNumber = 1;
        Integer pageSize = 1;
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);

        Map<String, String> criteriaMap = new HashMap<>();
        criteriaMap.put("Test", "1");

        SearchValue<Integer> searchValueInteger = new SearchValue<>(1, ComparisonOperator.EQUALS);
        AnySearchCriterion<?> anySearchCriterion = new AnySearchCriterion<Integer>("Test", searchValueInteger) {
        };
        Set<AnySearchCriterion<?>> searchCriteria = Set.of(anySearchCriterion);

        SearchCriteriaPaginationDTO searchCriteriaPaginationDTO = new SearchCriteriaPaginationDTO(
                searchCriteria, pageRequest);

        assertEquals(pageNumber, searchCriteriaPaginationDTO.getPagination().getNumber());
        assertEquals(pageSize, searchCriteriaPaginationDTO.getPagination().getSize());
        assertEquals(criteriaMap.get("Test"), searchCriteriaPaginationDTO.getCriteria().get("Test"));
    }

    @Test
    void shouldCreateSearchCriteriaPaginationDTOWithDateCriterionSuccessfully() {

        String fromValue = "20-10-2022";
        String toValue = "30-10-2022";
        String expectedResult = "{fromValue=" + fromValue + ", toValue=" + toValue + "}";

        PageRequest pageRequest = new PageRequest(1, 1);
        SearchValue<String> searchValue = new SearchValue<>(fromValue, toValue);
        AnySearchCriterion<?> anySearchCriterion = new AnySearchCriterion<>("Test", searchValue) {
        };
        Set<AnySearchCriterion<?>> searchCriteria = Set.of(anySearchCriterion);

        SearchCriteriaPaginationDTO searchCriteriaPaginationDTO = new SearchCriteriaPaginationDTO(
                searchCriteria, pageRequest);

        assertEquals(expectedResult, searchCriteriaPaginationDTO.getCriteria().get("Test"));
    }
}