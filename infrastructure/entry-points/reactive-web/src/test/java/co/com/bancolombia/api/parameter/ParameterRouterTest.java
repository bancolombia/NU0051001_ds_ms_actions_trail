package co.com.bancolombia.api.parameter;

import co.com.bancolombia.api.commons.request.ContextBuilder;
import co.com.bancolombia.api.exception.ExceptionHandler;
import co.com.bancolombia.api.helpers.RequestHelpers;
import co.com.bancolombia.api.parameter.dto.ProductDataResponseDTO;
import co.com.bancolombia.api.properties.RouteProperties;
import co.com.bancolombia.model.parameters.ParameterRepository;
import co.com.bancolombia.validator.ConstraintValidator;
import context.ContextCreator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@WebFluxTest(properties = {
        "routes.path-mapping.parameter.allParameters=/parameters",
        "channel-management.transactionCode=100",
        "channel-management.parameterName=consultaActividades"
})
@ContextConfiguration(classes = {
        RouteProperties.class,
        ParameterHandler.class,
        ParameterRouter.class,
        ExceptionHandler.class,
        ContextBuilder.class,
        ConstraintValidator.class,
        ParameterProperties.class
})
class ParameterRouterTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private ParameterRepository parameterRepository;
    private static final String PARAMETER_ROUTE = "/parameters";
    private static final String TRANSACTION_CODE = "100";
    private static final String PARAMETER_NAME = "consultaActividades";

    @Test
    void shouldGetParametersSuccessfully() {
        var context = ContextCreator.createTestContext();

        Map<String, Object> responseParameters = new HashMap<>();
        ProductDataResponseDTO productDataResponseDTO = new ProductDataResponseDTO(responseParameters);

        responseParameters.put("entities", List.of("test"));
        when(parameterRepository.getParameters(context.getChannel(), PARAMETER_NAME, TRANSACTION_CODE)).thenReturn(Mono.just(responseParameters));

        webTestClient.get()
                .uri(PARAMETER_ROUTE)
                .headers(RequestHelpers.contextConsumer
                        .apply(context))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(ProductDataResponseDTO.class)
                .isEqualTo(productDataResponseDTO);
    }

    @Test
    void shouldThrowExceptionWhenWhenContextHeadersDoesNotExist() {
        webTestClient.get()
                .uri(PARAMETER_ROUTE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isNotEmpty();
    }
}