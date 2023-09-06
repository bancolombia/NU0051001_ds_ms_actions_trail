package co.com.bancolombia.api.actionreport;

import action.ActionCreator;
import co.com.bancolombia.api.actionsearch.dto.request.SearchCriteriaRequestDTO;
import co.com.bancolombia.api.commons.request.ContextBuilder;
import co.com.bancolombia.api.commons.request.SearchCriteriaBuilder;
import co.com.bancolombia.api.exception.ExceptionHandler;
import co.com.bancolombia.api.helpers.RequestHelpers;
import co.com.bancolombia.api.properties.RouteProperties;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.actions.ActionsReportAdapter;
import co.com.bancolombia.model.actions.ActionsRepository;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.commons.PageSummary;
import co.com.bancolombia.model.events.ActionsEventPublisher;
import co.com.bancolombia.model.events.ReportEventPublisher;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import co.com.bancolombia.usecase.report.ActionsDetailReportUseCase;
import co.com.bancolombia.usecase.report.ActionsReportUseCase;
import co.com.bancolombia.usecase.search.ActionsSearchUseCase;
import co.com.bancolombia.validator.ConstraintValidator;
import context.ContextCreator;
import headers.HeadersCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.timeout;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;


@WebFluxTest(properties = {
        "routes.path-mapping.action-report.byCriteria=/report",
        "routes.path-mapping.action-report.history=/report/{trackerId}",
        "routes.path-mapping.action-report.detail=/detailed-report/{id}",
        "report.filename.detailed=detalleDeActividad",

})
@ContextConfiguration(classes = {
        ActionsSearchUseCase.class,
        ActionsReportUseCase.class,
        ActionsDetailReportUseCase.class,
        RouteProperties.class,
        ActionReportRouter.class,
        ActionReportHandler.class,
        ExceptionHandler.class,
        SearchCriteriaBuilder.class,
        ContextBuilder.class,
        ConstraintValidator.class
})
class ActionReportRouterTest {

    private static final String BY_CRITERIA = "/report";
    private static final String HISTORY_TEMPLATE = "/report/%s";
    private static final String DETAIL = "/detailed-report/1010";
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private ActionsRepository actionsRepository;
    @MockBean
    private ReportEventPublisher reportEventPublisher;
    @MockBean
    private ActionsReportAdapter actionsReportAdapter;
    @MockBean
    private ActionsEventPublisher actionsEventPublisher;

    public static SearchCriteriaRequestDTO getSearchCriteriaRequest() {
        SearchCriteriaRequestDTO.DateRange dateRange = new SearchCriteriaRequestDTO
                .DateRange(LocalDate.of(2022, 11, 17)
                , LocalDate.of(2022, 12, 17));
        SearchCriteriaRequestDTO.Product product = new SearchCriteriaRequestDTO
                .Product("DEBT", "0334-547-65");
        SearchCriteriaRequestDTO.Identification identification = new SearchCriteriaRequestDTO
                .Identification("MDM_022", "53654");
        SearchCriteriaRequestDTO.User user = new SearchCriteriaRequestDTO.User("Juliano", identification);
        SearchCriteriaRequestDTO.SearchCriteria searchCriteria = new SearchCriteriaRequestDTO.SearchCriteria(dateRange,
                "ACTIVE", "NON-MONETARY", "AUTHENTICATION", "BANK_COLOMBIA", product, user);
        return new SearchCriteriaRequestDTO(searchCriteria);
    }

    public static String getSearchCriteriaRequestDateRangeInvalidFormat() {
        return "{\n" +
                "  \"searchCriteria\": {\n" +
                "    \"dateRange\": {\n" +
                "      \"startDate\": \"2021-13-07\",\n" +
                "      \"endDate\": \"2021-12-07\"\n" +
                "    },\n" +
                "    \"state\": \"EJECUTADO\",\n" +
                "    \"type\": \"NO_MONETARIA\",\n" +
                "    \"name\": \"Autenticación\",\n" +
                "    \"bankEntity\": \"Bancolombia\",\n" +
                "    \"product\": {\n" +
                "      \"type\": \"TarjetaCredito\",\n" +
                "      \"number\": \"6033\"\n" +
                "    },\n" +
                "    \"user\": {\n" +
                "      \"identification\": {\n" +
                "        \"type\": \"C.C.\",\n" +
                "        \"number\": \"10103054658\"\n" +
                "      },\n" +
                "      \"name\": \"Pepito Andres Perez\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    @Test
    void shouldGetSearchActionsSuccessfully() {
        Action action = ActionCreator.createTestAction();
        var pageSummary = new PageSummary<>(List.of(action), new PageRequest(1, 1), 1L);
        var context = ContextCreator.createTestContext();
        byte[] bytes = new byte[1];
        var filename = "D2B_8f86702e-f66b-4f76-9d2d-6f7b0650734d_CC_00000009043_fileName-21-02-2023-10-20-57.pdf";
        when(actionsRepository.searchLastActions(anySet(), any(PageRequest.class), any(Context.class)))
                .thenReturn(Mono.just(pageSummary));
        when(actionsReportAdapter.generateReport(pageSummary.getData(), AvailableFormat.XLSX))
                .thenReturn(Mono.just(bytes));
        when(actionsReportAdapter.uploadReport(bytes, context, AvailableFormat.XLSX)).thenReturn(Mono.just(filename));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(BY_CRITERIA)
                        .build())
                .headers(RequestHelpers.contextConsumer
                        .apply(context))
                .header("format", "xlsx")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(getSearchCriteriaRequest())
                .exchange()
                .expectStatus().isAccepted()
                .expectBody().isEmpty();
        verify(reportEventPublisher, timeout(5000)).emitSuccessfulReportGenerationEvent(eq(context)
                , anySet(), eq(filename), eq(AvailableFormat.XLSX));
    }

    @Test
    void shouldGetErrorWhenGenerateReportFormatDateRangeInvalid() {
        Action action = ActionCreator.createTestAction();
        var pageSummary = new PageSummary<>(List.of(action), new PageRequest(1, 1), 1L);
        var context = ContextCreator.createTestContext();
        byte[] bytes = new byte[1];
        var exception = new TechnicalException(new RuntimeException(), TechnicalErrorMessage.SAVE_REPORT);
        when(actionsRepository.searchLastActions(anySet(), any(PageRequest.class), any(Context.class))).thenReturn(Mono.just(pageSummary));
        when(actionsReportAdapter.generateReport(pageSummary.getData(), AvailableFormat.PDF)).thenReturn(Mono.just(bytes));
        when(actionsReportAdapter.uploadReport(bytes, context, AvailableFormat.PDF)).thenReturn(Mono.error(exception));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(BY_CRITERIA)
                        .build())
                .headers(RequestHelpers.contextConsumer
                        .apply(context))
                .header("format", "pdf")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(getSearchCriteriaRequestDateRangeInvalidFormat()))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isEqualTo(BusinessErrorMessage.DATE_FORMAT_IS_NOT_VALID.getMessage())
                .jsonPath("$.errors[0].code").isEqualTo(BusinessErrorMessage.DATE_FORMAT_IS_NOT_VALID.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(BusinessErrorMessage.DATE_FORMAT_IS_NOT_VALID.getMessage());
    }

    @Test
    void shouldGetErrorWhenGenerateReportFail() {
        Action action = ActionCreator.createTestAction();
        var pageSummary = new PageSummary<>(List.of(action), new PageRequest(1, 1), 1L);
        var context = ContextCreator.createTestContext();
        byte[] bytes = new byte[1];
        var exception = new TechnicalException(new RuntimeException(), TechnicalErrorMessage.GENERATE_REPORT);
        when(actionsRepository.searchLastActions(anySet(), any(PageRequest.class), any(Context.class)))
                .thenReturn(Mono.just(pageSummary));
        when(actionsReportAdapter.generateReport(pageSummary.getData(), AvailableFormat.XLSX))
                .thenReturn(Mono.error(exception));
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(BY_CRITERIA)
                        .build())
                .headers(RequestHelpers.contextConsumer
                        .apply(context))
                .header("format", "xlsx")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(getSearchCriteriaRequest())
                .exchange()
                .expectStatus().isAccepted()
                .expectBody().isEmpty();
        verify(reportEventPublisher, timeout(5000)).emitFailedReportGenerationEvent(eq(context), anySet()
                , eq(AvailableFormat.XLSX), eq(exception));
    }

    @Test
    void shouldGetErrorWhenNoActionsAreFound() {
        List<Action> emptyList = List.of();
        var pageSummary = new PageSummary<>(emptyList, new PageRequest(1, 1), 1L);
        when(actionsRepository.searchLastActions(anySet(), any(PageRequest.class), any(Context.class)))
                .thenReturn(Mono.just(pageSummary));
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(BY_CRITERIA)
                        .build())
                .headers(RequestHelpers.contextConsumer
                        .apply(ContextCreator.createTestContext()))
                .header("format", "xlsx")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(getSearchCriteriaRequest())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty()
                .jsonPath("$.errors[0].code").isEqualTo(BusinessErrorMessage.NON_ACTION_FOUND.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(BusinessErrorMessage.NON_ACTION_FOUND.getMessage());
    }

    @Test
    void shouldGetErrorWhenHeaderFormatIsNull() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(BY_CRITERIA)
                        .build())
                .headers(RequestHelpers.contextConsumer
                        .apply(ContextCreator.createTestContext()))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(getSearchCriteriaRequest())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty()
                .jsonPath("$.errors[0].code").isEqualTo(TechnicalErrorMessage.MISSING_FORMAT_HEADER.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(TechnicalErrorMessage.MISSING_FORMAT_HEADER
                        .getMessage());
    }

    @Test
    void shouldGetErrorWhenHeaderFormatIsNoValid() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(BY_CRITERIA)
                        .build())
                .headers(RequestHelpers.contextConsumer
                        .apply(ContextCreator.createTestContext()))
                .header("format", "txt")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(getSearchCriteriaRequest())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty()
                .jsonPath("$.errors[0].code").isEqualTo(BusinessErrorMessage.INVALID_REPORT_FORMAT.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(BusinessErrorMessage.INVALID_REPORT_FORMAT
                        .getMessage());
    }

    @Test
    void shouldGetErrorInSearchActionsWhenHeaderFormatIsPdf() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(BY_CRITERIA)
                        .build())
                .headers(RequestHelpers.contextConsumer
                        .apply(ContextCreator.createTestContext()))
                .header("format", "pdf")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(getSearchCriteriaRequest())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty()
                .jsonPath("$.errors[0].code").isEqualTo(BusinessErrorMessage.INVALID_REPORT_FORMAT.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(BusinessErrorMessage.INVALID_REPORT_FORMAT
                        .getMessage());
    }

    @Test
    void shouldGetErrorWhenContextHeadersAreIncomplete() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(BY_CRITERIA)
                        .build())
                .headers(RequestHelpers.contextConsumer
                        .apply(ContextCreator.createTestContext()))
                .header("format", "xlsx")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(getSearchCriteriaRequest())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty()
                .jsonPath("$.errors[0].code").isEqualTo(TechnicalErrorMessage.UNEXPECTED_EXCEPTION.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(TechnicalErrorMessage.UNEXPECTED_EXCEPTION
                        .getMessage());
    }

    @Test
    void shouldGetErrorWhenSendingReportFails() {
        Action action = ActionCreator.createTestAction();
        var pageSummary = new PageSummary<>(List.of(action), new PageRequest(1, 1), 1L);
        byte[] bytes = new byte[1];
        var exception = new TechnicalException(new RuntimeException(), TechnicalErrorMessage.SAVE_REPORT);
        when(actionsRepository.searchLastActions(anySet(), any(), any())).thenReturn(Mono.just(pageSummary));
        when(actionsReportAdapter.generateReport(any(), any())).thenReturn(Mono.just(bytes));
        when(actionsReportAdapter.uploadReport(any(), any(), any())).thenReturn(Mono.error(exception));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(BY_CRITERIA)
                        .build())
                .headers(RequestHelpers.contextConsumer
                        .apply(ContextCreator.createTestContext()))
                .header("format", "xlsx")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(getSearchCriteriaRequest())
                .exchange()
                .expectStatus().isAccepted()
                .expectBody().isEmpty();
        verify(reportEventPublisher, timeout(5000)).emitFailedReportGenerationEvent(any(), anySet()
                , eq(AvailableFormat.XLSX), eq(exception));
    }

    @Test
    void shouldGetActionHistoryByTrackerIdPdfSuccessfully() {
        var trackerId = "9610";
        var transactionTrackerCriterion = new TransactionTrackerCriterion(trackerId);
        var requestContext = ContextCreator.createTestContext();
        var actions = List.of(ActionCreator.createTestAction());
        var bytes = new byte[1];
        when(actionsRepository.searchActions(transactionTrackerCriterion, requestContext))
                .thenReturn(Flux.fromIterable(actions));
        when(actionsReportAdapter.generateReportHistory(actions, AvailableFormat.PDF, requestContext)).thenReturn(Mono.just(bytes));

        var response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(String.format(HISTORY_TEMPLATE, trackerId))
                        .build())
                .headers(RequestHelpers.contextConsumer.apply(requestContext))
                .header("format", "pdf")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody().returnResult().getResponseBodyContent();
        Assertions.assertThat(response).isEqualTo(bytes);
    }

    @Test
    void shouldGetActionHistoryByTrackerIdXlsxSuccessfully() {
        var trackerId = "9610";
        var transactionTrackerCriterion = new TransactionTrackerCriterion(trackerId);
        var requestContext = ContextCreator.createTestContext();
        var actions = List.of(ActionCreator.createTestAction());
        var bytes = new byte[1];
        when(actionsRepository.searchActions(transactionTrackerCriterion, requestContext))
                .thenReturn(Flux.fromIterable(actions));
        when(actionsReportAdapter.generateReportHistory(actions, AvailableFormat.XLSX, requestContext)).thenReturn(Mono.just(bytes));

        var response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(String.format(HISTORY_TEMPLATE, trackerId))
                        .build())
                .headers(RequestHelpers.contextConsumer.apply(requestContext))
                .header("format", "xlsx")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody().returnResult().getResponseBodyContent();
        Assertions.assertThat(response).isEqualTo(bytes);
    }

    @Test
    void shouldGetErrorWhenHistoryByTrackerIdHaveHeaderFormatNull() {
        var trackerId = "9610";
        var requestContext = ContextCreator.createTestContext();
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(String.format(HISTORY_TEMPLATE, trackerId))
                        .build())
                .headers(RequestHelpers.contextConsumer.apply(requestContext))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty()
                .jsonPath("$.errors[0].code").isEqualTo(TechnicalErrorMessage.MISSING_FORMAT_HEADER.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(TechnicalErrorMessage
                        .MISSING_FORMAT_HEADER.getMessage());

    }

    @Test
    void shouldGetErrorWhenHistoryByTrackerIdHaveHeaderFormatIsNoValid() {
        var trackerId = "9610";
        var requestContext = ContextCreator.createTestContext();
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(String.format(HISTORY_TEMPLATE, trackerId))
                        .build())
                .headers(RequestHelpers.contextConsumer.apply(requestContext))
                .header("format", "txt")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty()
                .jsonPath("$.errors[0].code").isEqualTo(BusinessErrorMessage.INVALID_REPORT_FORMAT.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(BusinessErrorMessage.INVALID_REPORT_FORMAT
                        .getMessage());
    }

    @Test
    void shouldGetErrorWhenHistoryByTrackerIdGenarateReportFails() {
        var trackerId = "9610";
        var transactionTrackerCriterion = new TransactionTrackerCriterion(trackerId);
        var requestContext = ContextCreator.createTestContext();
        var actions = List.of(ActionCreator.createTestAction());
        when(actionsRepository.searchActions(transactionTrackerCriterion, requestContext))
                .thenReturn(Flux.fromIterable(actions));
        when(actionsReportAdapter.generateReportHistory(actions, AvailableFormat.PDF, requestContext))
                .thenReturn(Mono.error(new TechnicalException(new RuntimeException()
                        , TechnicalErrorMessage.GENERATE_REPORT)));
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(String.format(HISTORY_TEMPLATE, trackerId))
                        .build())
                .headers(RequestHelpers.contextConsumer.apply(requestContext))
                .header("format", "pdf")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty()
                .jsonPath("$.errors[0].code").isEqualTo(TechnicalErrorMessage.GENERATE_REPORT.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(TechnicalErrorMessage.GENERATE_REPORT.getMessage());
    }

    @Test
    void shouldGetErrorWhenHistoryByTrackerIdNoActionsAreFound() {
        var trackerId = "9610";
        var transactionTrackerCriterion = new TransactionTrackerCriterion(trackerId);
        var requestContext = ContextCreator.createTestContext();
        when(actionsRepository.searchActions(transactionTrackerCriterion, requestContext)).thenReturn(Flux.empty());
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(String.format(HISTORY_TEMPLATE, trackerId))
                        .build())
                .headers(RequestHelpers.contextConsumer.apply(requestContext))
                .header("format", "pdf")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty()
                .jsonPath("$.errors[0].code").isEqualTo(BusinessErrorMessage.NON_ACTION_FOUND.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(BusinessErrorMessage.NON_ACTION_FOUND.getMessage());
    }

    @Test
    void shouldGenerateDetailedReportSuccessfully() {
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("test");
        Action action = ActionCreator.createTestAction();
        byte[] bytes = new byte[1];
        var context = ContextCreator.createTestContext();
        when(actionsRepository.searchAction(transactionIdCriterion, context)).thenReturn(Mono.just(action));
        when(actionsReportAdapter.generateDetailedReport(action, AvailableFormat.PDF, context)).thenReturn(Mono.just(bytes));
        var response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(DETAIL)
                        .build())
                .headers(httpHeaders -> httpHeaders.addAll(HeadersCreator.createTestHeaders()))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectHeader().contentDisposition(ContentDisposition.attachment()
                        .filename("detalleDeActividad".concat(".pdf"))
                        .build())
                .expectBody().returnResult().getResponseBodyContent();

        assertThat(response).isEqualTo(bytes);
    }
}
