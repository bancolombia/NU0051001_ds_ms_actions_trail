package co.com.bancolombia.source.config.model;

import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.Customer;
import co.com.bancolombia.source.products.ProductsAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductsAdapterTest {
    String id = "id";
    String sessionId = "sessionId";
    String requestDate = "requestDate";
    String channel = "channel";
    String agentName = "agentName";
    String appVersion = "appVersion";
    String deviceId = "deviceId";
    String deviceIp = "deviceIp";
    String identificationType = "CC";
    String identificationNumber = "123";
    String domain = "/myPath";
    String deviceType = "mobile";
    String productNumber = "123";
    String productName = "CHANNEL";
    String errorExceptionString = "Error";
    Context context = Context.builder()
            .id(id)
            .sessionId(sessionId)
            .requestDate(requestDate)
            .channel(channel)
            .domain(domain)
            .agent(Context.Agent.builder()
                    .name(agentName)
                    .appVersion(appVersion)
                    .build())
            .device(Context.Device.builder()
                    .id(deviceId)
                    .ip(deviceIp)
                    .type(deviceType)
                    .build())
            .customer(Customer.builder()
                    .identification(Customer.Identification.builder()
                            .type(identificationType)
                            .number(identificationNumber)
                            .build())
                    .build())
            .build();
    @Mock
    ReactiveMongoTemplate mongoTemplate;
    @Test
    void shouldSearchProductNumberSuccessfully(){
        Flux<String> numberProductFlux = Flux.just(productNumber);
        when(mongoTemplate.findDistinct(any(),anyString(), (Class<?>) any(), (Class<String>)any()))
                .thenReturn(numberProductFlux);
        var productType = new ProductsAdapter(mongoTemplate)
                .searchProductNumber(productName,context).block();
        assertEquals(productName, productType.getName());
        assertEquals(productNumber, productType.getProductNumbers().get(0));
    }
    @Test
    void shouldThrowTechnicalProductNumberSearchExceptionSuccessfully(){
        when(mongoTemplate.findDistinct(any(),anyString(), (Class<?>) any(),any()))
                .thenReturn(Flux.error(new Exception(errorExceptionString)));
        var productsAdapter = new ProductsAdapter(mongoTemplate);
        Exception technicalErrorMessage = new Exception(
                String.valueOf(TechnicalErrorMessage.PRODUCT_NUMBER_SEARCH));
        StepVerifier.create(productsAdapter.searchProductNumber(productName,context))
                .expectErrorMatches(error -> {
                    assertThat(technicalErrorMessage).usingRecursiveComparison().isEqualTo(error);
                    return true;
                })
                .verify();
    }
    @Test
    void shouldSearchProductsSuccessfully(){
        Flux<String> productFlux = Flux.just(productName);
        when(mongoTemplate.findDistinct(any(),anyString(), (Class<?>) any(), (Class<String>)any()))
                .thenReturn(productFlux);
        var productsAdapter = new ProductsAdapter(mongoTemplate);
        var products = productsAdapter.searchProducts(context).blockFirst();
        assertEquals(productName, products.getName());
        assertEquals(Boolean.FALSE, products.getProductNumbers().isEmpty());
    }
    @Test
    void shouldThrowTechnicalProductSearchExceptionSuccessfully(){
        when(mongoTemplate.findDistinct(any(),anyString(), (Class<?>) any(), (Class<String>)any()))
                .thenReturn(Flux.error(new Exception(errorExceptionString)));
        var productsAdapter = new ProductsAdapter(mongoTemplate);
        Exception technicalErrorMessage = new Exception(
                String.valueOf(TechnicalErrorMessage.PRODUCTS_SEARCH));
        StepVerifier.create(productsAdapter.searchProducts(context))
                .expectErrorMatches(error -> {
                    assertThat(technicalErrorMessage).usingRecursiveComparison().isEqualTo(error);
                    return true;
                })
                .verify();
    }
}
