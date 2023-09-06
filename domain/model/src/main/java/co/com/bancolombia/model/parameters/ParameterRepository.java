package co.com.bancolombia.model.parameters;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface ParameterRepository {

    Mono<Map<String, Object>> getParameters(String channel, String parameterName, String transactionCode);
}