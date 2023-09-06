package co.com.bancolombia.model.search.criteria;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import lombok.NonNull;

/**
 * Criterion used when it is necessary to search for actions that are part of
 *  an entitlement flow
 */
public class TransactionTrackerCriterion extends AnySearchCriterion<String> {
    private static final String SEARCH_CODE = "transactionTracker";


    public TransactionTrackerCriterion(@NonNull String value) {
        super(SEARCH_CODE,new SearchValue<>(value, ComparisonOperator.EQUALS));
    }
}
