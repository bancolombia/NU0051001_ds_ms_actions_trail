package co.com.bancolombia.api.actionreport;

import co.com.bancolombia.api.commons.request.ContextBuilder;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.api.actionsearch.dto.request.SearchCriteriaRequestDTO;
import co.com.bancolombia.api.commons.request.SearchCriteriaBuilder;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import co.com.bancolombia.usecase.report.ActionsDetailReportUseCase;
import co.com.bancolombia.usecase.report.ActionsReportUseCase;
import co.com.bancolombia.validator.ConstraintValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

@Component
@RequiredArgsConstructor
public class ActionReportHandler {

    private static final String ACTION_ID_PATH = "id";
    private static final String TRACKER_ID_PATH = "trackerId";
    private static final String MEDIA_TYPE_APPLICATION_XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String FORMAT_HEADER = "format";
    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private static final String CONTENT_DISPOSITION_PREFIX = "attachment; filename=";
    private static final String DOT = ".";
    private final ActionsReportUseCase actionsReportUseCase;
    private final ActionsDetailReportUseCase actionsDetailReportUseCase;
    private final SearchCriteriaBuilder criteriaBuilder;
    private final ConstraintValidator constraintValidator;
    private final ContextBuilder contextBuilder;

    @Value("${report.filename.history}")
    private String historyFilename;
    @Value("${report.filename.detailed}")
    private String detailedFilename;

    public Mono<ServerResponse> searchActions(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SearchCriteriaRequestDTO.class)
                .flatMap(constraintValidator::validateData)
                .flatMap(request -> Mono.zip(
                        criteriaBuilder.getSearchCriteria(request.getSearchCriteria()),
                        getAvailableFormat(serverRequest),
                        contextBuilder.getContext(serverRequest)))
                .filter(tuple -> tuple.getT2() == AvailableFormat.XLSX)
                .switchIfEmpty(Mono.defer(() -> Mono
                        .error(new BusinessException(BusinessErrorMessage.INVALID_REPORT_FORMAT))))
                .flatMap(TupleUtils.function(actionsReportUseCase::generateReport))
                .then(ServerResponse.status(HttpStatus.ACCEPTED).build())
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

    public Mono<ServerResponse> getActionHistoryByTrackerId(ServerRequest serverRequest) {
        return Mono.zip(
                Mono.just(new TransactionTrackerCriterion(serverRequest.pathVariable(TRACKER_ID_PATH))),
                getAvailableFormat(serverRequest),
                contextBuilder.getContext(serverRequest))
                .flatMap(TupleUtils.function(
                        (trackerCriterion, availableFormat, context) ->
                                actionsReportUseCase.generateReport(
                                        trackerCriterion,
                                        availableFormat,
                                        context)
                                        .flatMap(report -> ServerResponse.status(HttpStatus.CREATED)
                                                .header(CONTENT_DISPOSITION_HEADER,
                                                        getContentDispositionValue(historyFilename, availableFormat))
                                                .contentType(getMediaType(availableFormat))
                                                .bodyValue(report))

                ));
    }

    public Mono<ServerResponse> generateDetailedReport(ServerRequest serverRequest) {
        String actionId = serverRequest.pathVariable(ACTION_ID_PATH);

        return contextBuilder.getContext(serverRequest)
                .flatMap(context ->
                        actionsDetailReportUseCase.generateReport(new TransactionIdCriterion(actionId),
                                AvailableFormat.PDF, context))
                .flatMap(report -> ServerResponse.status(HttpStatus.CREATED)
                        .header(CONTENT_DISPOSITION_HEADER,
                                getContentDispositionValue(detailedFilename, AvailableFormat.PDF))
                        .contentType(MediaType.APPLICATION_PDF)
                        .bodyValue(report));
    }

    private Mono<AvailableFormat> getAvailableFormat(ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(request -> request.headers().firstHeader(FORMAT_HEADER))
                .onErrorMap(NullPointerException.class, ex ->
                        new TechnicalException(ex, TechnicalErrorMessage.MISSING_FORMAT_HEADER))
                .map(String::toUpperCase)
                .map(AvailableFormat::valueOf)
                .onErrorMap(IllegalArgumentException.class, ex ->
                        new BusinessException(BusinessErrorMessage.INVALID_REPORT_FORMAT));
    }

    private MediaType getMediaType(AvailableFormat availableFormat) {
        MediaType mediaType;
        switch (availableFormat) {
            case PDF:
                mediaType = MediaType.APPLICATION_PDF;
                break;
            case XLSX:
                mediaType = MediaType
                        .valueOf(MEDIA_TYPE_APPLICATION_XLSX);
                break;
            default:
                throw new BusinessException(BusinessErrorMessage.INVALID_REPORT_FORMAT);
        }
        return mediaType;
    }

    private String getContentDispositionValue(String filename, AvailableFormat availableFormat) {
        return CONTENT_DISPOSITION_PREFIX.concat(filename)
                .concat(DOT).concat(availableFormat.getFormat());
    }

}