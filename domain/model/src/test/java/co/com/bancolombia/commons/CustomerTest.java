package co.com.bancolombia.commons;

import co.com.bancolombia.model.commons.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerTest {

    @Test
    void shouldCreateCustomerSuccessfully() {
        String type = "CC";
        String number = "123";
        Customer customer = Customer.builder()
                .identification(Customer.Identification.builder()
                        .type(type)
                        .number(number)
                        .build())
                .build();
        assertEquals(type, customer.getIdentification().getType());
        assertEquals(number, customer.getIdentification().getNumber());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        Customer.CustomerBuilder customerNull = Customer.builder();
        Customer.Identification.IdentificationBuilder withoutType = Customer.Identification.builder().number("");
        Customer.Identification.IdentificationBuilder withoutNumber = Customer.Identification.builder().type("");

        assertThrows(NullPointerException.class, customerNull::build);
        assertThrows(NullPointerException.class, withoutType::build);
        assertThrows(NullPointerException.class, withoutNumber::build);
    }
}
