package co.com.bancolombia.parameters;

import co.com.bancolombia.model.parameters.TransactionState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionStateTest {

    @Test
    void shouldCreatTransactionStateSuccessfully() {
        String name = "test";
        TransactionState transactionState = new TransactionState(name);
        assertEquals(name, transactionState.getName());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        assertThrows(NullPointerException.class, () -> new TransactionState(null));
    }
}