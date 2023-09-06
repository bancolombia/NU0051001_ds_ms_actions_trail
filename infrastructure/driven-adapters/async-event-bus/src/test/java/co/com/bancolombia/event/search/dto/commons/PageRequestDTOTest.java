package co.com.bancolombia.event.search.dto.commons;

import co.com.bancolombia.model.commons.PageRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PageRequestDTOTest {

    @Test
    void shouldCreatePageRequestDTOSuccessfully() {
        Integer number = 1;
        Integer size = 1;

        PageRequest pageRequest = new PageRequest(number, size);
        PageRequestDTO pageRequestDTO = new PageRequestDTO(pageRequest);

        assertEquals(number, pageRequestDTO.getNumber());
        assertEquals(size, pageRequestDTO.getSize());
    }

}