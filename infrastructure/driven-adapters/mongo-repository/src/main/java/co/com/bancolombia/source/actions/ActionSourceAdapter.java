package co.com.bancolombia.source.actions;

import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.actions.ActionsRepository;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.commons.PageSummary;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import co.com.bancolombia.source.model.ActionData;
import co.com.bancolombia.source.helper.QueryHelpers;
import co.com.bancolombia.source.actions.mapper.ActionDataMapper;
import co.com.bancolombia.source.actions.mapper.SearchCriteriaCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static co.com.bancolombia.exception.technical.message.TechnicalErrorMessage.ACTIONS_SEARCH;
import static co.com.bancolombia.source.actions.helper.QueryActionHelper.getGenericFilter;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class ActionSourceAdapter implements ActionsRepository {

    private final ReactiveMongoTemplate mongoTemplate;
    private final SearchCriteriaCodeMapper searchCriteriaCodeMapper;
    private final ActionDataMapper actionDataMapper;

    private static final long ZERO = 0;
    private static final int PAGE_INDEX = 1;
    private static final String DATE_CRITERION_CODE = "date";

    @Override
    public Mono<PageSummary<Action>> searchLastActions(Set<AnySearchCriterion<?>> searchCriteria,
                                                       PageRequest pageRequest, Context context) {

        var pageable = org.springframework.data.domain.PageRequest
                .of(pageRequest.getNumber() - PAGE_INDEX, pageRequest.getSize());

        var query = new Query();
        query = QueryHelpers.contextFilter.apply(query, context);
        query = getGenericFilter(searchCriteriaCodeMapper, query, searchCriteria);
        query.with(Sort.by(Sort.Direction.DESC, searchCriteriaCodeMapper.getField(DATE_CRITERION_CODE)));
        var pageQuery = query;

        return mongoTemplate.count(query, ActionData.class)
                .filter(total -> total > ZERO)
                .flatMap(total ->
                     mongoTemplate.find(pageQuery.with(pageable), ActionData.class)
                             .collect(ArrayList::new, this::getListActionsModel)
                             .map(actions -> new PageSummary<>(actions, pageRequest, total))
                )
                .switchIfEmpty(Mono.defer(() ->
                        Mono.just(new PageSummary<>(Collections.emptyList(), pageRequest, ZERO))))
                .onErrorMap(e -> new TechnicalException(e, ACTIONS_SEARCH));
    }

    private void getListActionsModel(List<Action> actions, ActionData actionData) {
        actions.add(actionDataMapper.toEntity(actionData));
    }

    @Override
    public Flux<Action> searchActions(TransactionTrackerCriterion transactionTrackerCriterion, Context context) {

        String transactionTrackerValue = transactionTrackerCriterion.getSearchValue().getValue();
        String transactionTrackerField = searchCriteriaCodeMapper.getField(transactionTrackerCriterion.getCode());
        var query = Query.query(where(transactionTrackerField).is(transactionTrackerValue));
        var finalQuery = QueryHelpers.contextFilter.apply(query, context);
        return mongoTemplate.find(finalQuery, ActionData.class)
                .map(actionDataMapper::toEntity)
                .onErrorMap(e -> new TechnicalException(e, ACTIONS_SEARCH));

    }

    @Override
    public Mono<Action> searchAction(TransactionIdCriterion transactionIdCriterion, Context context) {
        String transactionIdValue = transactionIdCriterion.getSearchValue().getValue();
        String transactionIdField = searchCriteriaCodeMapper.getField(transactionIdCriterion.getCode());
        var query = new Query();
        query = QueryHelpers.contextFilter.apply(query, context);
        query.addCriteria(where(transactionIdField).is(transactionIdValue));
        return mongoTemplate.findOne(query, ActionData.class)
                .map(actionDataMapper::toEntity)
                .onErrorMap(e -> new TechnicalException(e, ACTIONS_SEARCH));
    }
}