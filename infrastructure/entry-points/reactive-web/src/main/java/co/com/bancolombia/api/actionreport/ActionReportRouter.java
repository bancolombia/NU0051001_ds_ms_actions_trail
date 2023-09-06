package co.com.bancolombia.api.actionreport;

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
public class ActionReportRouter {

    private final RouteActionProperties routeActionProperties;

    @Bean
    public RouterFunction<ServerResponse> actionReportRoutes(ActionReportHandler actionReportHandler) {
        var actionReportUri = routeActionProperties.getActionReport();
        return route()
                .POST(actionReportUri.getByCriteria(), actionReportHandler::searchActions,
                        OpenAPI::searchActionReport)
                .GET(actionReportUri.getHistory(),
                        actionReportHandler::getActionHistoryByTrackerId,
                        OpenAPI::getActionHistoryByCode)
                .GET(actionReportUri.getDetail(),
                        actionReportHandler::generateDetailedReport,
                        OpenAPI::getActionDetailByCode)
                .build();
    }
}