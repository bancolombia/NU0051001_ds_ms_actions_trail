package co.com.bancolombia.search;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnySearchCriterionTest {

    @Test
    void shouldCreateSearchCriterionSuccessfully() {
        String code = "1";
        SearchValue<Integer> searchValue = new SearchValue<>(1, ComparisonOperator.EQUALS);
        AnySearchCriterion<Integer> anySearchCriterion = new AnySearchCriterion<>(code, searchValue) {
        };

        assertEquals(code, anySearchCriterion.getCode());
        assertEquals(searchValue, anySearchCriterion.getSearchValue());
    }

    @Test
    void shouldTwoAnySearchCriterionEqualsWhenHaveSameCode() {
        String code = "1";
        SearchValue<Integer> searchValue = new SearchValue<>(1, ComparisonOperator.EQUALS);

        AnySearchCriterion<Integer> anySearchCriterion = new AnySearchCriterion<>(code, searchValue) {
        };
        AnySearchCriterion<Integer> anySearchCriterion2 = new AnySearchCriterion<>(code, searchValue) {
        };

        assertEquals(anySearchCriterion2, anySearchCriterion);
    }

    @Test
    void shouldTwoAnySearchCriterionEqualsWhenIsTheSame() {
        String code = "1";
        SearchValue<Integer> searchValue = new SearchValue<>(1, ComparisonOperator.EQUALS);

        AnySearchCriterion<Integer> anySearchCriterion = new AnySearchCriterion<>(code, searchValue) {
        };

        assertEquals(anySearchCriterion, anySearchCriterion);
    }

    @Test
    void shouldReturnFalseWhenADifferentClassInstanceIsProvided() {
        String code = "1";
        SearchValue<Integer> searchValue = new SearchValue<>(1, ComparisonOperator.EQUALS);

        AnySearchCriterion<?> anySearchCriterion = new AnySearchCriterion<>(code, searchValue) {
        };

        Object object = "test";
        assertNotEquals(anySearchCriterion, object);
    }

    @Test
    void shouldValidateHashCodeSuccessfully() {
        String code = "1";
        SearchValue<Integer> searchValue = new SearchValue<>(1, ComparisonOperator.EQUALS);

        AnySearchCriterion<Integer> anySearchCriterion = new AnySearchCriterion<>(code, searchValue) {
        };
        AnySearchCriterion<Integer> anySearchCriterion2 = new AnySearchCriterion<>(code, searchValue) {
        };

        assertEquals(anySearchCriterion2.hashCode(), anySearchCriterion.hashCode());
    }

    @Test
    void shouldValidateGettersBeFinal() throws NoSuchMethodException {
        Method getCode = AnySearchCriterion.class.getMethod("getCode");
        Method getSearchValue = AnySearchCriterion.class.getMethod("getSearchValue");
        assertTrue(Modifier.isFinal(getCode.getModifiers()));
        assertTrue(Modifier.isFinal(getSearchValue.getModifiers()));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        String code = "test";
        SearchValue<Integer> searchValue = new SearchValue<>(1, ComparisonOperator.EQUALS);

        assertThrows(NullPointerException.class, () -> new AnySearchCriterion<>(null, searchValue) {
        });
        assertThrows(NullPointerException.class, () -> new AnySearchCriterion<>(code, null) {
        });
    }
}