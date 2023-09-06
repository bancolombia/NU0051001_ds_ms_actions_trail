package co.com.bancolombia.model.parameters;

import lombok.NonNull;
import lombok.Value;

/**
 * BankEntity represents the branch offices and subsidiaries that the bank has
 * which ones carry out financial operations.
 */
@Value
public class BankEntity {

    @NonNull String code;
}
