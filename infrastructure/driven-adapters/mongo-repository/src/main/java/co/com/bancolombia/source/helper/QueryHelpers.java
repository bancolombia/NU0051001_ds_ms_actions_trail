package co.com.bancolombia.source.helper;

import co.com.bancolombia.model.commons.Context;
import lombok.experimental.UtilityClass;
import org.springframework.data.mongodb.core.query.Query;


import java.util.function.BiFunction;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@UtilityClass
public class QueryHelpers {

    public static final BiFunction<Query, Context, Query> contextFilter =
            (query, context) -> {
                var customerIdentification = context.getCustomer().getIdentification();
                return query.addCriteria(where("documentType").is(customerIdentification.getType()))
                        .addCriteria(where("documentNumber").is(customerIdentification.getNumber()));
            };
}

