package co.com.bancolombia.search;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.SearchRangeValue;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchValueTest {

    @Test
    void shouldCreateSearchValueSuccessfully() {
        SearchValue<Integer> searchValue = new SearchValue<>(1, ComparisonOperator.EQUALS);

        assertEquals(1, searchValue.getValue());
        assertEquals(ComparisonOperator.EQUALS, searchValue.getOperator());
        assertNull(searchValue.getRangeValue());
        assertTrue(searchValue.isSingleValue());
        assertFalse(searchValue.isMultipleValue());
    }

    @Test
    void shouldCreateSearchRangeValueSuccessfully() {
        SearchValue<Integer> searchRangeValue = new SearchValue<>(1, 10);
        SearchRangeValue<Integer> searchRangeValueExpected = new SearchRangeValue<>(1, 10);

        assertEquals(searchRangeValueExpected, searchRangeValue.getRangeValue());
        assertEquals(ComparisonOperator.RANGE, searchRangeValue.getOperator());
        assertNull(searchRangeValue.getValue());
        assertTrue(searchRangeValue.isMultipleValue());
        assertFalse(searchRangeValue.isSingleValue());
    }

    @Test
    void shouldThrowInvalidOperatorBusinessExceptionWhenSearchValueHasOneValueAndOperatorIsRange() {
        BusinessException businessException = assertThrows(BusinessException.class, () -> new SearchValue<>(1, ComparisonOperator.RANGE));
        assertEquals(BusinessErrorMessage.INVALID_OPERATOR, businessException.getBusinessErrorMessage());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        assertThrows(NullPointerException.class, () -> new SearchValue<>(null, ComparisonOperator.EQUALS));
        assertThrows(NullPointerException.class, () -> new SearchValue<>(1, null));
        assertThrows(NullPointerException.class, () -> new SearchValue<>(null, 1));
        assertThrows(NullPointerException.class, () -> new SearchValue<>(1, null));
    }
}