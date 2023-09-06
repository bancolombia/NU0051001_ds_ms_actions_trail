package co.com.bancolombia.search;

import co.com.bancolombia.model.search.SearchRangeValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchRangeValueTest {

    private static final Integer MIN_RANGE = 1;
    private static final Integer MAX_RANGE = 10;

    @Test
    void shouldCreateSearchRangeValueSuccessfully() {
        SearchRangeValue<Integer> searchRangeValue = new SearchRangeValue<>(MIN_RANGE, MAX_RANGE);
        assertEquals(MIN_RANGE, searchRangeValue.getFromValue());
        assertEquals(MAX_RANGE, searchRangeValue.getToValue());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        assertThrows(NullPointerException.class, () -> new SearchRangeValue<>(null, MAX_RANGE));
        assertThrows(NullPointerException.class, () -> new SearchRangeValue<>(MIN_RANGE, null));
    }

    @Test
    void testToString() {
        String expectedString = "{fromValue=" + MIN_RANGE + ", toValue=" + MAX_RANGE + "}";

        SearchRangeValue<Integer> searchRangeValue = new SearchRangeValue<>(MIN_RANGE, MAX_RANGE);
        assertEquals(expectedString, searchRangeValue.toString());
    }
}