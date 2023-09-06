package co.com.bancolombia.api.commons.response;

import co.com.bancolombia.api.products.builder.ProductData;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@UtilityClass
public class ResponseHelper {
    public static final Function<Object, Mono<ServerResponse>> successWrapper = o -> status(OK).bodyValue(o);


    public static @NonNull Mono<ServerResponse> successFluxWrapper(Flux<ProductData> productData) {
        return ServerResponse.status(OK).
                contentType(MediaType.APPLICATION_JSON)
                .body(productData,ProductData.class);
    }
}
