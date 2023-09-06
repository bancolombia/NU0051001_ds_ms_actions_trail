package co.com.bancolombia.api.parameter;

import co.com.bancolombia.api.commons.request.ContextBuilder;
import co.com.bancolombia.api.commons.response.ResponseHelper;
import co.com.bancolombia.api.parameter.dto.ProductDataResponseDTO;
import co.com.bancolombia.model.parameters.ParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ParameterHandler {

    private final ParameterRepository parameterRepository;
    private final ContextBuilder contextBuilder;
    private final ParameterProperties parameterProperties;

    public Mono<ServerResponse> getParameters(ServerRequest serverRequest) {
        return contextBuilder.getContext(serverRequest)
                .flatMap(context -> parameterRepository.getParameters(context.getChannel(),
                        parameterProperties.getParameterName(), parameterProperties.getTransactionCode()))
                .map(ProductDataResponseDTO::new)
                .flatMap(ResponseHelper.successWrapper);
    }
}