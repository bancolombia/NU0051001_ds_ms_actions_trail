package co.com.bancolombia.model.search.criteria;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import lombok.NonNull;

import java.time.LocalDate;

/**
 * Criterion used when it is necessary to search for actions between a date range.
 */
public class DateCriterion extends AnySearchCriterion<LocalDate> {

    private static final String SEARCH_CODE = "date";

    public DateCriterion(@NonNull LocalDate fromDate, @NonNull LocalDate toDate) {
        super(SEARCH_CODE, new SearchValue<>(fromDate, toDate));
        if (fromDate.isAfter(toDate)) {
            throw new BusinessException(BusinessErrorMessage.INVALID_SEARCH_RANGE);
        }
    }
}
