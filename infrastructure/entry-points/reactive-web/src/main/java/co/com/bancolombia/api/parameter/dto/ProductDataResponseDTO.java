package co.com.bancolombia.api.parameter.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProductDataResponseDTO {
    private Map<String, Object> data;
}