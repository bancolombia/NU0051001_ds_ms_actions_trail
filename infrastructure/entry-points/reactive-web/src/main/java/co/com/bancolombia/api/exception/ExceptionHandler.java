package co.com.bancolombia.api.exception;

import co.com.bancolombia.api.exception.dto.ErrorResponse;
import co.com.bancolombia.api.exception.dto.ErrorResponse.ErrorDescription;
import co.com.bancolombia.api.logger.LogsConstantsEnum;
import co.com.bancolombia.api.logger.TechMessage;
import co.com.bancolombia.exception.status.HttpStatusExceptionMap;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.logging.technical.LoggerFactory;
import co.com.bancolombia.logging.technical.logger.TechLogger;
import co.com.bancolombia.model.exception.BusinessException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(-2)
public class ExceptionHandler extends AbstractErrorWebExceptionHandler {


    private static final TechLogger techLogger = LoggerFactory.getLog(ExceptionHandler.class.getName());

    public ExceptionHandler(DefaultErrorAttributes errorAttributes,
                            ApplicationContext applicationContext,
                            ServerCodecConfigurer serverCodecConfigurer) {

        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {
        return Mono.just(request)
                .map(this::getError)
                .flatMap(Mono::error)
                .onErrorResume(TechnicalException.class, this::buildErrorResponse)
                .onErrorResume(BusinessException.class, this::buildErrorResponse)
                .onErrorResume(this::buildErrorResponse)
                .cast(ErrorDescription.class)
                .map(errorResponse -> errorResponse.toBuilder().domain(request.path()).build())
                .flatMap(errorResponse -> buildResponse(errorResponse, request))
                .doAfterTerminate(() -> techLogger.error(TechMessage.getErrorTechMessage(getError(request), request)));
    }

    private Mono<ErrorDescription> buildErrorResponse(TechnicalException technicalException) {
        return Mono.just(ErrorDescription.builder()
                .reason(technicalException.getTechnicalErrorMessage().getMessage())
                .code(technicalException.getTechnicalErrorMessage().getCode())
                .message(technicalException.getTechnicalErrorMessage().getMessage())
                .build());
    }

    private Mono<ErrorDescription> buildErrorResponse(BusinessException businessException) {
        return Mono.just(ErrorDescription.builder()
                .reason(businessException.getBusinessErrorMessage().getMessage())
                .code(businessException.getBusinessErrorMessage().getCode())
                .message(businessException.getBusinessErrorMessage().getMessage())
                .build());
    }

    private Mono<ErrorDescription> buildErrorResponse(Throwable throwable) {
        return Mono.just(ErrorDescription.builder()
                .reason(TechnicalErrorMessage.UNEXPECTED_EXCEPTION.getMessage())
                .code(TechnicalErrorMessage.UNEXPECTED_EXCEPTION.getCode())
                .message(TechnicalErrorMessage.UNEXPECTED_EXCEPTION.getMessage())
                .build());
    }

    private Mono<ServerResponse> buildResponse(ErrorDescription errorDto, final ServerRequest request) {
        var errorResponse = ErrorResponse.builder()
                .errors(List.of(errorDto))
                .build();

        var status = HttpStatusExceptionMap.get(errorResponse.getErrors().get(0).getCode());

        return ServerResponse.status(status != null ? status : HttpStatusExceptionMap.getDefaultStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse)
                .doOnNext(response -> request.attributes()
                        .put(LogsConstantsEnum.CACHE_RESPONSE_BODY.getName(), errorResponse));
    }

}