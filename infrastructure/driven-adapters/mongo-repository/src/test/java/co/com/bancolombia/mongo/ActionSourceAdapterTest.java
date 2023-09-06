package co.com.bancolombia.mongo;

import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import co.com.bancolombia.source.actions.ActionSourceAdapter;
import co.com.bancolombia.source.actions.mapper.ActionDataMapper;
import co.com.bancolombia.source.actions.mapper.SearchCriteriaCodeMapper;
import co.com.bancolombia.source.config.PersistenceProperties;
import co.com.bancolombia.source.helper.QueryHelpers;
import co.com.bancolombia.source.model.ActionData;
import context.ContextCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;


class ActionSourceAdapterTest {

    ActionSourceAdapter actionSourceAdapter;
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        reactiveMongoTemplate = Mockito.mock(ReactiveMongoTemplate.class);
        Map<String, String> searchCriteriaCodesMapping = new HashMap<>();
        searchCriteriaCodesMapping.put("transactionId", "transactionId");
        searchCriteriaCodesMapping.put("transactionTracker", "transactionTracker");
        PersistenceProperties persistenceProperties = new PersistenceProperties(searchCriteriaCodesMapping);
        ActionDataMapper actionDataMapper = new ActionDataMapper();
        actionSourceAdapter = new ActionSourceAdapter(reactiveMongoTemplate, new SearchCriteriaCodeMapper(persistenceProperties), actionDataMapper);
    }

    @Test
    void shouldSearchActionSuccessfully() {
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("test");
        Context context = ContextCreator.createTestContext();
        var query = new Query();
        query = QueryHelpers.contextFilter.apply(query, context);
        query.addCriteria(where("transactionId").is(transactionIdCriterion.getSearchValue().getValue()));

        when(reactiveMongoTemplate.findOne(query, ActionData.class)).thenReturn(Mono.just(new ActionData()));
        StepVerifier.create(actionSourceAdapter.searchAction(transactionIdCriterion, context))
                .expectNextMatches(action -> action.getClass().equals(Action.class))
                .verifyComplete();
    }

    @Test
    void shouldThrowActionSearchExceptionWhenAnyError() {
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("test");
        Context context = ContextCreator.createTestContext();
        Exception exception = new Exception("error");
        var query = new Query();
        query = QueryHelpers.contextFilter.apply(query, context);
        query.addCriteria(where("transactionId").is(transactionIdCriterion.getSearchValue().getValue()));

        when(reactiveMongoTemplate.findOne(query, ActionData.class)).thenReturn(Mono.error(exception));
        StepVerifier.create(actionSourceAdapter.searchAction(transactionIdCriterion, context))
                .expectErrorMatches(ex -> {
                    TechnicalException technicalException = (TechnicalException) ex;
                    assertEquals(exception, technicalException.getCause());
                    assertEquals(TechnicalErrorMessage.ACTIONS_SEARCH, technicalException.getTechnicalErrorMessage());
                    return true;
                })
                .verify();
    }

    @Test
    void shouldSearchActionsSuccessfully() {
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion("test");
        Context context = ContextCreator.createTestContext();
        var query = new Query();
        query = QueryHelpers.contextFilter.apply(query, context);
        query.addCriteria(where("transactionTracker").is(transactionTrackerCriterion.getSearchValue().getValue()));

        when(reactiveMongoTemplate.find(query, ActionData.class)).thenReturn(Flux.just(new ActionData()));
        StepVerifier.create(actionSourceAdapter.searchActions(transactionTrackerCriterion, context))
                .expectNextMatches(action -> action.getClass().equals(Action.class))
                .verifyComplete();
    }

    @Test
    void shouldThrowSearchActionsExceptionWhenAnyError() {
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion("test");
        Context context = ContextCreator.createTestContext();
        Exception exception = new Exception("error");
        var query = new Query();
        query = QueryHelpers.contextFilter.apply(query, context);
        query.addCriteria(where("transactionTracker").is(transactionTrackerCriterion.getSearchValue().getValue()));

        when(reactiveMongoTemplate.find(query, ActionData.class)).thenReturn(Flux.error(exception));
        StepVerifier.create(actionSourceAdapter.searchActions(transactionTrackerCriterion, context))
                .expectErrorMatches(ex -> {
                    TechnicalException technicalException = (TechnicalException) ex;
                    assertEquals(exception, technicalException.getCause());
                    assertEquals(TechnicalErrorMessage.ACTIONS_SEARCH, technicalException.getTechnicalErrorMessage());
                    return true;
                })
                .verify();
    }

}