package co.com.bancolombia.event.search;

import co.com.bancolombia.d2b.model.events.AbstractEventSpec;
import co.com.bancolombia.d2b.model.events.IEventPublisherGateway;
import co.com.bancolombia.event.search.builder.ActionsQueryEventSpecBuilder;
import co.com.bancolombia.event.search.builder.ActionsReportEventSpecBuilder;
import co.com.bancolombia.event.search.builder.EventSearchPageBuilder;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.events.ActionsEventPublisher;
import co.com.bancolombia.model.events.ReportEventPublisher;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;


@Service
public class EventTransmitter implements ActionsEventPublisher, ReportEventPublisher {

    private final String actionReportByCriteria;
    private final String actionReportDetail;

    private final IEventPublisherGateway eventPublisherGateway;
    private final EventSearchPageBuilder eventSearchPageBuilder;
    private final ActionsQueryEventSpecBuilder actionsQueryEventSpecBuilder;
    private final ActionsReportEventSpecBuilder actionsReportEventSpecBuilder;
    private final String actionReportHistoryRoute;

    private static final String REGEX_PATH_ID_VARIABLE = "\\{[^{}]*}";

    public EventTransmitter(IEventPublisherGateway eventPublisherGateway,
                            EventSearchPageBuilder eventSearchPageBuilder,
                            ActionsQueryEventSpecBuilder actionsQueryEventSpecBuilder,
                            ActionsReportEventSpecBuilder actionsReportEventSpecBuilder,
                            @Value("${routes.path-mapping.action-report.history}") String actionReportHistoryRoute,
                            @Value("${routes.path-mapping.action-report.byCriteria}") String actionReportByCriteria,
                            @Value("${routes.path-mapping.action-report.detail}") String actionReportDetail) {
        this.eventPublisherGateway = eventPublisherGateway;
        this.eventSearchPageBuilder = eventSearchPageBuilder;
        this.actionsQueryEventSpecBuilder = actionsQueryEventSpecBuilder;
        this.actionsReportEventSpecBuilder = actionsReportEventSpecBuilder;
        this.actionReportByCriteria = actionReportByCriteria;
        this.actionReportHistoryRoute = actionReportHistoryRoute.replaceAll(REGEX_PATH_ID_VARIABLE, "");
        this.actionReportDetail = actionReportDetail.replaceAll(REGEX_PATH_ID_VARIABLE, "");
    }


    @Override
    public void emitSuccessfulPaginatedActionsSearchEvent(Context context,
                                                          Set<AnySearchCriterion<?>> searchCriteria,
                                                          PageRequest pageRequest) {
        var eventData = eventSearchPageBuilder.buildPageSearchPayload(context, searchCriteria, pageRequest);
        if (shouldEmitPaginatedActionsSearchEvent(context)) {
            emitEvent(eventData).subscribe();
        }
    }

    @Override
    public void emitFailedPaginatedActionsSearchEvent(Context context, Set<AnySearchCriterion<?>> searchCriteria,
                                                      PageRequest pageRequest, Throwable error) {
        var eventData = eventSearchPageBuilder.buildErrorPayload(context, searchCriteria, pageRequest, error);
        if (shouldEmitPaginatedActionsSearchEvent(context)) {
            emitEvent(eventData).subscribe();
        }
    }

    public boolean shouldEmitPaginatedActionsSearchEvent(Context context) {
        return !context.getDomain().equals(actionReportByCriteria);
    }

    @Override
    public void emitSuccessfulActionsSearchEvent(Context context,
                                                 TransactionTrackerCriterion transactionTrackerCriterion) {
        var eventData = actionsQueryEventSpecBuilder.buildSuccessSpec(context, transactionTrackerCriterion);
        if(shouldEmitActionsSearchEvent(context)) {
            emitEvent(eventData).subscribe();
        }
    }

    @Override
    public void emitFailedActionsSearchEvent(Context context, TransactionTrackerCriterion transactionTrackerCriterion,
                                             Throwable error) {
        var eventData = actionsQueryEventSpecBuilder.buildFailureSpec(context, transactionTrackerCriterion, error);
        if(shouldEmitActionsSearchEvent(context)) {
            emitEvent(eventData).subscribe();
        }
    }

    public boolean shouldEmitActionsSearchEvent(Context context) {
        return !context.getDomain().contains(actionReportHistoryRoute);
    }

    @Override
    public void emitSuccessfulActionSearchEvent(Context context, TransactionIdCriterion transactionIdCriterion) {
        var eventData = actionsQueryEventSpecBuilder.buildSuccessSpec(context, transactionIdCriterion);
        if (shouldEmitActionSearchEvent(context)) {
            emitEvent(eventData).subscribe();
        }
    }

    @Override
    public void emitFailedActionSearchEvent(Context context, TransactionIdCriterion transactionIdCriterion,
                                            Throwable error) {
        var eventData = actionsQueryEventSpecBuilder.buildFailureSpec(context, transactionIdCriterion, error);
        if (shouldEmitActionSearchEvent(context)) {
            emitEvent(eventData).subscribe();
        }
    }

    public boolean shouldEmitActionSearchEvent(Context context) {
        return !context.getDomain().contains(actionReportDetail);
    }

    Mono<Void> emitEvent(AbstractEventSpec<?> eventData) {
        return eventPublisherGateway.emitEvent(eventData)
                .onErrorMap(Exception.class,
                        error -> new TechnicalException(error, TechnicalErrorMessage.ERROR_EMITTING_EVENT));
    }

    @Override
    public void emitSuccessfulReportGenerationEvent(Context context, Set<AnySearchCriterion<?>> searchCriteria,
                                                    String filename, AvailableFormat format) {
        var eventData = actionsReportEventSpecBuilder.buildSuccessSpec(context, searchCriteria, filename, format);
        emitEvent(eventData).subscribe();
    }

    @Override
    public void emitFailedReportGenerationEvent(Context context, Set<AnySearchCriterion<?>> searchCriteria,
                                                AvailableFormat format, Throwable error) {
        var eventData = actionsReportEventSpecBuilder.buildFailureSpec(context, searchCriteria, format, error);
        emitEvent(eventData).subscribe();
    }

    @Override
    public void emitSuccessfulReportGenerationEvent(Context context,
                                                    TransactionTrackerCriterion transactionTrackerCriterion,
                                                    AvailableFormat format) {
        var eventData = actionsReportEventSpecBuilder.buildSuccessSpec(context, transactionTrackerCriterion, format);
        emitEvent(eventData).subscribe();
    }

    @Override
    public void emitFailedReportGenerationEvent(Context context,
                                                TransactionTrackerCriterion transactionTrackerCriterion,
                                                AvailableFormat format, Throwable error) {
        var eventData = actionsReportEventSpecBuilder
                .buildFailureSpec(context, transactionTrackerCriterion, format, error);
        emitEvent(eventData).subscribe();
    }

    @Override
    public void emitSuccessfulDetailedReportGenerationEvent(Context context,
                                                            TransactionIdCriterion transactionIdCriterion,
                                                            AvailableFormat format) {
        var eventData = actionsReportEventSpecBuilder.buildSuccessSpec(context, transactionIdCriterion, format);
        emitEvent(eventData).subscribe();
    }

    @Override
    public void emitFailedDetailedReportGenerationEvent(Context context, TransactionIdCriterion transactionIdCriterion,
                                                        AvailableFormat format, Throwable error) {

        var eventData = actionsReportEventSpecBuilder.buildFailureSpec(context, transactionIdCriterion, format, error);
        emitEvent(eventData).subscribe();
    }
}