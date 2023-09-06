package co.com.bancolombia.api.exception;

import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
class FailingRoutesConfiguration {

    @Bean
    RouterFunction<ServerResponse> routes() {
        return route()
                .GET("/fail-with-business", request ->
                        Mono.error(new BusinessException(BusinessErrorMessage.INVALID_OPERATOR)))
                .GET("/fail-with-technical", request ->
                        Mono.error(new TechnicalException(new Exception("tech error database"),
                                TechnicalErrorMessage.DATABASE_CONNECTION)))
                .GET("/fail-with-unexpected", request ->
                        Mono.error(new Exception("this is unexpected")))
                .build();
    }
}