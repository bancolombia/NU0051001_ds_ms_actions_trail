package co.com.bancolombia.event.helper;

import co.com.bancolombia.event.DataBuilder;
import co.com.bancolombia.event.search.dto.commons.ErrorDTO;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorBuildHelperTest {

    private static final String NUMBER_EXCEPTION_MESSAGE = "Number format exception";

    @Test
    void shouldGetStatus() {

        var status = ErrorBuildHelper.getStatus(new BusinessException(BusinessErrorMessage.INVALID_PAGE_NUMBER));

        assertNotNull(status);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, status);
    }

    @Test
    void shouldGetStatusWhenErrorIsNullOrNotExist() {

        var status = ErrorBuildHelper.getStatus(null);

        assertNotNull(status);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status);
    }

    @Test
    void shouldGetStatusWhenErrorIsTechnicalException() {

        var status = ErrorBuildHelper.getStatus(new TechnicalException(new UnsupportedOperationException(),
                TechnicalErrorMessage.DATABASE_CONNECTION));

        assertNotNull(status);
        assertEquals(HttpStatus.CONFLICT, status);
    }

    @Test
    void shouldGetErrorDTOBusiness() {

        var context = DataBuilder.getContext();
        var businessError = new BusinessException(BusinessErrorMessage.INVALID_PAGE_NUMBER);

        var error = ErrorBuildHelper.getError(businessError, context);
        var expected = new ErrorDTO(businessError, context.getDomain());

        assertNotNull(error);
        assertEquals(expected.getCode(), error.getCode());
        assertEquals(expected.getDomain(), error.getDomain());
        assertEquals(expected.getMessage(), error.getMessage());
        assertEquals(expected.getReason(), error.getReason());
    }

    @Test
    void shouldGetErrorDTOTechnical() {

        var context = DataBuilder.getContext();
        var technicalError = new TechnicalException(new UnsupportedOperationException(),
                TechnicalErrorMessage.DATABASE_CONNECTION);

        var error = ErrorBuildHelper.getError(technicalError, context);
        var expected = new ErrorDTO(technicalError, context.getDomain());

        assertNotNull(error);
        assertEquals(expected.getCode(), error.getCode());
        assertEquals(expected.getDomain(), error.getDomain());
        assertEquals(expected.getMessage(), error.getMessage());
        assertEquals(expected.getReason(), error.getReason());
    }

    @Test
    void shouldGetErrorDTOUndefined() {

        var context = DataBuilder.getContext();
        var genericError = new Throwable(NUMBER_EXCEPTION_MESSAGE, new NumberFormatException(NUMBER_EXCEPTION_MESSAGE));

        var error = ErrorBuildHelper.getError(genericError, context);
        var expected = new ErrorDTO(genericError, context.getDomain());

        assertNotNull(error);
        assertEquals(expected.getCode(), error.getCode());
        assertEquals(expected.getDomain(), error.getDomain());
        assertEquals(expected.getMessage(), error.getMessage());
        assertEquals(expected.getReason(), error.getReason());
    }

}