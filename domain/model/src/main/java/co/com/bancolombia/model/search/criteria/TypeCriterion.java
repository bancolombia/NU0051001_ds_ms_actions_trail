package co.com.bancolombia.model.search.criteria;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import lombok.NonNull;

public class TypeCriterion extends AnySearchCriterion<String> {

    private static final String SEARCH_TYPE = "type";

    private TypeCriterion(@NonNull SearchValue<String> searchValue) {
        super(SEARCH_TYPE, searchValue);
    }

    public TypeCriterion(@NonNull String value, @NonNull ComparisonOperator operator) {
        this(new SearchValue<>(value, operator));
    }

    public TypeCriterion(@NonNull String value) {
        this(new SearchValue<>(value, ComparisonOperator.EQUALS));
    }

}