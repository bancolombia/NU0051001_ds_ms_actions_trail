package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.criteria.DateCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateCriterionTest {

    @Test
    void shouldCreateDateCriterionSuccessfully() {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now();
        SearchValue<LocalDate> searchValue = new SearchValue<>(fromDate, toDate);
        DateCriterion dateCriterion = new DateCriterion(fromDate, toDate);

        assertEquals("date", dateCriterion.getCode());
        assertEquals(searchValue, dateCriterion.getSearchValue());
    }

    @Test
    void shouldThrowInvalidSearchRangeExceptionWhenInitialDateIsMajorThanFinalDate() {
        LocalDate fromDate = LocalDate.now().plusDays(1);
        LocalDate toDate = LocalDate.now();
        BusinessException businessException = assertThrows(BusinessException.class, () -> new DateCriterion(fromDate, toDate));
        assertEquals(BusinessErrorMessage.INVALID_SEARCH_RANGE, businessException.getBusinessErrorMessage());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        LocalDate date = LocalDate.now();

        assertThrows(NullPointerException.class, () -> new DateCriterion(null, date) {
        });
        assertThrows(NullPointerException.class, () -> new DateCriterion(date, null) {
        });
        assertThrows(NullPointerException.class, () -> new DateCriterion(null, null) {
        });
    }
}
