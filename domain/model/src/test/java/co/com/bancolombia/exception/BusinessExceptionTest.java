package co.com.bancolombia.exception;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessExceptionTest {

    @Test
    void shouldCreateBusinessExceptionSuccessfully() {
        BusinessException businessException = new BusinessException(BusinessErrorMessage.INVALID_PAGE_NUMBER);
        assertEquals(BusinessErrorMessage.INVALID_PAGE_NUMBER, businessException.getBusinessErrorMessage());
        assertEquals(BusinessException.class.getSuperclass(), RuntimeException.class);
    }
}