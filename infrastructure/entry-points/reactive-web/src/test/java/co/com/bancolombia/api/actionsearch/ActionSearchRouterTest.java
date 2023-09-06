package co.com.bancolombia.api.actionsearch;

import co.com.bancolombia.api.actionsearch.dto.request.SearchCriteriaRequestDTO;
import co.com.bancolombia.api.actionsearch.dto.request.SearchCriteriaRequestDTO.DateRange;
import co.com.bancolombia.api.actionsearch.dto.request.SearchCriteriaRequestDTO.Identification;
import co.com.bancolombia.api.actionsearch.dto.request.SearchCriteriaRequestDTO.Product;
import co.com.bancolombia.api.actionsearch.dto.request.SearchCriteriaRequestDTO.SearchCriteria;
import co.com.bancolombia.api.actionsearch.dto.request.SearchCriteriaRequestDTO.User;
import co.com.bancolombia.api.commons.request.ContextBuilder;
import co.com.bancolombia.api.commons.request.SearchCriteriaBuilder;
import co.com.bancolombia.validator.ConstraintValidator;
import co.com.bancolombia.api.exception.ExceptionHandler;
import co.com.bancolombia.api.helpers.RequestHelpers;
import co.com.bancolombia.api.properties.PageDefaultProperties;
import co.com.bancolombia.api.properties.RouteProperties;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.actions.ActionsRepository;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.commons.PageSummary;
import co.com.bancolombia.model.events.ActionsEventPublisher;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import co.com.bancolombia.usecase.search.ActionsSearchUseCase;
import context.ContextCreator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static co.com.bancolombia.exception.technical.message.TechnicalErrorMessage.ACTIONS_SEARCH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(properties = {
        "routes.path-mapping.action-search.byCriteria=/actions-search",
        "routes.path-mapping.action-search.byTrackerId=/actions-search/{trackerId}",
        "settings.pagination.default-page-size=10",
        "settings.pagination.default-page-number=1",
        "persistence.searchCriteriaCodesMapping.transactionTracker=transactionTracker"
})
@ContextConfiguration(classes = {
        RouteProperties.class,
        SearchCriteriaBuilder.class,
        PageDefaultProperties.class,
        ActionSearchHandler.class,
        ActionSearchRouter.class,
        ActionsSearchUseCase.class,
        ExceptionHandler.class,
        ContextBuilder.class,
        ConstraintValidator.class
})
class ActionSearchRouterTest {

    private static final String MESSAGE_ID = "message-id";
    private static final String IDENTIFICATION_NUMBER = "identification-number";
    private static final String IDENTIFICATION_TYPE = "identification-type";
    private static final String MESSAGE_ID_VALUE = "d3f21df-ds3d1-g13524";
    private static final String IDENTIFICATION_ID_VALUE = "12546513";
    private static final String IDENTIFICATION_TYPE_VALUE = "MDM_1011";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ActionsRepository actionsRepository;

    @MockBean
    ActionsEventPublisher actionsEventPublisher;

    private static final String CRITERIA_ROUTE = "/actions-search";
    private static final String CODE_ROUTE_TEMPLATE = "/actions-search/%s";

    @Test
    void shouldGetByCriteriaSuccessful() {

        when(actionsRepository.searchLastActions(any(), any(), any())).thenReturn(Mono.just(getPageSummary(2, 1)));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(CRITERIA_ROUTE)
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "1")
                        .build())
                .header(MESSAGE_ID, MESSAGE_ID_VALUE)
                .header(IDENTIFICATION_NUMBER, IDENTIFICATION_ID_VALUE)
                .header(IDENTIFICATION_TYPE, IDENTIFICATION_TYPE_VALUE)
                .header("session-tracker", "4532ghj-4df4gh5sd")
                .header("channel", "dbb")
                .header("request-timestamp", "2021-12-10")
                .header("app-version", "1.1.0")
                .header("user-agent", "myUserAgent")
                .header("ip", "1.1.1.1")
                .header("device-id", "25ddf-sdf5g4")
                .header("platform-type", "mobile")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(getSearchCriteriaRequest())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody()
                .jsonPath("$.meta").isNotEmpty()
                .jsonPath("$.meta.totalPage").isEqualTo(100)
                .jsonPath("$.data").isNotEmpty()
                .jsonPath("$.data[0].voucherNumber").isEqualTo("string")
                .jsonPath("$.data[0].paymentType").isEqualTo("string")
                .jsonPath("$.data[1].voucherNumber").isEqualTo("string")
                .jsonPath("$.data[1].paymentType").isEqualTo("string")
                .jsonPath("$.links").isNotEmpty()
                .jsonPath("$.links.self").isEqualTo("?pageNumber=2&pageSize=1")
                .jsonPath("$.links.first").isEqualTo("?pageNumber=1&pageSize=1")
                .jsonPath("$.links.prev").isEqualTo("?pageNumber=1&pageSize=1")
                .jsonPath("$.links.next").isEqualTo("?pageNumber=3&pageSize=1")
                .jsonPath("$.links.last").isEqualTo("?pageNumber=100&pageSize=1");
    }

    @Test
    void shouldGetByCriteriaWhenPaginationQueryParamsAreNotSent() {

        when(actionsRepository.searchLastActions(any(), any(), any())).thenReturn(Mono.just(getPageSummary(1, 10)));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(CRITERIA_ROUTE).build())
                .header(MESSAGE_ID, MESSAGE_ID_VALUE)
                .header(IDENTIFICATION_NUMBER, IDENTIFICATION_ID_VALUE)
                .header(IDENTIFICATION_TYPE, IDENTIFICATION_TYPE_VALUE)
                .header("session-tracker", "4532ghj-4df4gh5sd")
                .header("channel", "dbb")
                .header("request-timestamp", "2021-12-10")
                .header("app-version", "1.1.0")
                .header("user-agent", "myUserAgent")
                .header("ip", "1.1.1.1")
                .header("device-id", "25ddf-sdf5g4")
                .header("platform-type", "mobile")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(getSearchCriteriaRequest())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody()
                .jsonPath("$.meta").isNotEmpty()
                .jsonPath("$.meta.totalPage").isEqualTo(10)
                .jsonPath("$.data").isNotEmpty()
                .jsonPath("$.data[0].voucherNumber").isEqualTo("string")
                .jsonPath("$.data[0].paymentType").isEqualTo("string")
                .jsonPath("$.data[1].voucherNumber").isEqualTo("string")
                .jsonPath("$.data[1].paymentType").isEqualTo("string")
                .jsonPath("$.links").isNotEmpty()
                .jsonPath("$.links.self").isEqualTo("?pageNumber=1&pageSize=10")
                .jsonPath("$.links.first").isEqualTo("?pageNumber=1&pageSize=10")
                .jsonPath("$.links.prev").isEqualTo("")
                .jsonPath("$.links.next").isEqualTo("?pageNumber=2&pageSize=10")
                .jsonPath("$.links.last").isEqualTo("?pageNumber=10&pageSize=10");
    }

    @Test
    void shouldGetByCriteriaWhenNotSendHeaderChannel() {

        when(actionsRepository.searchLastActions(any(), any(), any())).thenReturn(Mono.just(getPageSummary(1, 10)));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(CRITERIA_ROUTE).build())
                .header(MESSAGE_ID, MESSAGE_ID_VALUE)
                .header(IDENTIFICATION_NUMBER, IDENTIFICATION_ID_VALUE)
                .header(IDENTIFICATION_TYPE, IDENTIFICATION_TYPE_VALUE)
                .header("session-tracker", "4532ghj-4df4gh5sd")
                .header("request-timestamp", "2021-12-10")
                .header("app-version", "1.1.0")
                .header("user-agent", "myUserAgent")
                .header("ip", "1.1.1.1")
                .header("device-id", "25ddf-sdf5g4")
                .header("platform-type", "mobile")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(getSearchCriteriaRequest())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isEqualTo(BusinessErrorMessage.CHANNEL_IS_NULL.getMessage())
                .jsonPath("$.errors[0].code").isEqualTo(BusinessErrorMessage.CHANNEL_IS_NULL.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(BusinessErrorMessage.CHANNEL_IS_NULL.getMessage())
                .jsonPath("$.errors[0].domain").isEqualTo(CRITERIA_ROUTE);
    }

    public static PageSummary<Action> getPageSummary(int number, int size) {
        PageRequest pageRequest = new PageRequest(number, size);
        Action action = new Action();
        action.put("paymentType", "string");
        action.put("voucherNumber", "string");

        return new PageSummary<>(List.of(action, action), pageRequest, 100L);
    }

    public static SearchCriteriaRequestDTO getSearchCriteriaRequest() {

        DateRange dateRange = new DateRange(LocalDate.of(2022, 11, 17), LocalDate.of(2022, 12, 17));
        Product product = new Product("DEBT", "0334-547-65");
        Identification identification = new Identification("MDM_022", "53654");
        User user = new User("Juliano", identification);

        SearchCriteria searchCriteria = new SearchCriteria(dateRange,
                "ACTIVE", "NON-MONETARY", "AUTHENTICATION", "BANK_COLOMBIA", product, user);

        return new SearchCriteriaRequestDTO(searchCriteria);
    }

    @Test
    void shouldGetActionsHistoryByTrackerId() {
        String trackerId = "9610";
        var transactionTrackerCriterion = new TransactionTrackerCriterion(trackerId);
        Context requestContext = ContextCreator.createTestContext();
        Action requestAction = new Action();
        requestAction.put("transactionId", "8f86702e-f66b-4f76-9d2d-6f7b0650734d");
        requestAction.put("sessionId", "448589");
        requestAction.put("transactionTracker", trackerId);

        Action approvalAction = new Action();
        requestAction.put("transactionId", "ee84ed49-c6ab-4607-b681-42ec911320e2");
        requestAction.put("sessionId", "448600");
        requestAction.put("transactionTracker", trackerId);

        Mockito.when(actionsRepository.searchActions(transactionTrackerCriterion, requestContext))
                .thenReturn(Flux.just(requestAction, approvalAction));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(String.format(CODE_ROUTE_TEMPLATE, trackerId))
                        .build())
                .headers(RequestHelpers.contextConsumer.apply(requestContext))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldGetErrorResponseWhenContextHeadersAreIncomplete(){
        String trackerId = "9610";
        Context requestContext = ContextCreator.createTestContext();
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(String.format(CODE_ROUTE_TEMPLATE, trackerId))
                        .build())
                .headers(RequestHelpers.contextConsumer.apply(requestContext))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty()
                .jsonPath("$.errors[0].code").isEqualTo("ATT0001")
                .jsonPath("$.errors[0].message").isEqualTo("Unexpected error");
    }

    @Test
    void shouldGetErrorResponseWhenRepositoryFails(){
        String relatedCode = "9610";
        var transactionTrackerCriterion = new TransactionTrackerCriterion(relatedCode);
        Context requestContext = ContextCreator.createTestContext();

        Mockito.when(actionsRepository.searchActions(transactionTrackerCriterion, requestContext))
                .thenReturn(Flux.error(()->new TechnicalException(new RuntimeException(), ACTIONS_SEARCH)));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(String.format(CODE_ROUTE_TEMPLATE, relatedCode))
                        .build())
                .headers(RequestHelpers.contextConsumer.apply(requestContext))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty()
                .jsonPath("$.errors[0].code").isEqualTo("ATT0005")
                .jsonPath("$.errors[0].message").isEqualTo("Error searching actions");
    }


}