package co.com.bancolombia.model.search.criteria;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import lombok.NonNull;

public class StateCriterion extends AnySearchCriterion<String> {

    private static final String SEARCH_STATE = "state";

    private StateCriterion(@NonNull SearchValue<String> searchValue) {
        super(SEARCH_STATE, searchValue);
    }

    public StateCriterion(@NonNull String value, @NonNull ComparisonOperator operator) {
        this(new SearchValue<>(value, operator));
    }

    public StateCriterion(@NonNull String value) {
        this(new SearchValue<>(value, ComparisonOperator.EQUALS));
    }

}