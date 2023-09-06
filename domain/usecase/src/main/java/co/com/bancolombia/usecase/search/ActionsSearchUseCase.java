package co.com.bancolombia.usecase.search;

import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.actions.ActionsRepository;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.commons.PageSummary;
import co.com.bancolombia.model.events.ActionsEventPublisher;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.criteria.DateCriterion;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

public class ActionsSearchUseCase {

    private final ActionsRepository actionsRepository;
    private final ActionsEventPublisher actionsEventPublisher;
    private final Set<Class<? extends AnySearchCriterion>> mandatoryCriteria;

    public ActionsSearchUseCase(@NonNull ActionsRepository actionsRepository,
                                @NonNull ActionsEventPublisher actionsEventPublisher) {
        this.actionsRepository = actionsRepository;
        this.actionsEventPublisher = actionsEventPublisher;
        this.mandatoryCriteria = Set.of(DateCriterion.class);
    }

    public Mono<PageSummary<Action>> searchLastActions(@NonNull Set<AnySearchCriterion<?>> searchCriteria,
                                                       @NonNull PageRequest pageRequest, @NonNull Context context) {
        return Mono.just(searchCriteria.stream()
                        .map(Object::getClass)
                        .collect(Collectors.toSet())
                        .containsAll(mandatoryCriteria))
                .filter(containsMandatoryCriteria -> containsMandatoryCriteria)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new BusinessException(BusinessErrorMessage.MISSING_MANDATORY_CRITERIA))))
                .flatMap(res -> actionsRepository.searchLastActions(searchCriteria, pageRequest, context))
                .doOnSuccess(res ->
                        actionsEventPublisher
                                .emitSuccessfulPaginatedActionsSearchEvent(context, searchCriteria, pageRequest))
                .doOnError(error ->
                        actionsEventPublisher
                                .emitFailedPaginatedActionsSearchEvent(context, searchCriteria, pageRequest, error));
    }

    public Mono<Action> searchAction(@NonNull TransactionIdCriterion transactionIdCriterion,
                                     @NonNull Context context) {
        return actionsRepository.searchAction(transactionIdCriterion, context)
                .doOnSuccess(res ->
                        actionsEventPublisher.emitSuccessfulActionSearchEvent(context, transactionIdCriterion))
                .doOnError(error ->
                        actionsEventPublisher.emitFailedActionSearchEvent(context, transactionIdCriterion, error));
    }

    public Flux<Action> searchActions(@NonNull TransactionTrackerCriterion transactionTrackerCriterion,
                                      @NonNull Context context) {
        return actionsRepository.searchActions(transactionTrackerCriterion, context)
                .doOnComplete(() -> actionsEventPublisher
                        .emitSuccessfulActionsSearchEvent(context, transactionTrackerCriterion))
                .doOnError(error ->
                        actionsEventPublisher.emitFailedActionsSearchEvent(context, transactionTrackerCriterion
                                , error));
    }


}
