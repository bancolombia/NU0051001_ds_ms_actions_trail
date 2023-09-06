package co.com.bancolombia.parameters;

import co.com.bancolombia.model.parameters.TransactionType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTypeTest {

    @Test
    void shouldCreateTransactionTypeSuccessfully() {
        String name = "test";
        List<String> transactions = new ArrayList<>();

        TransactionType transactionType = new TransactionType(name, transactions);
        assertEquals(name, transactionType.getName());
        assertEquals(transactions, transactionType.getTransactions());
    }

    @Test
    void shouldThrowUnsupportedOperationExceptionWhenAddingElementToTransactionsList() {
        String name = "test";
        List<String> transactions = new ArrayList<>();

        List<String> unmodifiableTransactions = new TransactionType(name, transactions).getTransactions();
        assertThrows(UnsupportedOperationException.class, () -> unmodifiableTransactions.add("Test"));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        List<String> transactions = new ArrayList<>();

        assertThrows(NullPointerException.class, () -> new TransactionType(null, transactions));
        assertThrows(NullPointerException.class, () -> new TransactionType("test", null));
    }
}