package co.com.bancolombia.api.products;

import co.com.bancolombia.api.commons.request.ContextBuilder;
import co.com.bancolombia.validator.ConstraintValidator;
import co.com.bancolombia.api.exception.ExceptionHandler;
import co.com.bancolombia.api.properties.RouteProperties;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.parameters.ProductRepository;
import co.com.bancolombia.model.parameters.ProductType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;

@WebFluxTest(properties = {
        "routes.path-mapping.parameter.product=/products"
})
@ContextConfiguration(classes = {
        RouteProperties.class,
        ProductRepository.class,
        ProductsHandler.class,
        ProductsRouter.class,
        ExceptionHandler.class,
        ContextBuilder.class,
        ConstraintValidator.class
})
class ProductsRouterTest {
    private static final String IDENTIFICATION_NUMBER = "identification-number";
    private static final String IDENTIFICATION_TYPE = "identification-type";
    private static final String IDENTIFICATION_ID_VALUE = "00000009043";
    private static final String IDENTIFICATION_TYPE_VALUE = "CC";
    private static final String PRODUCT_ROUTE = "/products";

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private ProductRepository productRepository;

    @Test
    void shouldGetProductsSuccessful() {
        List<String> productNumbers = new ArrayList<>();
        productNumbers.add("123");
        when(productRepository.getCustomerProducts(any()))
                .thenReturn(Flux.just(new ProductType("CUENTA_AHORRO",productNumbers)));
        var a=webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(PRODUCT_ROUTE).build())
                .header("message-id", "d3f21df-ds3d1-g13524")
                .header(IDENTIFICATION_NUMBER, IDENTIFICATION_ID_VALUE)
                .header(IDENTIFICATION_TYPE, IDENTIFICATION_TYPE_VALUE)
                .header("session-tracker", "4532ghj-4df4gh5sd")
                .header("channel", "dbb")
                .header("request-timestamp", "2021-12-10")
                .header("app-version", "1.1.0")
                .header("user-agent", "myUserAgent")
                .header("ip", "1.1.1.1")
                .header("device-id", "25ddf-sdf5g4")
                .header("platform-type", "mobile")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody()
                .jsonPath("$.[0].type").isNotEmpty()
                .jsonPath("$.[0].type").isEqualTo("CUENTA_AHORRO")
                .jsonPath("$.[0].numbers").isNotEmpty()
                .jsonPath("$.[0].numbers[0]").isEqualTo(123L);
        System.out.println(a);
    }

    @Test
    void shouldGetProductsWhenNotSendIdetificationNumber() {
        List<String> productNumbers = new ArrayList<>();
        productNumbers.add("123");
        when(productRepository.getCustomerProducts(any()))
                .thenReturn(Flux.just(new ProductType("CUENTA_AHORRO", productNumbers)));
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(PRODUCT_ROUTE).build())
                .header("message-id", "d3f21df-ds3d1-g13524")
                .header(IDENTIFICATION_TYPE, IDENTIFICATION_TYPE_VALUE)
                .header("session-tracker", "4532ghj-4df4gh5sd")
                .header("channel", "dbb")
                .header("request-timestamp", "2021-12-10")
                .header("app-version", "1.1.0")
                .header("user-agent", "myUserAgent")
                .header("ip", "1.1.1.1")
                .header("device-id", "25ddf-sdf5g4")
                .header("platform-type", "mobile")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isEqualTo(BusinessErrorMessage.DOCUMENT_NUMBER_CUSTOMER_IS_NULL.getMessage())
                .jsonPath("$.errors[0].code").isEqualTo(BusinessErrorMessage.DOCUMENT_NUMBER_CUSTOMER_IS_NULL.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(BusinessErrorMessage.DOCUMENT_NUMBER_CUSTOMER_IS_NULL.getMessage())
                .jsonPath("$.errors[0].domain").isEqualTo(PRODUCT_ROUTE);
    }

    @Test
    void shouldGetProductsWhenNotSendIdetificationType() {
        List<String> productNumbers = new ArrayList<>();
        productNumbers.add("123");
        when(productRepository.getCustomerProducts(any()))
                .thenReturn(Flux.just(new ProductType("CUENTA_AHORRO", productNumbers)));
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(PRODUCT_ROUTE).build())
                .header("message-id", "d3f21df-ds3d1-g13524")
                .header(IDENTIFICATION_NUMBER, IDENTIFICATION_ID_VALUE)
                .header("session-tracker", "4532ghj-4df4gh5sd")
                .header("channel", "dbb")
                .header("request-timestamp", "2021-12-10")
                .header("app-version", "1.1.0")
                .header("user-agent", "myUserAgent")
                .header("ip", "1.1.1.1")
                .header("device-id", "25ddf-sdf5g4")
                .header("platform-type", "mobile")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].reason").isEqualTo(BusinessErrorMessage.DOCUMENT_TYPE_CUSTOMER_IS_NULL.getMessage())
                .jsonPath("$.errors[0].code").isEqualTo(BusinessErrorMessage.DOCUMENT_TYPE_CUSTOMER_IS_NULL.getCode())
                .jsonPath("$.errors[0].message").isEqualTo(BusinessErrorMessage.DOCUMENT_TYPE_CUSTOMER_IS_NULL.getMessage())
                .jsonPath("$.errors[0].domain").isEqualTo(PRODUCT_ROUTE);
    }
}
