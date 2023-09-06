package co.com.bancolombia.api.commons.request;

import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.Customer;
import co.com.bancolombia.validator.ConstraintValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ContextBuilder {

    private final ConstraintValidator constraintValidator;

    public Mono<Context> getContext(ServerRequest serverRequest) {

        return Mono.just(ContextDTO.builder()
                .id(serverRequest.headers().firstHeader("message-id"))
                .sessionId(serverRequest.headers().firstHeader("session-tracker"))
                .channel(serverRequest.headers().firstHeader("channel"))
                .requestDate(serverRequest.headers().firstHeader("request-timestamp"))
                .domain(serverRequest.path())
                .agent(ContextDTO.Agent.builder()
                        .appVersion(serverRequest.headers().firstHeader("app-version"))
                        .name(serverRequest.headers().firstHeader("user-agent")).build())
                .customer(ContextDTO.Customer.builder()
                        .identification(ContextDTO.Customer.Identification.builder()
                                .type(serverRequest.headers().firstHeader("identification-type"))
                                .number(serverRequest.headers().firstHeader("identification-number"))
                                .build())
                        .build())
                .device(ContextDTO.Device.builder()
                        .ip(serverRequest.headers().firstHeader("ip"))
                        .id(serverRequest.headers().firstHeader("device-id"))
                        .type(serverRequest.headers().firstHeader("platform-type")).build())
                .build())
                .flatMap(constraintValidator::validateData)
                .map(ContextBuilder::toContext);
    }

    private static Context toContext(ContextDTO contextDTO) {
        return Context.builder()
                .id(contextDTO.getId())
                .sessionId(contextDTO.getSessionId())
                .channel(contextDTO.getChannel())
                .requestDate(contextDTO.getRequestDate())
                .domain(contextDTO.getDomain())
                .agent(Context.Agent.builder()
                        .appVersion(contextDTO.getAgent().getAppVersion())
                        .name(contextDTO.getAgent().getName()).build())
                .customer(Customer.builder()
                        .identification(Customer.Identification.builder()
                                .type(contextDTO.getCustomer().getIdentification().getType())
                                .number(contextDTO.getCustomer().getIdentification().getNumber())
                                .build())
                        .build())
                .device(Context.Device.builder()
                        .ip(contextDTO.getDevice().getIp())
                        .id(contextDTO.getDevice().getId())
                        .type(contextDTO.getDevice().getType()).build())
                .build();
    }

}