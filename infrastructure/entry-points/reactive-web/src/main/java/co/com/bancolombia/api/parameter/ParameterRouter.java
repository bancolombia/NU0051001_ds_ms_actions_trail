package co.com.bancolombia.api.parameter;

import co.com.bancolombia.api.openapi.OpenAPI;
import co.com.bancolombia.api.properties.RouteProperties.RouteParameterProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@Configuration
@RequiredArgsConstructor
public class ParameterRouter {

    private final RouteParameterProperties routeParameter;

    @Bean
    public RouterFunction<ServerResponse> parameterRoutes(ParameterHandler parameterHandler) {
        return route()
                .GET(routeParameter.getAllParameters(), parameterHandler::getParameters, OpenAPI::getParameters)
                .build();
    }

}