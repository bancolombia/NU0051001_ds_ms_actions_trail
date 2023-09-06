package co.com.bancolombia.parameters;

import co.com.bancolombia.model.parameters.BankEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankEntityTest {

    @Test
    void shouldCreateBankEntitySuccessfully() {
        String code = "test";
        BankEntity bankEntity = new BankEntity(code);
        assertEquals(code, bankEntity.getCode());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        assertThrows(NullPointerException.class, () -> new BankEntity(null));
    }
}