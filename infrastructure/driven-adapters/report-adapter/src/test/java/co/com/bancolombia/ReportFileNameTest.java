package co.com.bancolombia;

import co.com.bancolombia.model.actions.report.AvailableFormat;
import context.ContextCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportFileNameTest {
    private String dateNow;

    @BeforeEach()
    void setUp() {
        dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-hh-mm-ss"));
    }

    @Test
    void shouldGetFileNameSecureBoxPdfSuccessfully() {
        var reportFileName = ReportFileName.builder()
                .context(ContextCreator.createTestContext())
                .transactionCode("6161")
                .fileName("fileName")
                .format(AvailableFormat.PDF)
                .build().getFileNameSecureBox();
        assertEquals("D2B_6161_CC_00000009043_fileName-" + dateNow + ".pdf"
                , reportFileName);
    }

    @Test
    void shouldGetFileNameSecureBoxXlsxSuccessfully() {
        var reportFileName = ReportFileName.builder()
                .context(ContextCreator.createTestContext())
                .transactionCode("6161")
                .fileName("fileName")
                .format(AvailableFormat.XLSX)
                .build().getFileNameSecureBox();
        assertEquals("D2B_6161_CC_00000009043_fileName-" + dateNow + ".xlsx"
                , reportFileName);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        var context = ContextCreator.createTestContext();
        var withoutCustomer = ReportFileName.builder().fileName("fileName")
                .format(AvailableFormat.PDF);
        var withoutFileName = ReportFileName.builder().context(context)
                .format(AvailableFormat.PDF);
        var withoutAvailableFormat = ReportFileName.builder().context(context).fileName("fileName");
        assertThrows(NullPointerException.class, withoutCustomer::build);
        assertThrows(NullPointerException.class, withoutFileName::build);
        assertThrows(NullPointerException.class, withoutAvailableFormat::build);
    }
}
