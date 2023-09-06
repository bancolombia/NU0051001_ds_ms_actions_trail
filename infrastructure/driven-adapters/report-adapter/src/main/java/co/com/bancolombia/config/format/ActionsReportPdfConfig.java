package co.com.bancolombia.config.format;

import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import lombok.RequiredArgsConstructor;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.Rotation;
import net.sf.dynamicreports.report.constant.VerticalImageAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import reactor.core.publisher.Mono;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

@RequiredArgsConstructor
public class ActionsReportPdfConfig implements ActionsReportFormatConfig {

    private final Integer columnPdfFontSize;
    private final Integer styleColumnPadding;
    public static final Integer TITLE_FONT_SIZE = 16;
    public static final Integer SUBTITLE_FONT_SIZE = 13;
    public static final Integer TIMESTAMP_FONT_SIZE = 9;
    public static final Integer ADDITIONAL_DATA_FONT_SIZE = 10;
    public static final Integer FIELD_FONT_SIZE = 12;
    public static final Color SUBTITLE_FONT_COLOR = new java.awt.Color(0x454648);
    public static final Color SECONDARY_FONT_COLOR = new java.awt.Color(0x454648);
    public static final Color TITLE_BACKGROUND_COLOR = new java.awt.Color(0xD9DADD);
    public static final Color BACKGROUND_COLOR = new java.awt.Color(0xF2F2F4);
    public static final Color FOOTER_LINE_COLOR = new java.awt.Color(0xABA59D);

    @Override
    public Mono<byte[]> convertToFormat(JasperReportBuilder report) {
        var bos = new ByteArrayOutputStream();
        try {
            report.toPdf(export.pdfExporter(bos));
        } catch (DRException e) {
            Mono.error(new TechnicalException(e, TechnicalErrorMessage.GENERATE_REPORT));
        }
        return Mono.just(bos.toByteArray());
    }

    private ReportStyleBuilder getStyleCell() {
        return stl.style()
                .setFontSize(columnPdfFontSize)
                .setPadding(styleColumnPadding)
                .setBorder(stl.pen2Point().setLineColor(Color.WHITE))
                .setBackgroundColor(BACKGROUND_COLOR);
    }

    @Override
    public ReportStyleBuilder getStyleColumn() {
        return stl.style(getStyleCell())
                .setHorizontalTextAlignment(HorizontalTextAlignment.JUSTIFIED);
    }

    @Override
    public ReportStyleBuilder getStyleColumnTitle() {
        return stl.style(getStyleCell())
                .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setBackgroundColor(TITLE_BACKGROUND_COLOR);
    }

    public static ReportStyleBuilder getTitleStyle() {
        return stl.style().setFontSize(TITLE_FONT_SIZE).bold();
    }

    public static ReportStyleBuilder getSubTitleStyle() {
        return stl.style().setFontSize(SUBTITLE_FONT_SIZE).setForegroundColor(SECONDARY_FONT_COLOR);
    }

    public static ReportStyleBuilder getTimeStampStyle() {
        return stl.style().setFontSize(TIMESTAMP_FONT_SIZE).setForegroundColor(SECONDARY_FONT_COLOR);
    }

    public static ReportStyleBuilder getAdditionalDataStyle() {
        return stl.style().setFontSize(ADDITIONAL_DATA_FONT_SIZE).bold().setForegroundColor(SECONDARY_FONT_COLOR);
    }

    public static ReportStyleBuilder getDetailSubtitleStyle() {
        return stl.style().setFontSize(SUBTITLE_FONT_SIZE).setFontName(DEFAULT_FONT).bold()
                .setForegroundColor(SUBTITLE_FONT_COLOR);
    }

    public static ReportStyleBuilder getLabelFieldStyle() {
        return stl.style().setFontSize(FIELD_FONT_SIZE).setFontName(DEFAULT_FONT);
    }

    public static ReportStyleBuilder getValueFieldStyle() {
        return stl.style().setForegroundColor(SECONDARY_FONT_COLOR)
                .setFontSize(FIELD_FONT_SIZE).setFontName(DEFAULT_FONT);
    }

    public static ReportStyleBuilder getActionCardPageStyle() {
        return stl.style().setBackgroundColor(BACKGROUND_COLOR).setBorder(stl.pen1Point().setLineColor(Color.white));
    }

    public static ReportStyleBuilder getFooterLineStyle() {
        return stl.style().setForegroundColor(FOOTER_LINE_COLOR);
    }

    public static ReportStyleBuilder getSupervisedLogoStyle() {
        return stl.style().setRotation(Rotation.RIGHT).setVerticalImageAlignment(VerticalImageAlignment.BOTTOM);
    }

    public static ReportStyleBuilder getFooterDataStyle() {
        return stl.style().setFontSize(ADDITIONAL_DATA_FONT_SIZE).setForegroundColor(SUBTITLE_FONT_COLOR);
    }
}
