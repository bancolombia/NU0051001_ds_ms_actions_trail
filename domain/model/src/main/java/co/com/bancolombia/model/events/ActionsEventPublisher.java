package co.com.bancolombia.model.events;

import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;

import java.util.Set;

/**
 * Communicates the domain with the event publisher in charge of emitting the events that have occurred
 * for the actions search to the subscribers
 */
public interface ActionsEventPublisher {

    void emitSuccessfulPaginatedActionsSearchEvent(Context context, Set<AnySearchCriterion<?>> searchCriteria,
                                                   PageRequest pageRequest);

    void emitFailedPaginatedActionsSearchEvent(Context context, Set<AnySearchCriterion<?>> searchCriteria,
                                               PageRequest pageRequest, Throwable error);

    void emitSuccessfulActionsSearchEvent(Context context, TransactionTrackerCriterion transactionTrackerCriterion);

    void emitFailedActionsSearchEvent(Context context, TransactionTrackerCriterion transactionTrackerCriterion,
                                      Throwable error);

    void emitSuccessfulActionSearchEvent(Context context, TransactionIdCriterion transactionIdCriterion);

    void emitFailedActionSearchEvent(Context context, TransactionIdCriterion transactionIdCriterion,
                                     Throwable error);
}
