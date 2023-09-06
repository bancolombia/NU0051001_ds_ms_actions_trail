package co.com.bancolombia.parameters;

import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.mapper.ParametersMapper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParametersMapperTest {

    @Test
    void shouldHaveUtilityClassAnnotation() {
        assertThrows(InvocationTargetException.class, () -> {
            var constructor = ParametersMapper.class.getDeclaredConstructor();
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }

    @Test
    void shouldMapParameterResponseToParametersMapSuccessfully() {
        String key1 = "param1";
        String key2 = "param2";
        String value = "test";
        String json = "{\"value\": \"" + value + "\"}";

        var param1 = new ParametersResponseDTO.ParameterValueDTO(key1, json);
        var param2 = new ParametersResponseDTO.ParameterValueDTO(key2, json);
        List<ParametersResponseDTO.ParameterValueDTO> parameters = List.of(param1, param2);
        ParametersResponseDTO.ParametersDTO parametersDTO = new ParametersResponseDTO.ParametersDTO(parameters);
        ParametersResponseDTO parametersResponseDTO = new ParametersResponseDTO(parametersDTO);

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(key1, value);
        parameterMap.put(key2, value);
        assertEquals(parameterMap, ParametersMapper.toParametersMap(parametersResponseDTO));
    }

    @Test
    void shouldThrowProcessingExceptionWhenErrorInFormatJson() {
        String key1 = "param1";
        String json = "{\"error\": \" any \"}";

        var param1 = new ParametersResponseDTO.ParameterValueDTO(key1, json);
        ParametersResponseDTO.ParametersDTO parametersDTO = new ParametersResponseDTO.ParametersDTO(List.of(param1));
        ParametersResponseDTO parametersResponseDTO = new ParametersResponseDTO(parametersDTO);

        var exception = assertThrowsExactly(TechnicalException.class, () -> ParametersMapper.toParametersMap(parametersResponseDTO));
        assertEquals(TechnicalErrorMessage.ERROR_PROCESSING_PARAMETERS_JSON, exception.getTechnicalErrorMessage());
    }
}