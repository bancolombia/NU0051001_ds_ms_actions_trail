package co.com.bancolombia.event.search.builder;

import co.com.bancolombia.d2b.model.events.AbstractEventSpec;
import co.com.bancolombia.event.helper.ErrorBuildHelper;
import co.com.bancolombia.event.properties.EventNameProperties;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDetailedQueryDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDetailedQueryFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryFailureDTO;
import co.com.bancolombia.event.search.dto.criteria.SearchCriteriaDTO;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import org.springframework.stereotype.Component;
import java.util.Map;

import static co.com.bancolombia.event.helper.ErrorBuildHelper.getError;

@Component
public class ActionsQueryEventSpecBuilder {

    private static final String ACTIONS_HISTORY_QUERY_RESPONSE = "Actions History Query Generated Successfully";
    private static final String ACTION_DETAILED_QUERY_RESPONSE = "Action detailed Query Generated Successfully";

    private final EventSpecBuilder eventSpecBuilder;
    private final EventNameProperties.Suffix eventSuffix;
    private final EventNameProperties.EventBusinessAction eventBusinessAction;

    public ActionsQueryEventSpecBuilder(EventSpecBuilder eventSpecBuilder, EventNameProperties eventNameProperties) {
        this.eventSpecBuilder = eventSpecBuilder;
        this.eventBusinessAction = eventNameProperties.getEventBusinessAction();
        this.eventSuffix = eventBusinessAction.getSuffix();
    }

    public AbstractEventSpec<BodyDataActionQueryDTO> buildSuccessSpec(
            Context context, TransactionTrackerCriterion transactionTrackerCriterion) {

        Map<String, String> searchCriteria = createSearchCriteria(transactionTrackerCriterion);
        var eventBody = new BodyDataActionQueryDTO(MetaDTOBuilder.buildSuccessMeta(context),
                new BodyDataActionQueryDTO.Request(new SearchCriteriaDTO(searchCriteria)),
                new BodyDataActionQueryDTO.Response(ACTIONS_HISTORY_QUERY_RESPONSE));

        return eventSpecBuilder.buildEventSpec(context, eventSuffix.getActionHistoryQueryDone(),
                eventBusinessAction.getActionPrefix(), eventBody);

    }

    public AbstractEventSpec<BodyDataActionDetailedQueryDTO> buildSuccessSpec(
            Context context, TransactionIdCriterion transactionIdCriterion) {
        var eventBody = new BodyDataActionDetailedQueryDTO(
                MetaDTOBuilder.buildSuccessMeta(context),
                new BodyDataActionDetailedQueryDTO.Request(transactionIdCriterion.getSearchValue().getValue()),
                new BodyDataActionDetailedQueryDTO.Response(ACTION_DETAILED_QUERY_RESPONSE));
        return eventSpecBuilder.buildEventSpec(context,eventSuffix.getActionSearchDone(),
                eventBusinessAction.getActionPrefix(), eventBody);
    }

    public AbstractEventSpec<BodyDataActionQueryFailureDTO> buildFailureSpec(
            Context context, TransactionTrackerCriterion transactionTrackerCriterion, Throwable error) {

        Map<String, String> searchCriteria = createSearchCriteria(transactionTrackerCriterion);

        var eventBody = new BodyDataActionQueryFailureDTO(
                MetaDTOBuilder.buildErrorMeta(context, error),
                new BodyDataActionQueryFailureDTO.Request(new SearchCriteriaDTO(searchCriteria)),
                new BodyDataActionQueryFailureDTO.Response(getError(error, context)));

        return eventSpecBuilder.buildEventSpec(context,eventSuffix.getActionHistoryQueryRejected(),
                eventBusinessAction.getActionPrefix(), eventBody);
    }

    private Map<String, String> createSearchCriteria(TransactionTrackerCriterion transactionTrackerCriterion) {
        String searchValue = transactionTrackerCriterion.getSearchValue().getValue();
        String searchCriterion = transactionTrackerCriterion.getCode();
        return Map.of(searchCriterion, searchValue);
    }

    public AbstractEventSpec<BodyDataActionDetailedQueryFailureDTO> buildFailureSpec(
            Context context,
            TransactionIdCriterion transactionIdCriterion, Throwable error) {

        var eventBody = new BodyDataActionDetailedQueryFailureDTO(
                MetaDTOBuilder.buildErrorMeta(context, error),
                new BodyDataActionDetailedQueryFailureDTO.Request(transactionIdCriterion.getSearchValue().getValue()),
                new BodyDataActionDetailedQueryFailureDTO.Response(ErrorBuildHelper.getError(error, context))
        );
        return eventSpecBuilder.buildEventSpec(context, eventSuffix.getActionSearchRejected(),
                eventBusinessAction.getActionPrefix(), eventBody);
    }
}
