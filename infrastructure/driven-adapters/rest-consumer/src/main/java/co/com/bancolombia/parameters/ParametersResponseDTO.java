package co.com.bancolombia.parameters;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ParametersResponseDTO {

    private ParametersDTO data;

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParametersDTO {

        private List<ParameterValueDTO> paramValue;
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParameterValueDTO {

        private String type;
        private String description;
    }

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DescriptionDTO {

        private String field;
        private Object value;
    }
}
