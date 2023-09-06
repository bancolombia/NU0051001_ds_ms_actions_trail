package co.com.bancolombia.api.products;
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
public class ProductsRouter {
    private final RouteParameterProperties routeParameterProperties;
    @Bean
    public RouterFunction<ServerResponse> listProductsRoute(ProductsHandler productsHandler){
        return route()
                .GET(routeParameterProperties.getProduct()
                        ,productsHandler::getListProducts, OpenAPI::getProducts).build();
    }
}
