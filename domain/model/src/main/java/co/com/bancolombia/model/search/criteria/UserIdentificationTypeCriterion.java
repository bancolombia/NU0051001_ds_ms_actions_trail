package co.com.bancolombia.model.search.criteria;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import lombok.NonNull;

public class UserIdentificationTypeCriterion extends AnySearchCriterion<String> {

    private static final String SEARCH_USER_IDENTIFICATION_TYPE = "userIdentificationType";

    private UserIdentificationTypeCriterion(@NonNull SearchValue<String> searchValue) {
        super(SEARCH_USER_IDENTIFICATION_TYPE, searchValue);
    }

    public UserIdentificationTypeCriterion(@NonNull String value, @NonNull ComparisonOperator operator) {
        this(new SearchValue<>(value, operator));
    }

    public UserIdentificationTypeCriterion(@NonNull String value) {
        this(new SearchValue<>(value, ComparisonOperator.EQUALS));
    }
}