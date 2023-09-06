package co.com.bancolombia.api.products;

import co.com.bancolombia.api.commons.request.ContextBuilder;
import co.com.bancolombia.api.commons.response.ResponseHelper;
import co.com.bancolombia.api.products.builder.ProductData;
import co.com.bancolombia.model.parameters.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductsHandler {

    private final ProductRepository productRepository;
    private final ContextBuilder contextBuilder;

    public Mono<ServerResponse> getListProducts(ServerRequest serverRequest) {
        var productDataFlux= contextBuilder.getContext(serverRequest)
                .flatMapMany(productRepository::getCustomerProducts)
                .map(data-> ProductData.builder().type(data.getName())
                        .numbers(data.getProductNumbers()).build());

        return ResponseHelper.successFluxWrapper(productDataFlux);
    }
}