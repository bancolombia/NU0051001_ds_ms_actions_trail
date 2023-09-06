package co.com.bancolombia.api.actionsearch;

import co.com.bancolombia.api.openapi.OpenAPI;
import co.com.bancolombia.api.properties.RouteProperties.RouteActionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@Configuration
@RequiredArgsConstructor
public class ActionSearchRouter {

    private final RouteActionProperties routeActionProperties;

    @Bean
    public RouterFunction<ServerResponse> actionSearchRoutes(ActionSearchHandler actionSearchHandler) {
        var actionSearchUri = routeActionProperties.getActionSearch();
        return route()
                .POST(actionSearchUri.getByCriteria(), actionSearchHandler::searchActions, OpenAPI::searchActions)
                .GET(actionSearchUri.getByTrackerId(), actionSearchHandler::getActionsByTrackerId
                        , OpenAPI::getActionsByTrackerId)
                .build();
    }

}