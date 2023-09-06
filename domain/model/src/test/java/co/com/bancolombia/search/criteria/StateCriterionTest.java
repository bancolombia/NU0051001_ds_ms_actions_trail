package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.search.criteria.StateCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateCriterionTest {

    private static final String STATE = "ACTIVE";

    @Test
    void shouldCreateStateCriterionSuccessfully() {
        StateCriterion stateCriterion = new StateCriterion(STATE);

        assertEquals("state", stateCriterion.getCode());
        assertEquals(STATE, stateCriterion.getSearchValue().getValue());
    }

    @Test
    void shouldThrowStateCriterionWithValueNull() {

        Exception exception = assertThrows(NullPointerException.class, () -> new StateCriterion(null));
        assertEquals("value is marked non-null but is null", exception.getMessage());
        assertThrows(NullPointerException.class, () -> new StateCriterion(null, null));
    }

    @Test
    void shouldCreateStateCriterionWithComparatorSuccessfully() {
        StateCriterion stateCriterion = new StateCriterion(STATE,
                ComparisonOperator.EQUALS);

        assertEquals("state", stateCriterion.getCode());
        assertEquals(STATE, stateCriterion.getSearchValue().getValue());
    }

}