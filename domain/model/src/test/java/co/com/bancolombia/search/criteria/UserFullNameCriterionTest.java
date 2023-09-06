package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.search.criteria.UserFullNameCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserFullNameCriterionTest {

    private static final String NAME_OF_USER = "Juliano";

    @Test
    void shouldCreateNameUserCriterionSuccessfully() {
        UserFullNameCriterion userFullNameCriterion = new UserFullNameCriterion(NAME_OF_USER);

        assertEquals("userFullName", userFullNameCriterion.getCode());
        assertEquals(NAME_OF_USER, userFullNameCriterion.getSearchValue().getValue());
    }

    @Test
    void shouldThrowNameUserCriterionWithValueNull() {

        Exception exception = assertThrows(NullPointerException.class, () -> new UserFullNameCriterion(null));
        assertEquals("value is marked non-null but is null", exception.getMessage());
        assertThrows(NullPointerException.class, () -> new UserFullNameCriterion(null, null));
    }

    @Test
    void shouldCreateNameUserCriterionWithComparatorSuccessfully() {
        UserFullNameCriterion userFullNameCriterion = new UserFullNameCriterion(NAME_OF_USER, ComparisonOperator.EQUALS);

        assertEquals("userFullName", userFullNameCriterion.getCode());
        assertEquals(NAME_OF_USER, userFullNameCriterion.getSearchValue().getValue());
    }

}