package co.com.bancolombia.model.search.criteria;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import lombok.NonNull;

public class UserIdentificationNumberCriterion extends AnySearchCriterion<String> {

    private static final String SEARCH_USER_IDENTIFICATION_NUMBER = "userIdentificationNumber";

    private UserIdentificationNumberCriterion(@NonNull SearchValue<String> searchValue) {
        super(SEARCH_USER_IDENTIFICATION_NUMBER, searchValue);
    }

    public UserIdentificationNumberCriterion(@NonNull String value, @NonNull ComparisonOperator operator) {
        this(new SearchValue<>(value, operator));
    }

    public UserIdentificationNumberCriterion(@NonNull String value) {
        this(new SearchValue<>(value, ComparisonOperator.EQUALS));
    }
}
