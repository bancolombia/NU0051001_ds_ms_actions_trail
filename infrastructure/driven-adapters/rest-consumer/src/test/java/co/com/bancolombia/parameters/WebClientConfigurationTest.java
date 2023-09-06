package co.com.bancolombia.parameters;

import co.com.bancolombia.config.WebClientConfiguration;
import co.com.bancolombia.d2b.model.oauth.ClientCredentialsFlowClient;
import co.com.bancolombia.d2b.webclient.D2BWebClientFactory;
import co.com.bancolombia.d2b.webclient.cognitoauth.CognitoAuthFilterFunction;
import co.com.bancolombia.d2b.webclient.model.WebClientRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebClientConfigurationTest {

    @Mock
    private D2BWebClientFactory d2bWebClientFactory;

    @Mock
    private ClientCredentialsFlowClient clientCredentialsFlowClient;

    private WebClientConfiguration webClientConfiguration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webClientConfiguration = new WebClientConfiguration();
    }

    @Test
    void shouldCreateCognitoCredentialsExchangeFilterFunctionSuccessfully() {
        assertTrue(webClientConfiguration.cognitoCredentialsExchangeFunction(clientCredentialsFlowClient)
                instanceof CognitoAuthFilterFunction);
    }

    @Test
    void shouldCreateWebClientWithCognitoCredentialsSuccessfully() {
        String baseUrl = "test";
        WebClient webClient = mock(WebClient.class);
        ExchangeFilterFunction exchangeFilterFunction = webClientConfiguration.cognitoCredentialsExchangeFunction(clientCredentialsFlowClient);

        WebClientRequest request = WebClientRequest.builder()
                .exchangeFilterFunction(exchangeFilterFunction)
                .urlBase(baseUrl)
                .build();

        when(d2bWebClientFactory.createWebClientFor(ArgumentMatchers.refEq(request))).thenReturn(webClient);
        assertEquals(webClient, webClientConfiguration.createD2bWebClient(d2bWebClientFactory, exchangeFilterFunction, baseUrl));
    }
}