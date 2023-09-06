package co.com.bancolombia.api.actionsearch;

import co.com.bancolombia.api.actionsearch.builder.SearchCriteriaResponseBuilder;
import co.com.bancolombia.api.actionsearch.dto.request.SearchCriteriaRequestDTO;
import co.com.bancolombia.api.actionsearch.dto.response.ActionsResponseDTO;
import co.com.bancolombia.api.commons.request.ContextBuilder;
import co.com.bancolombia.api.commons.request.SearchCriteriaBuilder;
import co.com.bancolombia.validator.ConstraintValidator;
import co.com.bancolombia.api.properties.PageDefaultProperties;
import co.com.bancolombia.api.commons.response.ResponseHelper;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import co.com.bancolombia.usecase.search.ActionsSearchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.codec.DecodingException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

import static co.com.bancolombia.api.commons.request.PageBuilder.getPageRequest;

@Component
@RequiredArgsConstructor
public class ActionSearchHandler {

    private final SearchCriteriaBuilder criteriaBuilder;
    private final PageDefaultProperties defaultPage;
    private final ActionsSearchUseCase actionsSearchUseCase;
    private final ConstraintValidator constraintValidator;
    private final ContextBuilder contextBuilder;

    public Mono<ServerResponse> searchActions(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(SearchCriteriaRequestDTO.class)
                .flatMap(constraintValidator::validateData)
                .flatMap(request -> Mono.zip(
                        criteriaBuilder.getSearchCriteria(request.getSearchCriteria()),
                        getPageRequest(serverRequest, defaultPage),
                        contextBuilder.getContext(serverRequest)))
                .flatMap(TupleUtils.function(actionsSearchUseCase::searchLastActions))
                .flatMap(SearchCriteriaResponseBuilder::paginationResponse)
                .onErrorMap(ServerWebInputException.class,
                        e -> {
                            if (e.getCause() instanceof DecodingException &&
                                    e.getCause().getMessage().contains("java.time.LocalDate")){
                                return new BusinessException(BusinessErrorMessage.DATE_FORMAT_IS_NOT_VALID);
                            }
                            else {
                                return new TechnicalException(e, TechnicalErrorMessage.UNEXPECTED_EXCEPTION);
                            }
                        }
                );
    }

    public Mono<ServerResponse> getActionsByTrackerId(ServerRequest serverRequest) {
        var trackerId = serverRequest.pathVariable("trackerId");
        var transactionTrackerCriterion = new TransactionTrackerCriterion(trackerId);
        return contextBuilder.getContext(serverRequest)
                .flatMap(context -> actionsSearchUseCase.searchActions(transactionTrackerCriterion, context)
                        .collectList().map(ActionsResponseDTO::new)
                        .flatMap(ResponseHelper.successWrapper));
    }

}