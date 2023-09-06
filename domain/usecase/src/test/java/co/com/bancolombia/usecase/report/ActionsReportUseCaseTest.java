package co.com.bancolombia.usecase.report;

import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.actions.ActionsReportAdapter;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.commons.PageSummary;
import co.com.bancolombia.model.events.ReportEventPublisher;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.criteria.DateCriterion;
import co.com.bancolombia.model.search.criteria.TransactionNameCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import co.com.bancolombia.usecase.search.ActionsSearchUseCase;
import co.com.bancolombia.usecase.utils.DataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActionsReportUseCaseTest {

    @Mock
    private ActionsReportAdapter actionsReportAdapter;
    @Mock
    private ReportEventPublisher reportEventPublisher;
    @Mock
    private ActionsSearchUseCase actionsSearchUseCase;
    @InjectMocks
    private ActionsReportUseCase actionsReportUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGenerateReportWithSearchCriteriaSuccessfully() {
        int totalPages = 3;
        Context context = DataBuilder.buildContext();
        AvailableFormat format = AvailableFormat.PDF;
        byte[] bytes = new byte[1];
        PageRequest pageRequest = new PageRequest(1, totalPages);
        List<Action> actions = List.of(new Action());

        PageSummary<Action> pageSummary = new PageSummary<>(actions, pageRequest, 3L);
        DateCriterion dateCriterion = new DateCriterion(LocalDate.now(), LocalDate.now());
        Set<AnySearchCriterion<?>> searchCriteria = Set.of(dateCriterion);
        var filename = "D2B_8f86702e-f66b-4f76-9d2d-6f7b0650734d_CC_00000009043_fileName_21-02-2023-10-20-57.pdf";

        when(actionsSearchUseCase.searchLastActions(ArgumentMatchers.eq(searchCriteria), any(PageRequest.class), ArgumentMatchers.eq(context))).thenReturn(Mono.just(pageSummary));
        when(actionsReportAdapter.generateReport(any(), any())).thenReturn(Mono.just(bytes));
        when(actionsReportAdapter.uploadReport(any(), any(), any())).thenReturn(Mono.just(filename));

        StepVerifier.create(actionsReportUseCase.generateReport(searchCriteria, format, context))
                .verifyComplete();

        verify(reportEventPublisher, timeout(5000)).emitSuccessfulReportGenerationEvent(context, searchCriteria, filename, format);
    }

    @Test
    void shouldEmitReportGenerationEventWithSearchCriteriaWhenAnErrorOccurred() {
        Context context = DataBuilder.buildContext();
        AvailableFormat format = AvailableFormat.PDF;

        DateCriterion dateCriterion = new DateCriterion(LocalDate.now(), LocalDate.now());
        Set<AnySearchCriterion<?>> searchCriteria = Set.of(dateCriterion);

        when(actionsSearchUseCase.searchLastActions(any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("ANY ERROR")));

        StepVerifier.create(actionsReportUseCase.generateReport(searchCriteria, format, context))
                .expectError()
                .verify();

        verify(reportEventPublisher).emitFailedReportGenerationEvent(ArgumentMatchers.eq(context), ArgumentMatchers.eq(searchCriteria)
                , ArgumentMatchers.eq(format), any(Throwable.class));
    }

    @Test
    void shouldThrowsNullPointerExceptionWhenParametersAreNullGeneratingQueriedReport() {
        AvailableFormat format = AvailableFormat.PDF;
        Context context = mock(Context.class);
        TransactionNameCriterion transactionNameCriterion = new TransactionNameCriterion("ANY VALUE");
        Set<AnySearchCriterion<?>> searchCriteria = Set.of(transactionNameCriterion);

        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport((Set<AnySearchCriterion<?>>) null, null, null));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport((Set<AnySearchCriterion<?>>) null, format, null));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport((Set<AnySearchCriterion<?>>) null, null, context));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport((Set<AnySearchCriterion<?>>) null, format, context));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport(searchCriteria, null, null));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport(searchCriteria, format, null));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport(searchCriteria, null, context));
    }

    @Test
    void shouldGenerateReportHistorySuccessfully() {
        Context context = DataBuilder.buildContext();
        List<Action> actions = List.of(new Action());
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion("1");
        AvailableFormat format = AvailableFormat.PDF;
        byte[] bytes = new byte[1];

        when(actionsSearchUseCase.searchActions(transactionTrackerCriterion, context)).thenReturn(Flux.fromIterable(actions));
        when(actionsReportAdapter.generateReportHistory(actions, format, context)).thenReturn(Mono.just(bytes));

        StepVerifier.create(actionsReportUseCase.generateReport(transactionTrackerCriterion, format, context))
                .expectNext(bytes)
                .verifyComplete();
        verify(reportEventPublisher).emitSuccessfulReportGenerationEvent(context, transactionTrackerCriterion, format);
    }

    @Test
    void shouldEmitReportGenerationEventWhenAnErrorOccurred() {
        Context context = DataBuilder.buildContext();
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion("1");
        AvailableFormat format = AvailableFormat.PDF;
        when(actionsSearchUseCase.searchActions(transactionTrackerCriterion, context))
                .thenReturn(Flux.error(new RuntimeException("ANY ERROR")));

        StepVerifier.create(actionsReportUseCase.generateReport(transactionTrackerCriterion, format, context))
                .expectError()
                .verify();

        verify(reportEventPublisher).emitFailedReportGenerationEvent(ArgumentMatchers.eq(context), ArgumentMatchers.eq(transactionTrackerCriterion)
                , ArgumentMatchers.eq(format), any(Throwable.class));
    }

    @Test
    void shouldThrowsNullPointerExceptionWhenParametersAreNullGeneratingReport() {
        AvailableFormat format = AvailableFormat.PDF;
        Context context = mock(Context.class);
        TransactionTrackerCriterion searchCriteria = mock(TransactionTrackerCriterion.class);

        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport((TransactionTrackerCriterion) null, null, null));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport((TransactionTrackerCriterion) null, format, null));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport((TransactionTrackerCriterion) null, null, context));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport((TransactionTrackerCriterion) null, format, context));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport(searchCriteria, null, null));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport(searchCriteria, format, null));
        assertThrows(NullPointerException.class, () -> actionsReportUseCase.generateReport(searchCriteria, null, context));
    }

}
