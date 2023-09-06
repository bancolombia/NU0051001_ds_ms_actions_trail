package co.com.bancolombia.model.search;

import lombok.NonNull;

import java.util.Objects;

/**
 * AnySearchCriterion represents the information of an action field by
 * which the different actions will be searched and filtered.
 * It also allows implementing new specific criteria by which actions are required to be searched.
 */
public class AnySearchCriterion<T> {

    protected final String code;
    protected final SearchValue<T> searchValue;

    protected AnySearchCriterion(@NonNull String code, @NonNull SearchValue<T> searchValue) {
        this.code = code;
        this.searchValue = searchValue;
    }

    public final String getCode() {
        return code;
    }

    public final SearchValue<T> getSearchValue() {
        return searchValue;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AnySearchCriterion)) {
            return false;
        }
        AnySearchCriterion<?> anySearchCriterion = (AnySearchCriterion<?>) object;
        return code.equals(anySearchCriterion.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
