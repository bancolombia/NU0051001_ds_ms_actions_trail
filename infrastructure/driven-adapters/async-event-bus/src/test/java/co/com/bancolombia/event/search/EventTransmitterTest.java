package co.com.bancolombia.event.search;

import co.com.bancolombia.d2b.messaging.GalateaEventSpec;
import co.com.bancolombia.d2b.model.events.AbstractEventSpec;
import co.com.bancolombia.d2b.model.events.IEventPublisherGateway;
import co.com.bancolombia.event.search.builder.ActionsQueryEventSpecBuilder;
import co.com.bancolombia.event.search.builder.ActionsReportEventSpecBuilder;
import co.com.bancolombia.event.search.builder.EventSearchPageBuilder;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDetailedQueryDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDetailedQueryFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDownloadDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDownloadFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionHistoryGenerationDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionHistoryGenerationFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryPageDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryPageFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataDetailedReportGenerationDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataDetailedReportGenerationFailureDTO;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.criteria.DateCriterion;
import co.com.bancolombia.model.search.criteria.TransactionCodeCriterion;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static co.com.bancolombia.exception.technical.message.TechnicalErrorMessage.ERROR_EMITTING_EVENT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventTransmitterTest {

    private static final String ACTION_REPORT_HISTORY_ROUTE= "/report/{trackerId}";
    private static final String ACTION_REPORT_HISTORY_EXAMPLE= "/report/2312344641";
    private static final String ACTION_REPORT_DETAIL_ROUTE= "/detailed-report/{id}";
    private static final String ACTION_REPORT_DETAIL_EXAMPLE= "/detailed-report/123456789";
    private static final String ACTION_REPORT_HISTORY_BASE= "/report";
    private static final String TEST_STRING="test";
    private static final String ERROR_STRING="error";

    @Mock
    private Context context;
    @Mock
    private IEventPublisherGateway eventPublisherGateway;
    @Mock
    private EventSearchPageBuilder eventSearchPageBuilder;
    @Mock
    private ActionsQueryEventSpecBuilder actionsQueryEventSpecBuilder;
    @Mock
    private ActionsReportEventSpecBuilder actionsReportEventSpecBuilder;
    @Mock
    private EventTransmitter eventTransmitterSpyMock;

    private EventTransmitter eventTransmitter;
    Throwable error = new Throwable(ERROR_STRING);

    @BeforeEach
    public void setUp(){
        eventTransmitter = new EventTransmitter(eventPublisherGateway, eventSearchPageBuilder,
                actionsQueryEventSpecBuilder, actionsReportEventSpecBuilder, ACTION_REPORT_HISTORY_ROUTE,
                ACTION_REPORT_HISTORY_BASE, ACTION_REPORT_DETAIL_ROUTE);
        eventTransmitterSpyMock = spy(eventTransmitter);
    }

    @Test
    void shouldEmitEvent() {
        AbstractEventSpec<String> testEvent = new GalateaEventSpec<>();
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        StepVerifier.create(eventTransmitter.emitEvent(testEvent)).expectSubscription().verifyComplete();

    }

    @Test
    void shouldThrowException() {
        AbstractEventSpec<String> testEvent = new GalateaEventSpec<>();
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.error(new RuntimeException()));
        StepVerifier.create(eventTransmitter.emitEvent(testEvent))
                .expectErrorMatches(e->e instanceof TechnicalException &&
                        e.getMessage().equals(ERROR_EMITTING_EVENT.getMessage()))
                        .verify();

    }

    @Test
    void shouldEmitActionsSearchEvent() {
        Mockito.when(context.getDomain()).thenReturn(ACTION_REPORT_HISTORY_BASE);
        assertEquals(Boolean.TRUE, eventTransmitter.shouldEmitActionsSearchEvent(context));
    }
    @Test
    void shouldNotEmitActionsSearchEvent() {
        Mockito.when(context.getDomain()).thenReturn(ACTION_REPORT_HISTORY_EXAMPLE);
        assertEquals(Boolean.FALSE, eventTransmitter.shouldEmitActionsSearchEvent(context));
    }

    @Test
    void shouldEmitPaginatedActionsSearchEvent() {
        Mockito.when(context.getDomain()).thenReturn(TEST_STRING);
        assertEquals(Boolean.TRUE, eventTransmitter.shouldEmitPaginatedActionsSearchEvent(context));
    }

    @Test
    void shouldNotEmitPaginatedActionsSearchEvent() {
        Mockito.when(context.getDomain()).thenReturn(ACTION_REPORT_HISTORY_BASE);
        assertEquals(Boolean.FALSE, eventTransmitter.shouldEmitPaginatedActionsSearchEvent(context));
    }

    @Test
    void shouldEmitActionSearchEvent() {
        Mockito.when(context.getDomain()).thenReturn(TEST_STRING);
        assertEquals(Boolean.TRUE, eventTransmitter.shouldEmitActionSearchEvent(context));
    }

    @Test
    void shouldNotEmitActionSearchEvent() {
        Mockito.when(context.getDomain()).thenReturn(ACTION_REPORT_DETAIL_EXAMPLE);
        assertEquals(Boolean.FALSE, eventTransmitter.shouldEmitActionSearchEvent(context));
    }

    @Test
    void shouldEmitSuccessfulActionsSearchEvent() {
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion(TEST_STRING);
        AbstractEventSpec<BodyDataActionQueryDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsQueryEventSpecBuilder.buildSuccessSpec(
                context, transactionTrackerCriterion)).thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        doReturn(Boolean.TRUE).when(eventTransmitterSpyMock).shouldEmitActionsSearchEvent(context);
        eventTransmitterSpyMock
                .emitSuccessfulActionsSearchEvent(context, transactionTrackerCriterion);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

    @Test
    void shouldNotEmitSuccessfulActionsSearchEvent() {
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion(TEST_STRING);
        AbstractEventSpec<BodyDataActionQueryDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsQueryEventSpecBuilder.buildSuccessSpec(
                context, transactionTrackerCriterion)).thenReturn(testEvent);
        doReturn(Boolean.FALSE).when(eventTransmitterSpyMock).shouldEmitActionsSearchEvent(context);
        eventTransmitterSpyMock
                .emitSuccessfulActionsSearchEvent(context, transactionTrackerCriterion);
        verify(eventTransmitterSpyMock, times(0)).emitEvent(testEvent);
    }

    @Test
    void shouldEmitFailedActionsSearchEvent() {
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion(TEST_STRING);
        AbstractEventSpec<BodyDataActionQueryFailureDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsQueryEventSpecBuilder.buildFailureSpec(
                context, transactionTrackerCriterion, error)).thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        doReturn(Boolean.TRUE).when(eventTransmitterSpyMock).shouldEmitActionsSearchEvent(context);
        eventTransmitterSpyMock
                .emitFailedActionsSearchEvent(context, transactionTrackerCriterion, error);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

    @Test
    void shouldNotEmitFailedActionsSearchEvent() {
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion(TEST_STRING);
        AbstractEventSpec<BodyDataActionQueryFailureDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsQueryEventSpecBuilder.buildFailureSpec(
                context, transactionTrackerCriterion, error)).thenReturn(testEvent);
        doReturn(Boolean.FALSE).when(eventTransmitterSpyMock).shouldEmitActionsSearchEvent(context);
        eventTransmitterSpyMock
                .emitFailedActionsSearchEvent(context, transactionTrackerCriterion, error);
        verify(eventTransmitterSpyMock, times(0)).emitEvent(testEvent);
    }

    @Test
    void shouldEmitSuccessfulHistoryReportGenerationEvent() {
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion(TEST_STRING);
        AbstractEventSpec<BodyDataActionHistoryGenerationDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsReportEventSpecBuilder.buildSuccessSpec(
                context, transactionTrackerCriterion, AvailableFormat.XLSX)).thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        eventTransmitterSpyMock
                .emitSuccessfulReportGenerationEvent(context, transactionTrackerCriterion, AvailableFormat.XLSX);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

    @Test
    void shouldEmitFailedHistoryReportGenerationEvent() {
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion(TEST_STRING);
        AbstractEventSpec<BodyDataActionHistoryGenerationFailureDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsReportEventSpecBuilder.buildFailureSpec(
                context, transactionTrackerCriterion, AvailableFormat.XLSX, error)).thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        eventTransmitterSpyMock
                .emitFailedReportGenerationEvent(context, transactionTrackerCriterion, AvailableFormat.XLSX, error);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

    @Test
    void shouldEmitSuccessfulActionSearchEvent() {
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion(TEST_STRING);
        AbstractEventSpec<BodyDataActionDetailedQueryDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsQueryEventSpecBuilder.buildSuccessSpec(context, transactionIdCriterion))
                .thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        Mockito.when(context.getDomain()).thenReturn(TEST_STRING);
        eventTransmitterSpyMock.emitSuccessfulActionSearchEvent(context, transactionIdCriterion);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

    @Test
    void shouldNotEmitSuccessfulActionSearchEvent() {
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion(TEST_STRING);
        AbstractEventSpec<BodyDataActionDetailedQueryDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsQueryEventSpecBuilder.buildSuccessSpec(
                context, transactionIdCriterion)).thenReturn(testEvent);
        Mockito.when(context.getDomain()).thenReturn(ACTION_REPORT_DETAIL_EXAMPLE);
        eventTransmitter
                .emitSuccessfulActionSearchEvent(context, transactionIdCriterion);
        verify(eventPublisherGateway, times(0)).emitEvent(testEvent);
    }

    @Test
    void shouldEmitFailedActionSearchEvent() {
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion(TEST_STRING);
        AbstractEventSpec<BodyDataActionDetailedQueryFailureDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsQueryEventSpecBuilder.buildFailureSpec(
                context, transactionIdCriterion, error)).thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        Mockito.when(context.getDomain()).thenReturn(TEST_STRING);
        eventTransmitterSpyMock.emitFailedActionSearchEvent(context, transactionIdCriterion, error);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

    @Test
    void shouldNotEmitFailedActionSearchEvent() {
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion(TEST_STRING);
        AbstractEventSpec<BodyDataActionDetailedQueryFailureDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsQueryEventSpecBuilder.buildFailureSpec(
                context, transactionIdCriterion, error)).thenReturn(testEvent);
        Mockito.when(context.getDomain()).thenReturn(ACTION_REPORT_DETAIL_EXAMPLE);
        eventTransmitter
                .emitFailedActionSearchEvent(context, transactionIdCriterion, error);
        verify(eventPublisherGateway, times(0)).emitEvent(testEvent);
    }

    @Test
    void shouldEmitSuccessfulPaginatedActionsSearchEvent() {
        PageRequest pageRequest = new PageRequest(1, 1);
        SearchValue<Integer> searchValueInteger = new SearchValue<>(1, ComparisonOperator.EQUALS);

        DateCriterion dateCriterion = new DateCriterion(LocalDate.now(), LocalDate.now());
        TransactionCodeCriterion transactionCodeCriterion = new TransactionCodeCriterion("1");
        AnySearchCriterion<Integer> anySearchCriterion =
                new AnySearchCriterion<>(TEST_STRING, searchValueInteger) {};
        Set<AnySearchCriterion<?>> searchCriteria =
                Set.of(dateCriterion, transactionCodeCriterion, anySearchCriterion);
        AbstractEventSpec<BodyDataActionQueryPageDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(eventSearchPageBuilder.buildPageSearchPayload(context, searchCriteria,pageRequest))
                .thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        Mockito.when(context.getDomain()).thenReturn(TEST_STRING);
        eventTransmitterSpyMock.emitSuccessfulPaginatedActionsSearchEvent(context, searchCriteria, pageRequest);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

    @Test
    void shouldNotEmitSuccessfulPaginatedActionsSearchEvent() {
        PageRequest pageRequest = new PageRequest(1, 1);
        SearchValue<Integer> searchValueInteger = new SearchValue<>(1, ComparisonOperator.EQUALS);

        DateCriterion dateCriterion = new DateCriterion(LocalDate.now(), LocalDate.now());
        TransactionCodeCriterion transactionCodeCriterion = new TransactionCodeCriterion("1");
        AnySearchCriterion<Integer> anySearchCriterion =
                new AnySearchCriterion<>(TEST_STRING, searchValueInteger) {};
        Set<AnySearchCriterion<?>> searchCriteria =
                Set.of(dateCriterion, transactionCodeCriterion, anySearchCriterion);
        AbstractEventSpec<BodyDataActionQueryPageDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(eventSearchPageBuilder.buildPageSearchPayload(context, searchCriteria,pageRequest))
                .thenReturn(testEvent);
        Mockito.when(context.getDomain()).thenReturn(ACTION_REPORT_HISTORY_BASE);
        eventTransmitter.emitSuccessfulPaginatedActionsSearchEvent(context, searchCriteria, pageRequest);
        verify(eventPublisherGateway, times(0)).emitEvent(testEvent);
    }

    @Test
    void shouldEmitFailedPaginatedActionsSearchEvent() {
        PageRequest pageRequest = new PageRequest(1, 1);
        SearchValue<Integer> searchValueInteger = new SearchValue<>(1, ComparisonOperator.EQUALS);
        DateCriterion dateCriterion = new DateCriterion(LocalDate.now(), LocalDate.now());
        TransactionCodeCriterion transactionCodeCriterion = new TransactionCodeCriterion("1");
        AnySearchCriterion<Integer> anySearchCriterion =
                new AnySearchCriterion<>(TEST_STRING, searchValueInteger) {};
        Set<AnySearchCriterion<?>> searchCriteria =
                Set.of(dateCriterion, transactionCodeCriterion, anySearchCriterion);
        AbstractEventSpec<BodyDataActionQueryPageFailureDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(eventSearchPageBuilder.buildErrorPayload(context, searchCriteria, pageRequest, error))
                .thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        Mockito.when(context.getDomain()).thenReturn(TEST_STRING);
        eventTransmitterSpyMock.emitFailedPaginatedActionsSearchEvent(context, searchCriteria, pageRequest, error);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

    @Test
    void shouldNotEmitFailedPaginatedActionsSearchEvent() {
        PageRequest pageRequest = new PageRequest(1, 1);
        SearchValue<Integer> searchValueInteger = new SearchValue<>(1, ComparisonOperator.EQUALS);
        DateCriterion dateCriterion = new DateCriterion(LocalDate.now(), LocalDate.now());
        TransactionCodeCriterion transactionCodeCriterion = new TransactionCodeCriterion("1");
        AnySearchCriterion<Integer> anySearchCriterion =
                new AnySearchCriterion<>(TEST_STRING, searchValueInteger) {};
        Set<AnySearchCriterion<?>> searchCriteria =
                Set.of(dateCriterion, transactionCodeCriterion, anySearchCriterion);
        AbstractEventSpec<BodyDataActionQueryPageFailureDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(eventSearchPageBuilder.buildErrorPayload(context, searchCriteria, pageRequest, error))
                .thenReturn(testEvent);
        Mockito.when(context.getDomain()).thenReturn(ACTION_REPORT_HISTORY_BASE);
        eventTransmitter.emitFailedPaginatedActionsSearchEvent(context, searchCriteria, pageRequest, error);
        verify(eventPublisherGateway, times(0)).emitEvent(testEvent);
    }

    @Test
    void shouldEmitSuccessfulQueriedReportGenerationEvent() {
        var filename = "D2B_8f86702e-f66b-4f76-9d2d-6f7b0650734d_CC_00000009043_consultaDeActividades" +
                "_08-03-2023-05-54-33.xlsx";

        Set<AnySearchCriterion<?>> searchCriteria = Set.of();
        AbstractEventSpec<BodyDataActionDownloadDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsReportEventSpecBuilder.buildSuccessSpec(
                context, searchCriteria, filename, AvailableFormat.XLSX)).thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        eventTransmitterSpyMock
                .emitSuccessfulReportGenerationEvent(context, searchCriteria, filename, AvailableFormat.XLSX);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

    @Test
    void shouldEmitFailedHistoryQueriedReportGenerationEvent() {
        Set<AnySearchCriterion<?>> searchCriteria = Set.of();
        AbstractEventSpec<BodyDataActionDownloadFailureDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsReportEventSpecBuilder.buildFailureSpec(
                context, searchCriteria, AvailableFormat.XLSX, error)).thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        eventTransmitterSpyMock
                .emitFailedReportGenerationEvent(context, searchCriteria, AvailableFormat.XLSX, error);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

    @Test
    void shouldEmitSuccessfulDetailedReportGenerationEvent() {
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion(TEST_STRING);

        AbstractEventSpec<BodyDataDetailedReportGenerationDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsReportEventSpecBuilder.buildSuccessSpec(
                context, transactionIdCriterion, AvailableFormat.XLSX)).thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        eventTransmitterSpyMock
                .emitSuccessfulDetailedReportGenerationEvent(context, transactionIdCriterion, AvailableFormat.XLSX);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

    @Test
    void shouldEmitFailedDetailedReportGenerationEvent() {
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion(TEST_STRING);

        AbstractEventSpec<BodyDataDetailedReportGenerationFailureDTO> testEvent = new GalateaEventSpec<>();
        Mockito.when(actionsReportEventSpecBuilder.buildFailureSpec(
                context, transactionIdCriterion, AvailableFormat.XLSX, error)).thenReturn(testEvent);
        Mockito.when(eventPublisherGateway.emitEvent(testEvent)).thenReturn(Mono.empty().then());
        eventTransmitterSpyMock
                .emitFailedDetailedReportGenerationEvent(context, transactionIdCriterion, AvailableFormat.XLSX, error);
        verify(eventTransmitterSpyMock, times(1)).emitEvent(testEvent);
    }

}
