package co.com.bancolombia.model.search.criteria;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import lombok.NonNull;

/**
 * Criterion used when it is necessary to search a specific action
 */
public class TransactionIdCriterion extends AnySearchCriterion<String> {

    private static final String SEARCH_CODE = "transactionId";

    public TransactionIdCriterion(@NonNull String id) {
        super(SEARCH_CODE, new SearchValue<>(id, ComparisonOperator.EQUALS));
    }
}
