package co.com.bancolombia.model.commons;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 Customer represents the users of digital banking with whom there is a contractual relationship.
 */
@Getter
@Builder(toBuilder=true)
@EqualsAndHashCode
public class Customer {

    @NonNull private final Identification identification;

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class Identification {

        @NonNull private final String type;
        @NonNull private final String number;
    }
}
