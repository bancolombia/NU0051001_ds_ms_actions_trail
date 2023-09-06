package co.com.bancolombia.commons;

import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.commons.PageSummary;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageSummaryTest {

    @Test
    <T> void shouldValidateGettersSuccessfully() {

        int pageNumber = 1;
        int pageSize = 1;
        long totalRegisters = 3;
        List<T> data = new ArrayList<>();
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        PageSummary<T> pageSummary = new PageSummary<>(data, pageRequest, totalRegisters);

        assertEquals(data, pageSummary.getData());
        assertEquals(data.size(), pageSummary.getSize());
        assertEquals(pageNumber, pageSummary.getPageNumber());
        assertEquals(totalRegisters, pageSummary.getTotalPages());
        assertEquals(pageRequest, pageSummary.getPageRequest());
    }

    @Test
    <T> void shouldValidatePaginationPagesSuccessfully() {

        long totalRegisters = 3;
        int pageNumber = 1;
        int pageSize = 1;
        List<T> data = new ArrayList<>();

        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        PageSummary<T> pageSummary = new PageSummary<>(data, pageRequest, totalRegisters);
        assertTrue(pageSummary.isFirst());
        assertTrue(pageSummary.hasNext());
        assertFalse(pageSummary.hasPrevious());

        pageNumber = 2;
        pageRequest = new PageRequest(pageNumber, pageSize);
        pageSummary = new PageSummary<>(data, pageRequest, totalRegisters);
        assertFalse(pageSummary.isFirst());
        assertFalse(pageSummary.isLast());
        assertTrue(pageSummary.hasNext());
        assertTrue(pageSummary.hasPrevious());

        pageNumber = 3;
        pageRequest = new PageRequest(pageNumber, pageSize);
        pageSummary = new PageSummary<>(data, pageRequest, totalRegisters);
        assertTrue(pageSummary.isLast());
        assertFalse(pageSummary.hasNext());
        assertTrue(pageSummary.hasPrevious());

        totalRegisters = 0;
        pageSummary = new PageSummary<>(data, pageRequest, totalRegisters);
        assertFalse(pageSummary.isFirst());
        assertFalse(pageSummary.hasNext());
        assertFalse(pageSummary.hasPrevious());
        assertFalse(pageSummary.isLast());
    }

    @Test
    void shouldThrowUnsupportedOperationExceptionWhenAddingElementToDataList() {
        int pageNumber = 1;
        int pageSize = 1;
        long totalRegisters = 3;
        List<String> data = new ArrayList<>();
        PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
        List<String> unmodifiableData = new PageSummary<>(data, pageRequest, totalRegisters).getData();
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableData.add("test"));
    }

    @Test
    <T> void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        List<T> data = new ArrayList<>();
        PageRequest pageRequest = new PageRequest(1, 1);

        assertThrows(NullPointerException.class, () -> new PageSummary<>(null, pageRequest, 10L));
        assertThrows(NullPointerException.class, () -> new PageSummary<>(data, null, 10L));
        assertThrows(NullPointerException.class, () -> new PageSummary<>(data, pageRequest, null));
    }
}