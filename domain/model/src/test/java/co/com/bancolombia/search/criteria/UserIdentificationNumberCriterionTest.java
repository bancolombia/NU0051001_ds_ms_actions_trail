package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.search.criteria.UserIdentificationNumberCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserIdentificationNumberCriterionTest {

    private static final String USER_IDENTIFICATION_NUMBER = "101100";

    @Test
    void shouldCreateUserIdentificationNumberCriterionSuccessfully() {
        UserIdentificationNumberCriterion userIdentificationNumber = new UserIdentificationNumberCriterion(
                USER_IDENTIFICATION_NUMBER);

        assertEquals("userIdentificationNumber", userIdentificationNumber.getCode());
        assertEquals(USER_IDENTIFICATION_NUMBER, userIdentificationNumber.getSearchValue().getValue());
    }

    @Test
    void shouldThrowUserIdentificationNumberCriterionWithValueNull() {

        Exception exception = assertThrows(NullPointerException.class, () ->
                new UserIdentificationNumberCriterion(null));
        assertEquals("value is marked non-null but is null", exception.getMessage());
        assertThrows(NullPointerException.class, () ->
                new UserIdentificationNumberCriterion(null, null));
    }

    @Test
    void shouldCreateUserIdentificationNumberCriterionWithComparatorSuccessfully() {
        UserIdentificationNumberCriterion userIdentificationNumber = new UserIdentificationNumberCriterion(
                USER_IDENTIFICATION_NUMBER, ComparisonOperator.EQUALS);

        assertEquals("userIdentificationNumber", userIdentificationNumber.getCode());
        assertEquals(USER_IDENTIFICATION_NUMBER, userIdentificationNumber.getSearchValue().getValue());
    }
}
