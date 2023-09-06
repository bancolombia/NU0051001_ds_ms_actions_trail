package co.com.bancolombia.config.format;

import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import lombok.RequiredArgsConstructor;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;

import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

@RequiredArgsConstructor
public class ActionsReportXlsxConfig implements ActionsReportFormatConfig {

    private final Integer columnXlsxFontSize;

    private final Integer styleColumnPadding;
    @Override
    public Mono<byte[]> convertToFormat(JasperReportBuilder report){
        var bos = new ByteArrayOutputStream();
        report.ignorePageWidth();
        report.ignorePagination();
        try {
            report.toXlsx(export.xlsxExporter(bos)
                    .setDetectCellType(true)
                    .setIgnorePageMargins(true)
                    .setWhitePageBackground(false)
                    .setRemoveEmptySpaceBetweenColumns(true)
                    .setIgnoreCellBorder(true)
                    .setIgnoreCellBackground(true)
                    .setIgnoreGraphics(true)
            );
        } catch (DRException e) {
            return Mono.error(new TechnicalException(e, TechnicalErrorMessage.GENERATE_REPORT));
        }
        return Mono.just(bos.toByteArray());
    }

    private ReportStyleBuilder getStyleCell() {
        return stl.style()
                .setPadding(styleColumnPadding)
                .setFontSize(columnXlsxFontSize);
    }

    @Override
    public ReportStyleBuilder getStyleColumn() {
        return stl.style(getStyleCell())
                .setHorizontalTextAlignment(HorizontalTextAlignment.JUSTIFIED);
    }

    @Override
    public ReportStyleBuilder getStyleColumnTitle() {
        return stl.style(getStyleCell())
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

    }
}
