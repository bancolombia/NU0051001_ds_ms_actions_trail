package co.com.bancolombia.model.actions;

import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.commons.PageSummary;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Communicates the domain with the repository in charge of searching and filtering all the actions
 * carried out by the users.
 */
public interface ActionsRepository {

    Mono<PageSummary<Action>> searchLastActions(Set<AnySearchCriterion<?>> searchCriteria,
                                                PageRequest pageRequest, Context context);

    Flux<Action> searchActions(TransactionTrackerCriterion transactionTrackerCriterion, Context context);

    Mono<Action> searchAction(TransactionIdCriterion transactionCodeCriterion, Context context);
}
