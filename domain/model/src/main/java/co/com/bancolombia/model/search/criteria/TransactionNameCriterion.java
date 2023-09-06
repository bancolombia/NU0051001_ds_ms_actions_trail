package co.com.bancolombia.model.search.criteria;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import lombok.NonNull;

public class TransactionNameCriterion extends AnySearchCriterion<String> {

    private static final String SEARCH_NAME = "transactionName";

    private TransactionNameCriterion(@NonNull SearchValue<String> searchValue) {
        super(SEARCH_NAME, searchValue);
    }

    public TransactionNameCriterion(@NonNull String value, @NonNull ComparisonOperator operator) {
        this(new SearchValue<>(value, operator));
    }

    public TransactionNameCriterion(@NonNull String value) {
        this(new SearchValue<>(value, ComparisonOperator.EQUALS));
    }

}