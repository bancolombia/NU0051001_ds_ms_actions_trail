package co.com.bancolombia.config;

import co.com.bancolombia.d2b.model.oauth.ClientCredentialsFlowClient;
import co.com.bancolombia.d2b.webclient.D2BWebClientFactory;
import co.com.bancolombia.d2b.webclient.cognitoauth.CognitoAuthFilterFunction;
import co.com.bancolombia.d2b.webclient.model.WebClientRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public ExchangeFilterFunction cognitoCredentialsExchangeFunction(ClientCredentialsFlowClient clientCredentialsFlowClient) {
        return new CognitoAuthFilterFunction(clientCredentialsFlowClient);
    }

    @Primary
    @Bean
    public WebClient createD2bWebClient(D2BWebClientFactory d2bWebClientFactory,
                                        ExchangeFilterFunction cognitoCredentialsExchangeFunction,
                                        @Value("${adapter.rest-consumer.base-url}") String baseUrl) {
        return d2bWebClientFactory.createWebClientFor(
                WebClientRequest.builder()
                        .exchangeFilterFunction(cognitoCredentialsExchangeFunction)
                        .urlBase(baseUrl)
                        .build()
        );
    }
}