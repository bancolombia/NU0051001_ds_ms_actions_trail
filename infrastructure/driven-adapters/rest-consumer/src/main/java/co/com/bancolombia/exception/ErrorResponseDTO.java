package co.com.bancolombia.exception;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {

    private List<ErrorDTO> errors;

    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorDTO {

        private String reason;
        private String domain;
        private String code;
        private String message;
    }
}