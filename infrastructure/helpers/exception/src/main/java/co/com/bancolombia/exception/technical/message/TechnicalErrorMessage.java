package co.com.bancolombia.exception.technical.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalErrorMessage {

    UNEXPECTED_EXCEPTION("ATT0001", "Unexpected error"),
    DATABASE_CONNECTION("ATT0002", "Error connecting to database"),
    ERROR_EMITTING_EVENT("ATT0003", "Error emitting event"),
    ERROR_GETTING_SEARCH_CODE_MAPPING("ATT0004","Error getting search code mapping"),
    ACTIONS_SEARCH("ATT0005","Error searching actions"),
    PRODUCTS_SEARCH("ATT0006","Error searching products"),
    PRODUCT_NUMBER_SEARCH("ATT0007","Error searching products number"),
    GENERATE_REPORT("ATT0008", "Error generating report"),
    TIME_OUT_EXCEPTION("ATT0009", "Time out for the consumption of the rest service has been exceeded"),
    REST_CLIENT_EXCEPTION("ATT010", "Unexpected rest client error"),
    PARAMETER_NOT_FOUND("ATT0011", "The parameter does not exist"),
    SAVE_REPORT("ATT0012", "Error saving report"),
    MISSING_FORMAT_HEADER("ATT0013", "header format must be required"),
    INVALID_REPORT_FIELD_FORMAT("ATT0014", "invalid report field format, must be of type JSON"),
    ERROR_PROCESSING_PARAMETERS_JSON("ATT0015", "There is a error in the format of the parameter"),
    TRANSACTION_CODE_IS_REQUIRED("ATT0016", "The transaction code is required");

    private final String code;
    private final String message;
}