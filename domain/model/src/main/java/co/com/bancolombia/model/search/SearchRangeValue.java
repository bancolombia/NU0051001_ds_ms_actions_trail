package co.com.bancolombia.model.search;

import lombok.NonNull;
import lombok.Value;

/**
 * SearchRangeValue represents the value object that will be compared by the criterion
 * when querying by ranges is required.
 */
@Value
public class SearchRangeValue<T> {

    @NonNull T fromValue;
    @NonNull T toValue;

    @Override
    public String toString() {
        return "{fromValue="
                .concat(fromValue.toString())
                .concat(", toValue=")
                .concat(toValue.toString())
                .concat("}");
    }
}
