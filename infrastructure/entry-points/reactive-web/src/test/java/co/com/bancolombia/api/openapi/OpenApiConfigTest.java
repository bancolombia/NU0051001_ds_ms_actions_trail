package co.com.bancolombia.api.openapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenApiConfigTest {

    private static final String TITLE = "Actions trail";
    private static final String VERSION = "1.0.0";
    private static final String DESCRIPTION = "Actions trail facilitates the experience for digital banking " +
            "users to search all kind of information that they or other users have made over their financial " +
            "products, such as monetary and non monetary transactions, and the different actions made available " +
            "to users. The information that actions trail provides is a subset of the last 3 months collected " +
            "by the channel's central repository.";

    @Test
    void customOpenAPI() {
        OpenApiConfig api = new OpenApiConfig();
        assertEquals(TITLE, api.customOpenAPI(VERSION).getInfo().getTitle());
        assertEquals(VERSION, api.customOpenAPI(VERSION).getInfo().getVersion());
        assertEquals(DESCRIPTION, api.customOpenAPI(VERSION).getInfo().getDescription());
    }

}