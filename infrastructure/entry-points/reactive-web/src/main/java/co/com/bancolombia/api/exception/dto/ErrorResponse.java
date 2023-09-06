package co.com.bancolombia.api.exception.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
@Builder(toBuilder = true)
public class ErrorResponse {

    private final List<ErrorDescription> errors;

    @JsonCreator
    public ErrorResponse(@JsonProperty("errors") List<ErrorDescription> errors) {
        this.errors = errors;
    }

    public List<ErrorDescription> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    @Getter
    @EqualsAndHashCode
    @Builder(toBuilder = true)
    public static class ErrorDescription {

        private final String reason;
        private final String domain;
        private final String code;
        private final String message;

        @ConstructorProperties({"reason","domain","code","message"})
        public ErrorDescription(String reason, String domain, String code, String message) {
            this.reason = reason;
            this.domain = domain;
            this.code = code;
            this.message = message;
        }
    }

}