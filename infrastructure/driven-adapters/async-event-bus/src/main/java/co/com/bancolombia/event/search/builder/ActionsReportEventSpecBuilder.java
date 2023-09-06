package co.com.bancolombia.event.search.builder;

import co.com.bancolombia.d2b.model.events.AbstractEventSpec;
import co.com.bancolombia.event.helper.ErrorBuildHelper;
import co.com.bancolombia.event.properties.EventNameProperties;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDownloadDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDownloadFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionHistoryGenerationDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionHistoryGenerationFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataDetailedReportGenerationDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataDetailedReportGenerationFailureDTO;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ActionsReportEventSpecBuilder {

    private static final String DETAILED_REPORT_GENERATION_RESPONSE = "Detailed Report Generated Successfully";
    private static final String ACTIONS_HISTORY_GENERATION_RESPONSE = "Actions History Report Generated Successfully";
    private static final String REPORT_GENERATION_RESPONSE = "Report Generated Successfully";

    private final EventSpecBuilder eventSpecBuilder;
    private final EventNameProperties.Suffix eventSuffix;
    private final EventNameProperties.EventBusinessAction eventBusinessAction;

    public ActionsReportEventSpecBuilder(EventSpecBuilder eventSpecBuilder, EventNameProperties eventNameProperties) {
        this.eventSpecBuilder = eventSpecBuilder;
        this.eventBusinessAction = eventNameProperties.getEventBusinessAction();
        this.eventSuffix = eventBusinessAction.getSuffix();
    }


    public AbstractEventSpec<BodyDataActionDownloadDTO> buildSuccessSpec(
            Context context, Set<AnySearchCriterion<?>> searchCriteria, String filename, AvailableFormat format) {
        var eventBody = new BodyDataActionDownloadDTO(
                MetaDTOBuilder.buildSuccessMeta(context),
                new BodyDataActionDownloadDTO.Request(format.getFormat(), searchCriteria),
                new BodyDataActionDownloadDTO.Response(REPORT_GENERATION_RESPONSE, filename));
        return eventSpecBuilder.buildEventSpec(context, eventSuffix.getActionDownloadDone(),
                eventBusinessAction.getReportPrefix(), eventBody);
    }

    public AbstractEventSpec<BodyDataActionDownloadFailureDTO> buildFailureSpec(
            Context context, Set<AnySearchCriterion<?>> searchCriteria, AvailableFormat format, Throwable error) {
        var eventBody = new BodyDataActionDownloadFailureDTO(
                MetaDTOBuilder.buildErrorMeta(context, error),
                new BodyDataActionDownloadFailureDTO
                        .Request(format.getFormat(), searchCriteria),
                new BodyDataActionDownloadFailureDTO.Response(ErrorBuildHelper.getError(error, context))
        );
        return eventSpecBuilder.buildEventSpec(context, eventSuffix.getActionDownloadRejected(),
                eventBusinessAction.getReportPrefix(), eventBody);
    }

    public AbstractEventSpec<BodyDataActionHistoryGenerationDTO> buildSuccessSpec(
            Context context, TransactionTrackerCriterion transactionTrackerCriterion, AvailableFormat format) {
        var eventBody = new BodyDataActionHistoryGenerationDTO(
                MetaDTOBuilder.buildSuccessMeta(context),
                new BodyDataActionHistoryGenerationDTO.Request(transactionTrackerCriterion.getSearchValue().getValue(),
                        format.getFormat()),
                new BodyDataActionHistoryGenerationDTO.Response(ACTIONS_HISTORY_GENERATION_RESPONSE));
        return eventSpecBuilder.buildEventSpec(context, eventSuffix.getActionHistoryDownloadDone(),
                eventBusinessAction.getReportPrefix(), eventBody);
    }

    public AbstractEventSpec<BodyDataActionHistoryGenerationFailureDTO> buildFailureSpec(
            Context context, TransactionTrackerCriterion transactionTrackerCriterion, AvailableFormat format,
            Throwable error) {
        var eventBody = new BodyDataActionHistoryGenerationFailureDTO(
                MetaDTOBuilder.buildErrorMeta(context, error),
                new BodyDataActionHistoryGenerationFailureDTO
                        .Request(transactionTrackerCriterion.getSearchValue().getValue(), format.getFormat()),
                new BodyDataActionHistoryGenerationFailureDTO.Response(ErrorBuildHelper.getError(error, context))
        );
        return eventSpecBuilder.buildEventSpec(context, eventSuffix.getActionHistoryDownloadRejected(),
                eventBusinessAction.getReportPrefix(), eventBody);
    }

    public AbstractEventSpec<BodyDataDetailedReportGenerationDTO> buildSuccessSpec(
            Context context, TransactionIdCriterion transactionIdCriterion, AvailableFormat format) {
        var eventBody = new BodyDataDetailedReportGenerationDTO(
                MetaDTOBuilder.buildSuccessMeta(context),
                new BodyDataDetailedReportGenerationDTO.Request(transactionIdCriterion.getSearchValue().getValue(),
                        format.getFormat()),
                new BodyDataDetailedReportGenerationDTO.Response(DETAILED_REPORT_GENERATION_RESPONSE));
        return eventSpecBuilder.buildEventSpec(context, eventSuffix.getActionDetailedReportDone(),
                eventBusinessAction.getReportPrefix(),eventBody);
    }

    public AbstractEventSpec<BodyDataDetailedReportGenerationFailureDTO> buildFailureSpec(
            Context context, TransactionIdCriterion transactionIdCriterion, AvailableFormat format, Throwable error) {
        var eventBody = new BodyDataDetailedReportGenerationFailureDTO(
                MetaDTOBuilder.buildErrorMeta(context, error),
                new BodyDataDetailedReportGenerationFailureDTO
                        .Request(transactionIdCriterion.getSearchValue().getValue(), format.getFormat()),
                new BodyDataDetailedReportGenerationFailureDTO.Response(ErrorBuildHelper.getError(error, context))
        );
        return eventSpecBuilder.buildEventSpec(context, eventSuffix.getActionDetailedReportRejected(),
                eventBusinessAction.getReportPrefix(), eventBody);
    }
}
