package co.com.bancolombia;

import co.com.bancolombia.config.format.ActionsReportFormatConfigFactory;
import co.com.bancolombia.config.format.ActionsReportFormatConfig;
import co.com.bancolombia.config.format.ActionsReportPdfConfig;
import co.com.bancolombia.config.format.ActionsReportXlsxConfig;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest( classes = { ActionsReportFormatConfigFactory.class},
        properties = {
                "report.font.size.columnPdf=5",
                "report.font.size.columnXlsx=12",
                "report.style.column.padding=1"
        })
class ActionsReportFormatConfigFactoryTest {

    @Autowired
    private ActionsReportFormatConfigFactory actionsReportFormatConfigFactory;

    @Test
    void shouldCreateReportFormatConfigPdfWhenFormatIsPdf(){
        ActionsReportFormatConfig actionsReportFormatConfig = actionsReportFormatConfigFactory.createReportFormatConfig(AvailableFormat.PDF);
        assertNotNull(actionsReportFormatConfig);
        assertEquals(ActionsReportPdfConfig.class, actionsReportFormatConfig.getClass());

    }
    @Test
    void shouldCreateReportFormatConfigXlsxWhenFormatIsXlsx(){
        ActionsReportFormatConfig actionsReportFormatConfig = actionsReportFormatConfigFactory.createReportFormatConfig(AvailableFormat.XLSX);
        assertNotNull(actionsReportFormatConfig);
        assertEquals(ActionsReportXlsxConfig.class, actionsReportFormatConfig.getClass());
    }

}
