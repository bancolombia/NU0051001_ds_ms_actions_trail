package co.com.bancolombia.commons;

import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageRequestTest {

    private static final int MAX_SIZE = 100;

    @Test
    void shouldCreatePageRequestSuccessfully() {
        int pageNumber = 1;
        int pageSize = 1;
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        assertEquals(pageRequest.getNumber(), pageNumber);
        assertEquals(pageRequest.getSize(), pageSize);
    }

    @Test
    void shouldCreatePageRequestWithOutPageSizeSuccessfully() {
        int DEFAULT_SIZE = 10;
        int pageNumber = 1;
        PageRequest pageRequest = new PageRequest(pageNumber);
        assertEquals(pageRequest.getNumber(), pageNumber);
        assertEquals(pageRequest.getSize(), DEFAULT_SIZE);
    }

    @Test
    void shouldCreatePageRequestSuccessfullyWhenSizeIsGreaterThan100() {
        int pageNumber = 1;
        int pageSize = 101;
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        assertEquals(pageNumber, pageRequest.getNumber());
        assertEquals(MAX_SIZE, pageRequest.getSize());
    }

    @Test
    void shouldThrowInvalidPageNumberExceptionWhenPageIsLessThan1() {
        int pageNumber = 0;
        int pageSize = 1;
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                new PageRequest(pageNumber, pageSize));
        assertEquals(BusinessErrorMessage.INVALID_PAGE_NUMBER, businessException.getBusinessErrorMessage());
    }

    @Test
    void shouldThrowInvalidPageNumberExceptionWhenPageIsLessThan1InPageRequestWithoutPageSize() {
        int pageNumber = 0;
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                new PageRequest(pageNumber));
        assertEquals(BusinessErrorMessage.INVALID_PAGE_NUMBER, businessException.getBusinessErrorMessage());
    }

    @Test
    void shouldThrowInvalidPageSizeExceptionWhenSizeIsLessThan1() {
        int pageNumber = 1;
        int pageSize = 0;
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                new PageRequest(pageNumber, pageSize));
        assertEquals(BusinessErrorMessage.INVALID_PAGE_SIZE, businessException.getBusinessErrorMessage());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        assertThrows(NullPointerException.class, () -> new PageRequest(0, null));
        assertThrows(NullPointerException.class, () -> new PageRequest(null, 0));
        assertThrows(NullPointerException.class, () -> new PageRequest(null));
    }


}