package co.com.bancolombia.parameters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParameterPropertiesTest {

    @Test
    void shouldGetParameterPathSuccessfully() {
        String baseUrl = "baseUrl";
        String path = "/path";
        ParameterProperties parameterProperties = new ParameterProperties(baseUrl, path);
        assertEquals(baseUrl.concat(path), parameterProperties.getParameterPath());
    }
}
