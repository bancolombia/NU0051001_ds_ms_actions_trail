package co.com.bancolombia.parameters;

import co.com.bancolombia.exception.ErrorResponseDTO;
import co.com.bancolombia.exception.RestErrorMapper;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.mapper.ParametersMapper;
import co.com.bancolombia.model.parameters.ParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Repository
@EnableConfigurationProperties(ParameterProperties.class)
public class ParameterRepositoryImpl implements ParameterRepository {

    private final WebClient webClient;
    private final ParameterProperties parameterProperties;
    private final RestErrorMapper restErrorMapper;
    private static final String CHANNEL_QUERY_PARAM = "channelId";
    private static final String PARAMETER_NAME_QUERY_PARAM = "paramName";
    private static final String TRANSACTION_CODE_QUERY_PARAM = "transactionCode";

    public Mono<Map<String, Object>> getParameters(String channel, String parameterName, String transactionCode) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(parameterProperties.getParameterPath())
                        .queryParam(CHANNEL_QUERY_PARAM, channel)
                        .queryParam(PARAMETER_NAME_QUERY_PARAM, parameterName)
                        .queryParam(TRANSACTION_CODE_QUERY_PARAM, transactionCode)
                        .build())
                .exchangeToMono(this::getResponse)
                .map(ParametersMapper::toParametersMap)
                .onErrorMap(TimeoutException.class, ex ->
                        new TechnicalException(ex, TechnicalErrorMessage.TIME_OUT_EXCEPTION))
                .onErrorMap(WebClientException.class, ex ->
                        new TechnicalException(ex, TechnicalErrorMessage.REST_CLIENT_EXCEPTION))
                .onErrorMap(ex -> ex instanceof TechnicalException ?
                        ex : new TechnicalException(ex, TechnicalErrorMessage.UNEXPECTED_EXCEPTION));
    }

    private Mono<ParametersResponseDTO> getResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().is2xxSuccessful()) {
            return clientResponse.bodyToMono(ParametersResponseDTO.class);
        }
        return clientResponse.bodyToMono(ErrorResponseDTO.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(buildTechnicalEmptyBodyException(clientResponse))))
                .map(errorResponseDTO -> errorResponseDTO.getErrors().get(0).getCode())
                .map(restErrorMapper::getTechnicalErrorMessage)
                .flatMap(technicalErrorMessage -> Mono.error(new TechnicalException(null, technicalErrorMessage)));
    }

    private TechnicalException buildTechnicalEmptyBodyException(ClientResponse clientResponse) {
        return new TechnicalException(new Exception(clientResponse.statusCode().toString()),
                TechnicalErrorMessage.REST_CLIENT_EXCEPTION);
    }
}
