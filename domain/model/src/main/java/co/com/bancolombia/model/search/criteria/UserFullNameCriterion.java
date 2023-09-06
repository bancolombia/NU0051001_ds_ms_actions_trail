package co.com.bancolombia.model.search.criteria;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import lombok.NonNull;

public class UserFullNameCriterion extends AnySearchCriterion<String> {

    private static final String SEARCH_USER_FULL_NAME = "userFullName";

    private UserFullNameCriterion(@NonNull SearchValue<String> searchValue) {
        super(SEARCH_USER_FULL_NAME, searchValue);
    }

    public UserFullNameCriterion(@NonNull String value, @NonNull ComparisonOperator operator) {
        this(new SearchValue<>(value, operator));
    }

    public UserFullNameCriterion(@NonNull String value) {
        this(new SearchValue<>(value, ComparisonOperator.EQUALS));
    }

}