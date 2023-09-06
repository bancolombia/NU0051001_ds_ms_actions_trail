package co.com.bancolombia.model.search;


import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import co.com.bancolombia.model.search.enums.ComparisonOperator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * SearchValue represents the value object that will be compared by the criterion.
 */
@Getter
@EqualsAndHashCode
public class SearchValue<T> {

    private final T value;
    private final SearchRangeValue<T> rangeValue;
    private final ComparisonOperator operator;

    public SearchValue(@NonNull T value, @NonNull ComparisonOperator operator) {
        if (operator.equals(ComparisonOperator.RANGE)) {
            throw new BusinessException(BusinessErrorMessage.INVALID_OPERATOR);
        }
        this.value = value;
        this.rangeValue = null;
        this.operator = operator;
    }

    public SearchValue(@NonNull T fromValue, @NonNull T toValue) {
        this.rangeValue = new SearchRangeValue<>(fromValue, toValue);
        this.value = null;
        this.operator = ComparisonOperator.RANGE;
    }

    public boolean isSingleValue() {
        return this.value != null;
    }

    public boolean isMultipleValue() {
        return this.rangeValue != null;
    }
}
