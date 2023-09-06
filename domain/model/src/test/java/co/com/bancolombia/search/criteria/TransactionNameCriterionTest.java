package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.search.criteria.TransactionNameCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionNameCriterionTest {

    private static final String NAME_ACTION = "authentication";

    @Test
    void shouldCreateNameCriterionSuccessfully() {
        TransactionNameCriterion customerTransactionNameCriterion = new TransactionNameCriterion(NAME_ACTION);

        assertEquals("transactionName", customerTransactionNameCriterion.getCode());
        assertEquals(NAME_ACTION, customerTransactionNameCriterion.getSearchValue().getValue());
    }

    @Test
    void shouldThrowNameCriterionWithValueNull() {

        Exception exception = assertThrows(NullPointerException.class, () -> new TransactionNameCriterion(null));
        assertEquals("value is marked non-null but is null", exception.getMessage());
        assertThrows(NullPointerException.class, () -> new TransactionNameCriterion(null, null));
    }

    @Test
    void shouldCreateNameCriterionWithComparatorSuccessfully() {
        TransactionNameCriterion customerTransactionNameCriterion = new TransactionNameCriterion(NAME_ACTION, ComparisonOperator.EQUALS);

        assertEquals("transactionName", customerTransactionNameCriterion.getCode());
        assertEquals(NAME_ACTION, customerTransactionNameCriterion.getSearchValue().getValue());
    }
}
