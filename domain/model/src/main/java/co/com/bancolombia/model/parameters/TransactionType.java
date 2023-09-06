package co.com.bancolombia.model.parameters;

import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

/**
 * TransactionType represents the different available transactions that a
 * transaction can have from one specific type.
 */
@Value
public class TransactionType {

    @NonNull String name;
    @NonNull List<String> transactions;

    public List<String> getTransactions() {
        return Collections.unmodifiableList(this.transactions);
    }
}
