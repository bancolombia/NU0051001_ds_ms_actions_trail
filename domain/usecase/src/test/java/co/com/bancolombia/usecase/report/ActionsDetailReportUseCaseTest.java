package co.com.bancolombia.usecase.report;

import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.actions.ActionsReportAdapter;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.events.ReportEventPublisher;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.usecase.search.ActionsSearchUseCase;
import co.com.bancolombia.usecase.utils.DataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActionsDetailReportUseCaseTest {

    @Mock
    private ActionsReportAdapter actionsReportAdapter;
    @Mock
    private ReportEventPublisher reportEventPublisher;
    @Mock
    private ActionsSearchUseCase actionsSearchUseCase;
    @InjectMocks
    private ActionsDetailReportUseCase actionsDetailReportUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGenerateDetailedReportSuccessfully() {
        Context context = DataBuilder.buildContext();
        Action action = new Action();
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("1");
        AvailableFormat format = AvailableFormat.PDF;
        byte[] bytes = new byte[1];

        when(actionsSearchUseCase.searchAction(transactionIdCriterion, context)).thenReturn(Mono.just(action));
        when(actionsReportAdapter.generateDetailedReport(action, format, context)).thenReturn(Mono.just(bytes));

        StepVerifier.create(actionsDetailReportUseCase.generateReport(transactionIdCriterion, format, context))
                .expectNext(bytes)
                .verifyComplete();
        verify(reportEventPublisher).emitSuccessfulDetailedReportGenerationEvent(context, transactionIdCriterion, format);
    }

    @Test
    void shouldThrowBusinessExceptionWhenNoActionFound() {
        Context context = DataBuilder.buildContext();
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("1");
        AvailableFormat format = AvailableFormat.PDF;
        when(actionsSearchUseCase.searchAction(transactionIdCriterion, context))
                .thenReturn(Mono.empty());

        StepVerifier.create(actionsDetailReportUseCase.generateReport(transactionIdCriterion, format, context))
                .expectErrorMatches(ex -> ((BusinessException) ex).getBusinessErrorMessage().equals(BusinessErrorMessage.NON_ACTION_FOUND))
                .verify();

        verify(reportEventPublisher).emitFailedDetailedReportGenerationEvent(ArgumentMatchers.eq(context), ArgumentMatchers.eq(transactionIdCriterion)
                , ArgumentMatchers.eq(format), any(Throwable.class));
    }

    @Test
    void shouldEmitFailedDetailedReportGenerationEventWhenAnErrorOccurred() {
        Context context = DataBuilder.buildContext();
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("1");
        AvailableFormat format = AvailableFormat.PDF;
        when(actionsSearchUseCase.searchAction(transactionIdCriterion, context))
                .thenReturn(Mono.error(new RuntimeException("ANY ERROR")));

        StepVerifier.create(actionsDetailReportUseCase.generateReport(transactionIdCriterion, format, context))
                .expectError()
                .verify();

        verify(reportEventPublisher).emitFailedDetailedReportGenerationEvent(ArgumentMatchers.eq(context), ArgumentMatchers.eq(transactionIdCriterion)
                , ArgumentMatchers.eq(format), any(Throwable.class));
    }
}
