package co.com.bancolombia.config.format;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.MarginBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import reactor.core.publisher.Mono;

import static net.sf.dynamicreports.report.builder.DynamicReports.margin;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public interface ActionsReportFormatConfig {
    Integer DEFAULT_MARGIN = 20;
    String DEFAULT_FONT = "CIBFontSans";

    Mono<byte[]> convertToFormat(JasperReportBuilder report);
    ReportStyleBuilder getStyleColumn();
    ReportStyleBuilder getStyleColumnTitle();
    static FontBuilder getDefaultFont(){
        return stl.font().setFontName(DEFAULT_FONT);
    }
    static MarginBuilder getDefaultMargin(){
        return margin(DEFAULT_MARGIN);
    }
}
