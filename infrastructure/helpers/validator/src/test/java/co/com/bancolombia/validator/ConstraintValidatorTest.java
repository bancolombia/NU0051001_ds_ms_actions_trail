package co.com.bancolombia.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Mono;
import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.openMocks;

class ConstraintValidatorTest {

    private ConstraintValidator constraintValidator;

    @Mock
    private Validator validator;

    @BeforeEach
    void setUp() {
        openMocks(this);
        constraintValidator = new ConstraintValidator(validator);
    }

    @Test
    void testValidateDataWithValidData() {
        String validData = "test data";

        Mono<String> result = constraintValidator.validateData(validData);
        assertEquals(validData, result.block());
    }

}