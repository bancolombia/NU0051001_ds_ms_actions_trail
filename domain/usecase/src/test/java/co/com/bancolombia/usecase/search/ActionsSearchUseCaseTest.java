package co.com.bancolombia.usecase.search;

import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.actions.ActionsRepository;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.commons.PageSummary;
import co.com.bancolombia.model.events.ActionsEventPublisher;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.criteria.DateCriterion;
import co.com.bancolombia.model.search.criteria.TransactionCodeCriterion;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import co.com.bancolombia.usecase.utils.DataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActionsSearchUseCaseTest {

    @Mock
    private ActionsRepository actionsRepository;
    @Mock
    private ActionsEventPublisher actionsEventPublisher;
    @InjectMocks
    private ActionsSearchUseCase actionsSearchUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNullCreatingUseCase() {
        assertThrows(NullPointerException.class, () -> new ActionsSearchUseCase(null, actionsEventPublisher));
        assertThrows(NullPointerException.class, () -> new ActionsSearchUseCase(actionsRepository, null));
        assertThrows(NullPointerException.class, () -> new ActionsSearchUseCase(null, null));
    }

    @Test
    void shouldSearchPaginatedActionsSuccessfully() {
        Context context = DataBuilder.buildContext();
        List<Action> data = new ArrayList<>();
        PageRequest pageRequest = new PageRequest(1, 1);
        PageSummary<Action> pageSummary = new PageSummary<>(data, pageRequest, 1L);

        SearchValue<Integer> searchValueInteger = new SearchValue<>(1, ComparisonOperator.EQUALS);

        DateCriterion dateCriterion = new DateCriterion(LocalDate.now(), LocalDate.now());
        TransactionCodeCriterion transactionCodeCriterion = new TransactionCodeCriterion("1");
        AnySearchCriterion<Integer> anySearchCriterion = new AnySearchCriterion<>("Test", searchValueInteger) {
        };

        Set<AnySearchCriterion<?>> searchCriteria = Set.of(dateCriterion, transactionCodeCriterion, anySearchCriterion);
        when(actionsRepository.searchLastActions(searchCriteria, pageRequest, context)).thenReturn(Mono.just(pageSummary));

        StepVerifier.create(actionsSearchUseCase.searchLastActions(searchCriteria, pageRequest, context))
                .expectNext(pageSummary)
                .verifyComplete();
        verify(actionsEventPublisher).emitSuccessfulPaginatedActionsSearchEvent(context, searchCriteria, pageRequest);
    }

    @Test
    void shouldPaginatedSearchActionsThrowMissingMandatoryCriteriaExceptionWhenMissingDateCriterion() {
        Context context = DataBuilder.buildContext();
        PageRequest pageRequest = new PageRequest(1, 1);
        TransactionCodeCriterion transactionCodeCriterion = new TransactionCodeCriterion("1");

        Set<AnySearchCriterion<?>> searchCriteria = Set.of(transactionCodeCriterion);

        BusinessException businessException = new BusinessException(BusinessErrorMessage.MISSING_MANDATORY_CRITERIA);

        StepVerifier.create(actionsSearchUseCase.searchLastActions(searchCriteria, pageRequest, context))
                .expectErrorMatches(error -> {
                    assertThat(businessException).usingRecursiveComparison().isEqualTo(error);
                    return true;
                })
                .verify();
        verify(actionsEventPublisher).emitFailedPaginatedActionsSearchEvent(ArgumentMatchers.eq(context), ArgumentMatchers.eq(searchCriteria)
                , ArgumentMatchers.eq(pageRequest), any(BusinessException.class));
    }

    @Test
    void shouldEmitFailedPaginatedActionsSearchEventWhenAnErrorOccurred() {
        Context context = DataBuilder.buildContext();
        PageRequest pageRequest = new PageRequest(1, 1);
        DateCriterion dateCriterion = new DateCriterion(LocalDate.now(), LocalDate.now());
        Set<AnySearchCriterion<?>> searchCriteria = Set.of(dateCriterion);

        when(actionsRepository.searchLastActions(searchCriteria, pageRequest, context)).thenReturn(Mono.error(new RuntimeException("ANY ERROR")));

        StepVerifier.create(actionsSearchUseCase.searchLastActions(searchCriteria, pageRequest, context))
                .expectError()
                .verify();
        verify(actionsEventPublisher).emitFailedPaginatedActionsSearchEvent(ArgumentMatchers.eq(context), ArgumentMatchers.eq(searchCriteria)
                , ArgumentMatchers.eq(pageRequest), any(Throwable.class));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNullSearchingLastActions() {
        Set<AnySearchCriterion<?>> searchCriteria = Set.of();
        PageRequest pageRequest = Mockito.mock(PageRequest.class);
        Context context = Mockito.mock(Context.class);

        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchLastActions(searchCriteria, pageRequest, null));
        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchLastActions(searchCriteria, null, context));
        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchLastActions(searchCriteria, null, null));
        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchLastActions(null, pageRequest, context));
        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchLastActions(null, pageRequest, null));
        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchLastActions(null, null, context));
        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchLastActions(null, null, null));
    }

    @Test
    void shouldSearchActionsSuccessfully() {
        Context context = DataBuilder.buildContext();
        Action action = new Action();
        Action anotherAction = new Action();
        TransactionTrackerCriterion trackerCriterion = new TransactionTrackerCriterion("1");

        when(actionsRepository.searchActions(trackerCriterion, context)).thenReturn(Flux.just(action,anotherAction));

        StepVerifier.create(actionsSearchUseCase.searchActions(trackerCriterion, context))
                .expectNext(action,anotherAction)
                .verifyComplete();
        verify(actionsEventPublisher).emitSuccessfulActionsSearchEvent(context, trackerCriterion);
    }

    @Test
    void shouldEmitFailedActionsSearchEventWhenAnErrorOccurred() {
        Context context = DataBuilder.buildContext();
        TransactionTrackerCriterion trackerCriterion = new TransactionTrackerCriterion("1");

        when(actionsRepository.searchActions(trackerCriterion, context)).thenReturn(Flux.error(new RuntimeException("ANY ERROR")));

        StepVerifier.create(actionsSearchUseCase.searchActions(trackerCriterion, context))
                .expectError()
                .verify();
        verify(actionsEventPublisher).emitFailedActionsSearchEvent(ArgumentMatchers.eq(context), ArgumentMatchers.eq(trackerCriterion)
                , any(Throwable.class));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNullSearchingActions() {
        Context context = DataBuilder.buildContext();
        TransactionTrackerCriterion trackerCriterion = new TransactionTrackerCriterion("1");

        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchActions(trackerCriterion, null));
        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchActions(null, context));
        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchActions(null, null));
    }

    @Test
    void shouldSearchActionSuccessfully() {
        Context context = DataBuilder.buildContext();
        Action action = new Action();
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("1");

        when(actionsRepository.searchAction(transactionIdCriterion, context)).thenReturn(Mono.just(action));

        StepVerifier.create(actionsSearchUseCase.searchAction(transactionIdCriterion, context))
                .expectNext(action)
                .verifyComplete();
        verify(actionsEventPublisher).emitSuccessfulActionSearchEvent(context, transactionIdCriterion);
    }

    @Test
    void shouldEmitFailedActionSearchEventWhenAnErrorOccurred() {
        Context context = DataBuilder.buildContext();
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("1");

        when(actionsRepository.searchAction(transactionIdCriterion, context)).thenReturn(Mono.error(new RuntimeException("ANY ERROR")));

        StepVerifier.create(actionsSearchUseCase.searchAction(transactionIdCriterion, context))
                .expectError()
                .verify();
        verify(actionsEventPublisher).emitFailedActionSearchEvent(ArgumentMatchers.eq(context), ArgumentMatchers.eq(transactionIdCriterion)
                , any(Throwable.class));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNullSearchingAction() {
        Context context = DataBuilder.buildContext();
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("1");

        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchAction(transactionIdCriterion, null));
        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchAction(null, context));
        assertThrows(NullPointerException.class, () -> actionsSearchUseCase.searchAction(null, null));
    }
}
