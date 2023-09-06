package co.com.bancolombia.api.exception;

import co.com.bancolombia.api.exception.dto.ErrorResponse;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

@WebFluxTest(properties = {
        "spring.application.name=appName"
})
@ContextConfiguration(classes = {
        ExceptionHandler.class,
        FailingRoutesConfiguration.class})
class ExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturn400IfBusinessExceptionThrown() {
        var errorMessage = BusinessErrorMessage.INVALID_OPERATOR;
        var url = "/fail-with-business";

        webTestClient.get()
                .uri(url)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Collections.singletonList(
                                ErrorResponse.ErrorDescription.builder()
                                        .reason(errorMessage.getMessage())
                                        .domain(url)
                                        .code(errorMessage.getCode())
                                        .message(errorMessage.getMessage())
                                        .build()
                        ))
                        .build());
    }

    @Test
    void shouldReturn400IfTechnicalExceptionThrown() {
        var errorMessage = TechnicalErrorMessage.DATABASE_CONNECTION;
        var url = "/fail-with-technical";

        webTestClient.get()
                .uri(url)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Collections.singletonList(
                                ErrorResponse.ErrorDescription.builder()
                                        .reason(errorMessage.getMessage())
                                        .domain(url)
                                        .code(errorMessage.getCode())
                                        .message(errorMessage.getMessage())
                                        .build()
                        ))
                        .build());
    }

    @Test
    void shouldReturn500IfUnexpectedExceptionThrown() {
        var errorMessage = TechnicalErrorMessage.UNEXPECTED_EXCEPTION;
        var url = "/fail-with-unexpected";

        webTestClient.get()
                .uri(url)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Collections.singletonList(
                                ErrorResponse.ErrorDescription.builder()
                                        .reason(errorMessage.getMessage())
                                        .domain(url)
                                        .code(errorMessage.getCode())
                                        .message(errorMessage.getMessage())
                                        .build()
                        ))
                        .build());
    }
}
