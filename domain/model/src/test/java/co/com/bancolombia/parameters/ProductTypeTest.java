package co.com.bancolombia.parameters;

import co.com.bancolombia.model.parameters.ProductType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTypeTest {

    @Test
    void shouldCreateProductTypeSuccessfully() {
        String name = "test";
        List<String> productNumbers = new ArrayList<>();

        ProductType productType = new ProductType(name, productNumbers);
        assertEquals(name, productType.getName());
        assertEquals(productNumbers, productType.getProductNumbers());
    }

    @Test
    void shouldThrowUnsupportedOperationExceptionWhenAddingElementToProductNumbersList() {
        String name = "test";
        List<String> productNumbers = new ArrayList<>();

        List<String> unmodifiableProductNumbers = new ProductType(name, productNumbers).getProductNumbers();
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableProductNumbers.add("1"));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        List<String> productNumbers = new ArrayList<>();

        assertThrows(NullPointerException.class, () -> new ProductType(null, productNumbers));
        assertThrows(NullPointerException.class, () -> new ProductType("test", null));
    }
}