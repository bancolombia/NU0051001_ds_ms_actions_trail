package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.search.criteria.BankEntityCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankEntityCriterionTest {

    private static final String BANK_ENTITY = "bank_colombia";

    @Test
    void shouldCreateBankEntityCriterionSuccessfully() {
        BankEntityCriterion bankEntityCriterion = new BankEntityCriterion(BANK_ENTITY);

        assertEquals("bankEntity", bankEntityCriterion.getCode());
        assertEquals(BANK_ENTITY, bankEntityCriterion.getSearchValue().getValue());
    }

    @Test
    void shouldThrowBankEntityCriterionWithValueNull() {

        Exception exception = assertThrows(NullPointerException.class, () -> new BankEntityCriterion(null));
        assertEquals("value is marked non-null but is null", exception.getMessage());
        assertThrows(NullPointerException.class, () -> new BankEntityCriterion(null, null));
    }

    @Test
    void shouldCreateBankEntityCriterionWithComparatorSuccessfully() {
        BankEntityCriterion bankEntityCriterion = new BankEntityCriterion(BANK_ENTITY, ComparisonOperator.EQUALS);

        assertEquals("bankEntity", bankEntityCriterion.getCode());
        assertEquals(BANK_ENTITY, bankEntityCriterion.getSearchValue().getValue());
    }

}
