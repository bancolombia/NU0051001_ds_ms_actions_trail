package co.com.bancolombia.search.criteria;

import co.com.bancolombia.model.search.criteria.ProductTypeCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTypeCriterionTest {

    private static final String PRODUCT_TYPE = "9877-98787";

    @Test
    void shouldCreateProductTypeCriterionSuccessfully() {
        ProductTypeCriterion productTypeCriterion = new ProductTypeCriterion(PRODUCT_TYPE);

        assertEquals("productType", productTypeCriterion.getCode());
        assertEquals(PRODUCT_TYPE, productTypeCriterion.getSearchValue().getValue());
    }

    @Test
    void shouldThrowProductTypeCriterionWithValueNull() {

        Exception exception = assertThrows(NullPointerException.class, () -> new ProductTypeCriterion(null));
        assertEquals("value is marked non-null but is null", exception.getMessage());
        assertThrows(NullPointerException.class, () -> new ProductTypeCriterion(null, null));
    }

    @Test
    void shouldCreateProductTypeCriterionWithComparatorSuccessfully() {
        ProductTypeCriterion productTypeCriterion = new ProductTypeCriterion(PRODUCT_TYPE,
                ComparisonOperator.EQUALS);

        assertEquals("productType", productTypeCriterion.getCode());
        assertEquals(PRODUCT_TYPE, productTypeCriterion.getSearchValue().getValue());
    }

}