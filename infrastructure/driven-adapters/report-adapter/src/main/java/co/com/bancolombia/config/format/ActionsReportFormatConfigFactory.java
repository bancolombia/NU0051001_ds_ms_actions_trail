package co.com.bancolombia.config.format;

import co.com.bancolombia.model.actions.report.AvailableFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActionsReportFormatConfigFactory {

    @Value("${report.font.size.columnXlsx}")
    private Integer columnXlsxFontSize;
    @Value("${report.font.size.columnPdf}")
    private Integer columnPdfFontSize;
    @Value("${report.style.column.padding}")
    private Integer styleColumnPadding;

    public ActionsReportFormatConfig createReportFormatConfig(AvailableFormat availableFormat){
        ActionsReportFormatConfig actionsReportFormatConfig;
        switch (availableFormat){
            case XLSX:
                actionsReportFormatConfig = new ActionsReportXlsxConfig(columnXlsxFontSize, styleColumnPadding);
                break;
            case PDF:
            default:
                actionsReportFormatConfig = new ActionsReportPdfConfig(columnPdfFontSize, styleColumnPadding);
        }
        return actionsReportFormatConfig;
    }
}
