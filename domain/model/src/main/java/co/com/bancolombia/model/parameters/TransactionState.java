package co.com.bancolombia.model.parameters;

import lombok.NonNull;
import lombok.Value;


/**
 * TransactionState represents the different available states that a transaction can have.
 */
@Value
public class TransactionState {

    @NonNull String name;
}
