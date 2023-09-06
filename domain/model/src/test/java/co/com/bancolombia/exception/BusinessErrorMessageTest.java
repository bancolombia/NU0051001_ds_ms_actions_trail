package co.com.bancolombia.exception;

import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessErrorMessageTest {

    @ParameterizedTest
    @CsvSource({
            "INVALID_PAGE_NUMBER, ATB0001, The page number must be greater than 0",
            "INVALID_PAGE_SIZE, ATB0002, The page size must be greater than 0",
            "INVALID_OPERATOR, ATB0003, The comparison operator for a single field cannot be RANGE",
            "MISSING_MANDATORY_CRITERIA, ATB0004, The provided search criteria does not contain the required criteria",
            "INVALID_SEARCH_RANGE, ATB0005, The initial search range value cannot be greater than the final one",
            "INVALID_REPORT_FORMAT, ATB0006, Invalid report format",
            "NON_ACTION_FOUND, ATB0007, Could not find any action with the criteria"
    })
    void shouldValidateBusinessErrorMessageSuccessfully(String enumName, String code, String message) {
        BusinessErrorMessage businessErrorMessage = BusinessErrorMessage.valueOf(enumName);
        assertEquals(code, businessErrorMessage.getCode());
        assertEquals(message, businessErrorMessage.getMessage());
    }

}
