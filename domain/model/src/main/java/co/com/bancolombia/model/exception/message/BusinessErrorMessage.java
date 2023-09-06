package co.com.bancolombia.model.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorMessage {

    INVALID_PAGE_NUMBER("ATB0001", "The page number must be greater than 0"),
    INVALID_PAGE_SIZE("ATB0002", "The page size must be greater than 0"),
    INVALID_OPERATOR("ATB0003", "The comparison operator for a single field cannot be RANGE"),
    MISSING_MANDATORY_CRITERIA
            ("ATB0004", "The provided search criteria does not contain the required criteria"),
    INVALID_SEARCH_RANGE("ATB0005",
            "The initial search range value cannot be greater than the final one"),
    INVALID_REPORT_FORMAT("ATB0006", "Invalid report format"),
    NON_ACTION_FOUND("ATB0007", "Could not find any action with the criteria"),
    MESSAGE_ID_IS_NULL("ATB0008", "The message-id header cannot be null or empty"),
    SESSION_TRACKER_IS_NULL("ATB0009", "The session-tracker header cannot be null or empty"),
    REQUEST_TIMESTAMP_IS_NULL("ATB0010", "The request-timestamp header cannot be null or empty"),
    CHANNEL_IS_NULL("ATB0011", "The channel header cannot be null or empty"),
    USER_AGENT_IS_NULL("ATB0012", "The User-Agent header cannot be null or empty"),
    APP_VERSION_IS_NULL("ATB0013", "The app-version header cannot be null or empty"),
    DEVICE_ID_IS_NULL("ATB0014", "The device-id header cannot be null or empty"),
    IP_IS_NULL("ATB0015", "The ip header cannot be null or empty"),
    DOCUMENT_NUMBER_CUSTOMER_IS_NULL
            ("ATB0016", "The identification-number header cannot be null or empty"),
    DOCUMENT_TYPE_CUSTOMER_IS_NULL("ATB0017", "The identification-type header cannot be null or empty"),
    DATE_FORMAT_IS_NOT_VALID
            ("ATB0018", "The startDate and endDate fields must contain the date in the format yyyy-MM-dd"),
    SEARCH_CRITERIA_NOT_FOUND("ATB0019", "The object searchCriteria is not found in body request"),
    DATE_RANGE_IS_NOT_FOUND("ATB0020", "The object dateRange is not found in body request"),
    START_DATE_IS_NOT_FOUND_OR_NULL("ATB0021", "The startDate field cannot be null or empty"),
    END_DATE_IS_NOT_FOUND_OR_NULL("ATB0022", "The endDate field cannot be null or empty"),
    PLATFORM_TYPE_IS_NULL("ATB0023", "The platform-type header cannot be null or empty");

    private final String code;
    private final String message;
}