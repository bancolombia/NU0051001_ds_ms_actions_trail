package co.com.bancolombia.source.products;

import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.parameters.ProductRepository;
import co.com.bancolombia.model.parameters.ProductType;
import co.com.bancolombia.source.model.ActionData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static co.com.bancolombia.exception.technical.message.TechnicalErrorMessage.PRODUCTS_SEARCH;
import static co.com.bancolombia.exception.technical.message.TechnicalErrorMessage.PRODUCT_NUMBER_SEARCH;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class ProductsAdapter implements ProductRepository {
    @Autowired
    private final ReactiveMongoTemplate mongoTemplate;
    private static final String DOCUMENT_TYPE_SEARCH_CRITERIA = "documentType";
    private static final String DOCUMENT_NUMBER_SEARCH_CRITERIA = "documentNumber";
    private static final String ORIGIN_PRODUCT_TYPE_FIELD = "originProductType";
    private static final String ORIGIN_PRODUCT_NUMBER_FIELD = "originProductNumber";

    @Override
    public Flux<ProductType> getCustomerProducts(Context context) {
        return searchProducts(context);
    }

    @Override
    public Flux<ProductType> searchProducts(Context context) {
        var query = productQuery(context);
        return mongoTemplate.findDistinct(query, ORIGIN_PRODUCT_TYPE_FIELD,
                        ActionData.class, String.class)
                .flatMap(productType -> searchProductNumber(productType, context))
                .onErrorMap(e -> new TechnicalException(e, PRODUCTS_SEARCH));
    }

    @Override
    public Mono<ProductType> searchProductNumber(String productType, Context context) {
        var query = productQuery(productType, context);
        return mongoTemplate.findDistinct(query, ORIGIN_PRODUCT_NUMBER_FIELD,
                        ActionData.class, String.class)
                .collectList()
                .map(data -> new ProductType(productType, data))
                .onErrorMap(e -> new TechnicalException(e, PRODUCT_NUMBER_SEARCH))
                ;
    }

    private static Query productQuery(Context context) {
        var query = Query.query(where(DOCUMENT_TYPE_SEARCH_CRITERIA)
                .is(context.getCustomer().getIdentification().getType()));
        query.addCriteria(where(DOCUMENT_NUMBER_SEARCH_CRITERIA)
                .is(context.getCustomer().getIdentification().getNumber()));
        return query;
    }

    private static Query productQuery(String productType, Context context) {
        var query = Query.query(where(DOCUMENT_TYPE_SEARCH_CRITERIA)
                .is(context.getCustomer().getIdentification().getType()));
        query.addCriteria(where(DOCUMENT_NUMBER_SEARCH_CRITERIA)
                .is(context.getCustomer().getIdentification().getNumber()));
        query.addCriteria(where(ORIGIN_PRODUCT_TYPE_FIELD).is(productType));
        return query;
    }

}
