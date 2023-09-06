package co.com.bancolombia.model.search.criteria;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import lombok.NonNull;

public class ProductTypeCriterion extends AnySearchCriterion<String> {

    private static final String SEARCH_PRODUCT_TYPE = "productType";

    private ProductTypeCriterion(@NonNull SearchValue<String> searchValue) {
        super(SEARCH_PRODUCT_TYPE, searchValue);
    }

    public ProductTypeCriterion(@NonNull String value, @NonNull ComparisonOperator operator) {
        this(new SearchValue<>(value, operator));
    }

    public ProductTypeCriterion(@NonNull String value) {
        this(new SearchValue<>(value, ComparisonOperator.EQUALS));
    }

}