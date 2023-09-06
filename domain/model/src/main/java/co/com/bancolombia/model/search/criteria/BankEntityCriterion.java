package co.com.bancolombia.model.search.criteria;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import lombok.NonNull;

public class BankEntityCriterion extends AnySearchCriterion<String> {

    private static final String SEARCH_BANK_ENTITY = "bankEntity";

    private BankEntityCriterion(@NonNull SearchValue<String> searchValue) {
        super(SEARCH_BANK_ENTITY, searchValue);
    }

    public BankEntityCriterion(@NonNull String value, @NonNull ComparisonOperator operator) {
        this(new SearchValue<>(value, operator));
    }

    public BankEntityCriterion(@NonNull String value) {
        this(new SearchValue<>(value, ComparisonOperator.EQUALS));
    }

}