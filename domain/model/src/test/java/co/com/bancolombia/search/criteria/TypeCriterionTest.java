package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.search.criteria.TypeCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TypeCriterionTest {

    private static final String TYPE = "NO_MONETARY";

    @Test
    void shouldCreateTypeCriterionSuccessfully() {
        TypeCriterion typeCriterion = new TypeCriterion(TYPE);

        assertEquals("type", typeCriterion.getCode());
        assertEquals(TYPE, typeCriterion.getSearchValue().getValue());
    }

    @Test
    void shouldThrowTypeCriterionWithValueNull() {

        Exception exception = assertThrows(NullPointerException.class, () -> new TypeCriterion(null));
        assertEquals("value is marked non-null but is null", exception.getMessage());
        assertThrows(NullPointerException.class, () -> new TypeCriterion(null, null));
    }

    @Test
    void shouldCreateTypeCriterionWithComparatorSuccessfully() {
        TypeCriterion typeCriterion = new TypeCriterion(TYPE,
                ComparisonOperator.EQUALS);

        assertEquals("type", typeCriterion.getCode());
        assertEquals(TYPE, typeCriterion.getSearchValue().getValue());
    }

}