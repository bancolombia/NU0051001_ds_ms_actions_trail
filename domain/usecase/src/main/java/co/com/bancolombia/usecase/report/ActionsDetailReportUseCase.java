package co.com.bancolombia.usecase.report;

import co.com.bancolombia.model.actions.ActionsReportAdapter;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.events.ReportEventPublisher;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.usecase.search.ActionsSearchUseCase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ActionsDetailReportUseCase {

    private final ActionsReportAdapter actionsReportAdapter;
    private final ActionsSearchUseCase actionsSearchUseCase;
    private final ReportEventPublisher reportEventPublisher;

    public Mono<byte[]> generateReport(@NonNull TransactionIdCriterion transactionIdCriterion,
                                       @NonNull AvailableFormat format, @NonNull Context context) {
        return actionsSearchUseCase.searchAction(transactionIdCriterion, context)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new BusinessException(BusinessErrorMessage.NON_ACTION_FOUND))))
                .flatMap(actions -> actionsReportAdapter.generateDetailedReport(actions, format, context))
                .doOnSuccess(res -> reportEventPublisher.
                        emitSuccessfulDetailedReportGenerationEvent(context, transactionIdCriterion, format))
                .doOnError(error ->
                        reportEventPublisher.emitFailedDetailedReportGenerationEvent(context, transactionIdCriterion
                                , format, error));
    }
}
