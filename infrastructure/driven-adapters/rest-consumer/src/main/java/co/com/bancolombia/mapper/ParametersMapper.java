package co.com.bancolombia.mapper;

import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.parameters.ParametersResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ParametersMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> toParametersMap(ParametersResponseDTO parametersResponseDTO) {
        return parametersResponseDTO.getData().getParamValue().stream()
                .collect(Collectors.toMap(ParametersResponseDTO.ParameterValueDTO::getType,
                        ParametersMapper::getParameterValue));
    }

    @SuppressWarnings("fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
    private Object getParameterValue(ParametersResponseDTO.ParameterValueDTO parameterValue) {
        try {
            return objectMapper.readValue(parameterValue.getDescription(),
                    ParametersResponseDTO.DescriptionDTO.class).getValue();
        } catch (JsonProcessingException e) {
            throw new TechnicalException(e, TechnicalErrorMessage.ERROR_PROCESSING_PARAMETERS_JSON);
        }
    }
}