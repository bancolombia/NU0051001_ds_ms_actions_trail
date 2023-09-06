package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.search.criteria.UserIdentificationTypeCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserIdentificationTypeCriterionTest {

    private static final String USER_IDENTIFICATION_TYPE = "MDM_1011";

    @Test
    void shouldCreateUserIdentificationTypeCriterionSuccessfully() {
        UserIdentificationTypeCriterion userIdentificationType = new UserIdentificationTypeCriterion(
                USER_IDENTIFICATION_TYPE);

        assertEquals("userIdentificationType", userIdentificationType.getCode());
        assertEquals(USER_IDENTIFICATION_TYPE, userIdentificationType.getSearchValue().getValue());
    }

    @Test
    void shouldThrowUserIdentificationTypeCriterionWithValueNull() {

        Exception exception = assertThrows(NullPointerException.class, () -> new UserIdentificationTypeCriterion(null));
        assertEquals("value is marked non-null but is null", exception.getMessage());
        assertThrows(NullPointerException.class, () -> new UserIdentificationTypeCriterion(null, null));
    }

    @Test
    void shouldCreateUserIdentificationTypeCriterionWithComparatorSuccessfully() {
        UserIdentificationTypeCriterion userIdentificationType = new UserIdentificationTypeCriterion(
                USER_IDENTIFICATION_TYPE, ComparisonOperator.EQUALS);

        assertEquals("userIdentificationType", userIdentificationType.getCode());
        assertEquals(USER_IDENTIFICATION_TYPE, userIdentificationType.getSearchValue().getValue());
    }

}