package co.com.bancolombia.model.parameters;

import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

/**
 * ProductType represents the different available type of products that the customer has and uses.
 */
@Value
public class ProductType {

    @NonNull String name;
    @NonNull List<String> productNumbers;

    public List<String> getProductNumbers() {
        return Collections.unmodifiableList(this.productNumbers);
    }
}
