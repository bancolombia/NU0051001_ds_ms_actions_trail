package co.com.bancolombia.api.commons.request;

import co.com.bancolombia.api.properties.PageDefaultProperties;
import co.com.bancolombia.model.commons.PageRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageBuilder {


    public static Mono<PageRequest> getPageRequest(ServerRequest serverRequest, PageDefaultProperties defaultPage) {

        return Mono.just(new PageRequest(
                Integer.parseInt(serverRequest.queryParam("pageNumber")
                        .orElse(defaultPage.getDefaultPageNumber())),
                Integer.parseInt(serverRequest.queryParam("pageSize")
                        .orElse(defaultPage.getDefaultPageSize())))
        );
    }

}