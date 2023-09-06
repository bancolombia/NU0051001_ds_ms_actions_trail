package co.com.bancolombia.exception.technical;

import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TechnicalExceptionTest {

    @Test
    void whenUnexpectedExceptionThenReturnATechnicalError() {
        TechnicalException technicalException = new TechnicalException(new Throwable(),
                TechnicalErrorMessage.UNEXPECTED_EXCEPTION);

        assertEquals(TechnicalErrorMessage.UNEXPECTED_EXCEPTION, technicalException.getTechnicalErrorMessage());
        assertEquals("Unexpected error", TechnicalErrorMessage.UNEXPECTED_EXCEPTION.getMessage());
        assertEquals("ATT0001", TechnicalErrorMessage.UNEXPECTED_EXCEPTION.getCode());
    }

}
