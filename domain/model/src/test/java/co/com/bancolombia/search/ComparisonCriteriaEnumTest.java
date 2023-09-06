package co.com.bancolombia.search;

import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComparisonCriteriaEnumTest {

    @Test
    void shouldValidateComparisonCriteriaTypesSuccessfully() {
        assertEquals("EQUALS", ComparisonOperator.EQUALS.getOperator());
        assertEquals("GREATER_THAN", ComparisonOperator.GREATER_THAN.getOperator());
        assertEquals("LOWER_THAN", ComparisonOperator.LOWER_THAN.getOperator());
        assertEquals("RANGE", ComparisonOperator.RANGE.getOperator());
    }
}