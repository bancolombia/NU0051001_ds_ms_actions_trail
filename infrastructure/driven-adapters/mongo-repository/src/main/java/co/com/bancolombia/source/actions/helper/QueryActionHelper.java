package co.com.bancolombia.source.actions.helper;

import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.source.actions.mapper.SearchCriteriaCodeMapper;
import lombok.experimental.UtilityClass;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@UtilityClass
public class QueryActionHelper {

    private static final int ZERO = 0;
    private static final int HOUR_MAX = 23;
    private static final int MINUTE_MAX = 59;
    private static final int SECOND_MAX = 59;
    private static final int NANO_OF_SECOND_MAX = 9999;

    public static Query getGenericFilter(
            SearchCriteriaCodeMapper searchCriteriaCodeMapper, Query query, Set<AnySearchCriterion<?>> setCriteria) {

        setCriteria.forEach(criterion -> {
            if (criterion.getSearchValue().isSingleValue()) {
                query.addCriteria(where(searchCriteriaCodeMapper.getField(criterion.getCode()))
                        .is(criterion.getSearchValue().getValue()));
            } else {
                LocalDate dateTo = (LocalDate) criterion.getSearchValue().getRangeValue().getToValue();
                LocalDate dateFrom = (LocalDate) criterion.getSearchValue().getRangeValue().getFromValue();

                query.addCriteria(where(searchCriteriaCodeMapper.getField(criterion.getCode()))
                        .gte(dateFrom.atTime(ZERO, ZERO, ZERO, ZERO))
                        .lt(dateTo.atTime(HOUR_MAX, MINUTE_MAX, SECOND_MAX, NANO_OF_SECOND_MAX)));
            }
        });
        return query;
    }

}
