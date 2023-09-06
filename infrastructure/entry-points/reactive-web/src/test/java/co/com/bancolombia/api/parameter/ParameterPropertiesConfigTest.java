package co.com.bancolombia.api.parameter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ParameterPropertiesConfigTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldInitializePropertiesSuccessfully() {
        var parameterProperties = new ParameterProperties();
        parameterProperties.setParameterName("test");
        parameterProperties.setTransactionCode("test");
        Set<ConstraintViolation<ParameterProperties>> violations = validator.validate(parameterProperties);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "transactionCode",
            "parameterName"
    })
    void shouldThrowErrorWhenPropertyNull(String property) {
        var ValidationMessage = "must not be blank";
        var parameterProperties = new ParameterProperties();
        Set<ConstraintViolation<ParameterProperties>> violations = validator.validate(parameterProperties);

        assertEquals(ValidationMessage, violations.stream()
                .filter(violation -> violation.getPropertyPath().toString().equals(property))
                .map(ConstraintViolation::getMessage).collect(Collectors.joining()));
    }
}
