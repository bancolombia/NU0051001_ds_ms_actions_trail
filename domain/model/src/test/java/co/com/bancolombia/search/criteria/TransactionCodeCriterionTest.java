package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.search.criteria.TransactionCodeCriterion;
import org.junit.jupiter.api.Test;

import static co.com.bancolombia.model.search.enums.ComparisonOperator.EQUALS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionCodeCriterionTest {

    @Test
    void shouldCreateTransactionCodeCriterionSuccessfully() {
        TransactionCodeCriterion transactionCodeCriterion = new TransactionCodeCriterion("1");
        assertEquals("transactionCode", transactionCodeCriterion.getCode());
        assertEquals("1",transactionCodeCriterion.getSearchValue().getValue());
        assertEquals(EQUALS, transactionCodeCriterion.getSearchValue().getOperator());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> new TransactionCodeCriterion(null) {
        });
    }
}
