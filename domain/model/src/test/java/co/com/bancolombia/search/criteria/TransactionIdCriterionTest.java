package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionIdCriterionTest {

    @Test
    void shouldCreateTransactionIdCriterionSuccessfully() {
        SearchValue<String> searchValue = new SearchValue<>("1", ComparisonOperator.EQUALS);
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("1");

        assertEquals("transactionId", transactionIdCriterion.getCode());
        assertEquals(searchValue, transactionIdCriterion.getSearchValue());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        assertThrows(NullPointerException.class, () -> new TransactionIdCriterion(null) {
        });
    }
}
