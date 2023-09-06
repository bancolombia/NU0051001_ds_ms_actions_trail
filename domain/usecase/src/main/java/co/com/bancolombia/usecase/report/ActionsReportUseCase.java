package co.com.bancolombia.usecase.report;

import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.commons.PageSummary;
import co.com.bancolombia.model.events.ReportEventPublisher;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.actions.ActionsReportAdapter;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import co.com.bancolombia.usecase.search.ActionsSearchUseCase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Set;

@RequiredArgsConstructor
public class ActionsReportUseCase {

    private final ActionsReportAdapter actionsReportAdapter;
    private final ActionsSearchUseCase actionsSearchUseCase;
    private final ReportEventPublisher reportEventPublisher;
    private static final Integer PAGE_OFFSET = 1;

    public Mono<Void> generateReport(@NonNull Set<AnySearchCriterion<?>> searchCriteria,
                                     @NonNull AvailableFormat format, @NonNull Context context) {
        var pageRequest = new PageRequest(1);
        return actionsSearchUseCase.searchLastActions(searchCriteria, pageRequest, context)
                .filter(pageSummary -> !pageSummary.getData().isEmpty())
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new BusinessException(BusinessErrorMessage.NON_ACTION_FOUND))))
                .doOnNext(pageSummary ->
                    Flux.range(pageRequest.getNumber() + PAGE_OFFSET, pageSummary.getTotalPages() - PAGE_OFFSET)
                            .flatMap(pageNumber -> actionsSearchUseCase
                                    .searchLastActions(searchCriteria, new PageRequest(pageNumber), context))
                            .mergeWith(Flux.just(pageSummary))
                            .map(PageSummary::getData)
                            .collect(ArrayList<Action>::new, ArrayList::addAll)
                            .flatMap(actions -> actionsReportAdapter.generateReport(actions, format))
                            .flatMap(bytes -> actionsReportAdapter.uploadReport(bytes, context, format))
                            .doOnSuccess(fileName -> reportEventPublisher.
                                    emitSuccessfulReportGenerationEvent(context, searchCriteria, fileName, format))
                            .doOnError(error -> reportEventPublisher.
                                    emitFailedReportGenerationEvent(context, searchCriteria, format, error))
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe()
                )
                .then()
                .doOnError(error -> reportEventPublisher.
                        emitFailedReportGenerationEvent(context, searchCriteria, format, error));
    }

    public Mono<byte[]> generateReport(@NonNull TransactionTrackerCriterion transactionTrackerCriterion,
                                       @NonNull AvailableFormat format, @NonNull Context context) {
        return actionsSearchUseCase.searchActions(transactionTrackerCriterion, context)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new BusinessException(BusinessErrorMessage.NON_ACTION_FOUND))))
                .collectList()
                .flatMap(actions -> actionsReportAdapter.generateReportHistory(actions, format, context))
                .doOnSuccess(res -> reportEventPublisher.
                        emitSuccessfulReportGenerationEvent(context, transactionTrackerCriterion, format))
                .doOnError(error ->
                        reportEventPublisher.emitFailedReportGenerationEvent(context, transactionTrackerCriterion
                                , format, error));
    }

}
