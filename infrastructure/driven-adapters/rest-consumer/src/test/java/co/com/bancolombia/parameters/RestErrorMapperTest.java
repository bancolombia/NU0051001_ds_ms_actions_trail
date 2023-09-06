package co.com.bancolombia.parameters;

import co.com.bancolombia.exception.RestErrorMapper;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestErrorMapperTest {

    RestErrorMapper restErrorMapper;

    @BeforeEach
    void setUp() {
        restErrorMapper = new RestErrorMapper();
    }

    @Test
    void shouldGetTechnicalInvalidParameterMessageSuccessfully() {
        assertEquals(TechnicalErrorMessage.PARAMETER_NOT_FOUND, restErrorMapper.getTechnicalErrorMessage("CTB0014"));
    }

    @Test
    void shouldGetTechnicalRestClientMessageWhenCodeDoesNotExits() {
        assertEquals(TechnicalErrorMessage.REST_CLIENT_EXCEPTION, restErrorMapper.getTechnicalErrorMessage("test"));
    }

}
