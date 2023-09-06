package co.com.bancolombia.action;

import co.com.bancolombia.model.actions.report.AvailableFormat;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvailableFormatEnumTest {

    @ParameterizedTest
    @CsvSource({
            "PDF, pdf",
            "XLSX, xlsx"
    })
    void shouldValidateAllAvailableFormatsSuccessfully(String enumFormat, String format) {
        AvailableFormat availableFormat = AvailableFormat.valueOf(enumFormat);
        assertEquals(format, availableFormat.getFormat());
    }
}
