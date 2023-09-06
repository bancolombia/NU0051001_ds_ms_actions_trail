package co.com.bancolombia.event.search.builder;

import co.com.bancolombia.d2b.model.events.AbstractEventSpec;
import co.com.bancolombia.event.properties.EventNameProperties;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryPageDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryPageDTO.Request;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryPageDTO.Response;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryPageFailureDTO;
import co.com.bancolombia.event.search.dto.criteria.SearchCriteriaPaginationDTO;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.search.AnySearchCriterion;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

import static co.com.bancolombia.event.helper.ErrorBuildHelper.getError;

@Component
@EnableConfigurationProperties(EventNameProperties.class)
public class EventSearchPageBuilder {

    private final EventSpecBuilder eventSpecBuilder;
    private final EventNameProperties.Suffix eventSuffix;
    private final EventNameProperties.EventBusinessAction eventBusinessAction;
    private static final String QUERY_GENERATED_SUCCESSFULLY = "Query Generated Successfully";

    public EventSearchPageBuilder(EventSpecBuilder eventSpecBuilder, EventNameProperties eventNameProperties) {
        this.eventSpecBuilder = eventSpecBuilder;
        this.eventBusinessAction = eventNameProperties.getEventBusinessAction();
        this.eventSuffix = eventBusinessAction.getSuffix();
    }

    public  AbstractEventSpec<BodyDataActionQueryPageDTO> buildPageSearchPayload(
            Context context, Set<AnySearchCriterion<?>> searchCriteria, PageRequest pageRequest) {

        var bodyDataEvent = new BodyDataActionQueryPageDTO(
               MetaDTOBuilder.buildSuccessMeta(context),
                new Request(new SearchCriteriaPaginationDTO(searchCriteria, pageRequest)),
                new Response(QUERY_GENERATED_SUCCESSFULLY));

        return eventSpecBuilder.buildEventSpec(context,eventSuffix.getActionSearchDone(),
                eventBusinessAction.getActionPrefix(),bodyDataEvent);
    }

    public AbstractEventSpec<BodyDataActionQueryPageFailureDTO> buildErrorPayload(
            Context context, Set<AnySearchCriterion<?>> searchCriteria
            , PageRequest pageRequest,
             Throwable error) {


        var bodyDataEvent = new BodyDataActionQueryPageFailureDTO(
                MetaDTOBuilder.buildErrorMeta(context, error),
                new BodyDataActionQueryPageFailureDTO
                        .Request(new SearchCriteriaPaginationDTO(searchCriteria, pageRequest)),
                new BodyDataActionQueryPageFailureDTO.Response(getError(error, context)));

        return eventSpecBuilder.buildEventSpec(context, eventSuffix.getActionSearchRejected(),
                eventBusinessAction.getActionPrefix(), bodyDataEvent);
    }
}