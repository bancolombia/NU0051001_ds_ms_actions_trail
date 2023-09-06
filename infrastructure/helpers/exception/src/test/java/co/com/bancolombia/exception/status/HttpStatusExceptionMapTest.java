package co.com.bancolombia.exception.status;

import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpStatusExceptionMapTest {

    @Test
    void testGet() {
        HttpStatus status = HttpStatusExceptionMap.get(TechnicalErrorMessage.UNEXPECTED_EXCEPTION.getCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status);

        status = HttpStatusExceptionMap.get(BusinessErrorMessage.INVALID_PAGE_NUMBER.getCode());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, status);

        status = HttpStatusExceptionMap.get("NonExistingErrorCode");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status);
    }

    @Test
    void testGetDefaultStatus() {
        HttpStatus defaultStatus = HttpStatusExceptionMap.getDefaultStatus();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, defaultStatus);
    }
}