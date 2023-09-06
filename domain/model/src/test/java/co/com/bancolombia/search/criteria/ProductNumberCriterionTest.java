package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.search.criteria.ProductNumberCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductNumberCriterionTest {

    private static final String PRODUCT_NUMBER = "9877-98787";

    @Test
    void shouldCreateProductNumberCriterionSuccessfully() {
        ProductNumberCriterion productNumberCriterion = new ProductNumberCriterion(PRODUCT_NUMBER);

        assertEquals("productNumber", productNumberCriterion.getCode());
        assertEquals(PRODUCT_NUMBER, productNumberCriterion.getSearchValue().getValue());
    }

    @Test
    void shouldThrowProductNumberCriterionWithValueNull() {

        Exception exception = assertThrows(NullPointerException.class, () -> new ProductNumberCriterion(null));
        assertEquals("value is marked non-null but is null", exception.getMessage());
        assertThrows(NullPointerException.class, () -> new ProductNumberCriterion(null, null));
    }

    @Test
    void shouldCreateProductNumberCriterionWithComparatorSuccessfully() {
        ProductNumberCriterion productNumberCriterion = new ProductNumberCriterion(PRODUCT_NUMBER,
                ComparisonOperator.EQUALS);

        assertEquals("productNumber", productNumberCriterion.getCode());
        assertEquals(PRODUCT_NUMBER, productNumberCriterion.getSearchValue().getValue());
    }

}