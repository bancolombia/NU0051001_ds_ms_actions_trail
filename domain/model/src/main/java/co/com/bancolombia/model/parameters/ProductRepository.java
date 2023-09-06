package co.com.bancolombia.model.parameters;

import co.com.bancolombia.model.commons.Context;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Flux<ProductType> getCustomerProducts(Context context);
    Flux<ProductType> searchProducts(Context context);
    Mono<ProductType> searchProductNumber(String productType, Context context);
}
