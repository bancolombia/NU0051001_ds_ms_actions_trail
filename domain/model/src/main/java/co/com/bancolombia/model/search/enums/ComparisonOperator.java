package co.com.bancolombia.model.search.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ComparisonCriteria represents the possible comparison operators in which the values
 * of the search criteria can be evaluated against another values.
 */
@RequiredArgsConstructor
@Getter
public enum ComparisonOperator {

    EQUALS("EQUALS"),
    GREATER_THAN("GREATER_THAN"),
    LOWER_THAN("LOWER_THAN"),
    RANGE("RANGE");

    private final String operator;
}
