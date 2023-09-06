package co.com.bancolombia.model.events;

import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;

import java.util.Set;

/**
 * Communicates the domain with the event publisher in charge of emitting the events that have occurred
 * for the reports generation to the subscribers.
 */
public interface ReportEventPublisher {

    void emitSuccessfulReportGenerationEvent(Context context, Set<AnySearchCriterion<?>> searchCriteria,
                                             String filename, AvailableFormat format);

    void emitFailedReportGenerationEvent(Context context, Set<AnySearchCriterion<?>> searchCriteria,
                                         AvailableFormat format, Throwable error);

    void emitSuccessfulReportGenerationEvent(Context context, TransactionTrackerCriterion transactionTrackerCriterion,
                                             AvailableFormat format);

    void emitFailedReportGenerationEvent(Context context, TransactionTrackerCriterion transactionTrackerCriterion,
                                         AvailableFormat format, Throwable error);

    void emitSuccessfulDetailedReportGenerationEvent(Context context, TransactionIdCriterion transactionIdCriterion,
                                                     AvailableFormat format);

    void emitFailedDetailedReportGenerationEvent(Context context, TransactionIdCriterion transactionIdCriterion,
                                                 AvailableFormat format, Throwable error);
}
