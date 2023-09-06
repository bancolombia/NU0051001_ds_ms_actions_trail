package co.com.bancolombia;

import co.com.bancolombia.config.ActionsReportProperties;
import co.com.bancolombia.config.ReportImageLoader;
import co.com.bancolombia.config.format.ActionsReportFormatConfig;
import co.com.bancolombia.config.format.ActionsReportPdfConfig;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.actions.Action;
import lombok.RequiredArgsConstructor;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(ActionsReportProperties.class)
public class ComponentBuilder {

    private static final String TIME_FORMAT = "EEEE dd 'de' MMMM 'de' yyyy '-' hh:mm:ss aa";
    private static final String TIME_ZONE = "GMT-5:00";
    private static final String TIME_LANGUAGE = "es";
    private static final String TIME_COUNTRY = "CO";
    private static final String IP_TEXT = "Dirección IP ";
    private static final String CUSTOMER_NAME_FIELD = "customerName";
    private static final String DOCUMENT_TYPE_FIELD = "documentType";
    private static final String DOCUMENT_NUMBER_FIELD = "documentNumber";
    private static final String COLON_STRING = ": ";
    private static final Integer FIRST_ELEMENT = 0;
    private static final Integer MAX_COLUMNS_BY_ROW = 3;
    private static final Integer ADDITIONAL_DATA_HEIGHT = 55;
    private static final Integer MINIMUM_GAP = 10;
    private static final Integer DOUBLE_GAP = 20;
    private static final Integer ACTION_CARD_GAP = 15;
    private static final Integer ROW_MAX_SIZE = (PageType.LETTER.getWidth() -
            ActionsReportFormatConfig.DEFAULT_MARGIN * 2 -
            ACTION_CARD_GAP * (MAX_COLUMNS_BY_ROW + 1)) / MAX_COLUMNS_BY_ROW;

    private final ActionsReportProperties actionsReportProperties;

    @Autowired
    private final ReportImageLoader reportImageLoader;

    public VerticalListBuilder buildField(String label, String value) {
        TextFieldBuilder<String> labelField = cmp.text(label).setStyle(ActionsReportPdfConfig.getLabelFieldStyle());
        TextFieldBuilder<String> valueField = cmp.text(value).setStyle(ActionsReportPdfConfig.getValueFieldStyle());
        return cmp.verticalList(labelField, valueField).setFixedWidth(ROW_MAX_SIZE);
    }

    public VerticalListBuilder buildActionCard(Map<String, String> fields) {
        var verticalListBuilder = cmp.verticalList().setStyle(ActionsReportPdfConfig.getActionCardPageStyle());
        HorizontalListBuilder horizontal = cmp.horizontalList().setStyle(stl.style().setPadding(ACTION_CARD_GAP));
        var iterator = 0;
        for (Map.Entry<String, String> field : fields.entrySet()) {
            if (iterator == MAX_COLUMNS_BY_ROW) {
                verticalListBuilder.add(horizontal);
                horizontal = cmp.horizontalList().setStyle(stl.style().setPadding(ACTION_CARD_GAP).setTopPadding(0));
                iterator = 0;
            }
            horizontal.add(buildField(field.getKey(), field.getValue()));
            if (iterator < MAX_COLUMNS_BY_ROW - 1) {
                horizontal.add(cmp.horizontalGap(ACTION_CARD_GAP));
            }
            iterator++;
        }
        verticalListBuilder.add(horizontal);
        return verticalListBuilder;
    }

    public VerticalListBuilder buildDetail(String subtitle, Map<String, String> informationFields) {
        return cmp.verticalList().add(
                cmp.text(subtitle).setStyle(ActionsReportPdfConfig.getDetailSubtitleStyle()),
                cmp.verticalGap(MINIMUM_GAP),
                buildActionCard(informationFields),
                cmp.pageBreak()
        );
    }

    public TextFieldBuilder<String> buildXLSXTitle(String titleReport) {
        return cmp.text(titleReport);
    }

    public HorizontalListBuilder buildPDFTitle() {
        return reportImageLoader.pageTitle;
    }

    public VerticalListBuilder buildPageHeaderDetailedReport(String reportDetailedReportTitle,
                                                             String personalizedChannelName, Action action, String ip) {
        return cmp.verticalList().add(
                cmp.horizontalList().add(
                        cmp.verticalList().add(
                                cmp.text(personalizedChannelName).setStyle(ActionsReportPdfConfig.getTitleStyle()),
                                cmp.verticalGap(MINIMUM_GAP),
                                cmp.text(reportDetailedReportTitle).setStyle(ActionsReportPdfConfig.getSubTitleStyle()),
                                cmp.verticalGap(MINIMUM_GAP)
                        ),
                        cmp.verticalList().setFixedHeight(ADDITIONAL_DATA_HEIGHT).add(
                                cmp.text(getReportTimeStamp())
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
                                        .setStyle(ActionsReportPdfConfig.getTimeStampStyle()),
                                cmp.text(IP_TEXT + ip)
                                        .setStyle(ActionsReportPdfConfig.getFooterDataStyle())
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                                cmp.text(action.get(CUSTOMER_NAME_FIELD).toString())
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
                                        .setStyle(ActionsReportPdfConfig.getAdditionalDataStyle()),
                                cmp.text(action.get(DOCUMENT_TYPE_FIELD).toString() +
                                                COLON_STRING + action.get(DOCUMENT_NUMBER_FIELD).toString())
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
                                        .setStyle(ActionsReportPdfConfig.getTimeStampStyle())
                        )
                ),
                cmp.verticalGap(DOUBLE_GAP)
        );
    }

    public VerticalListBuilder buildPageHeaderHistoryReport(String reportDetailedReportTitle,
                                                            String personalizedChannelName, Action action, String ip) {
        return cmp.verticalList().add(
                cmp.horizontalList().add(
                        cmp.verticalList().add(
                                cmp.verticalGap(MINIMUM_GAP),
                                cmp.text(personalizedChannelName).setStyle(ActionsReportPdfConfig.getTitleStyle())),
                        cmp.verticalList().add(
                                cmp.verticalGap(MINIMUM_GAP),
                                cmp.text(reportDetailedReportTitle).setStyle(ActionsReportPdfConfig.getSubTitleStyle())
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)),
                        cmp.verticalList().add(
                                cmp.text(getReportTimeStamp())
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
                                        .setStyle(ActionsReportPdfConfig.getTimeStampStyle()),
                                cmp.text(IP_TEXT + ip)
                                        .setStyle(ActionsReportPdfConfig.getFooterDataStyle())
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
                                cmp.text(action.get(CUSTOMER_NAME_FIELD).toString())
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
                                        .setStyle(ActionsReportPdfConfig.getAdditionalDataStyle()),
                                cmp.text(action.get(DOCUMENT_TYPE_FIELD).toString() +
                                                COLON_STRING + action.get(DOCUMENT_NUMBER_FIELD).toString())
                                        .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
                                        .setStyle(ActionsReportPdfConfig.getTimeStampStyle()))
                ),
                cmp.verticalGap(MINIMUM_GAP)
        );
    }

    public VerticalListBuilder buildPageFooter(PageOrientation pageOrientation) {
        if (pageOrientation.equals(PageOrientation.PORTRAIT)) {
            return reportImageLoader.pageFooterDetailedReport;
        } else {
            return reportImageLoader.pageFooterHistoryReport;
        }
    }

    public Action getMaxSizeAction(List<Action> actions) {
        return actions.stream()
                .max(Comparator.comparingInt(HashMap::size))
                .orElseThrow(() -> new TechnicalException(null, TechnicalErrorMessage.GENERATE_REPORT));
    }

    public ColumnBuilder[] getColumns(List<Action> actions, ActionsReportFormatConfig config,
                                      Map<String, String> informationFields) {
        return getColumns(getMaxSizeAction(actions), config, informationFields);
    }

    public ColumnBuilder[] getColumns(Action action, ActionsReportFormatConfig config,
                                      Map<String, String> informationFields) {
        return informationFields.keySet().stream()
                .filter(action.keySet()::contains)
                .map(key -> {
                    var type = applyFormat(action.get(key)).getClass();
                    return DynamicReports.col.column(informationFields.get(key), key, type)
                            .setStyle(config.getStyleColumn())
                            .setTitleStyle(config.getStyleColumnTitle());
                })
                .collect(Collectors.toList())
                .toArray(new ColumnBuilder[FIRST_ELEMENT]);
    }

    public Map<String, String> getFields(Map<String, String> informationFields, Action action) {
        return informationFields.entrySet().stream()
                .filter(field -> action.containsKey(field.getKey()) && action.get(field.getKey()) != null)
                .collect(Collectors.toMap(Map.Entry::getValue, field -> applyFormat(action.get(field.getKey()))));
    }

    public String applyFormat(Object field) {
        return Optional.ofNullable(field)
                .map(f -> {
                    if (f instanceof Number) {
                        return new DecimalFormat(actionsReportProperties.getFormat().getNumber()).format(f);
                    } else if (f instanceof LocalDateTime) {
                        return ((LocalDateTime) f)
                                .format(DateTimeFormatter.ofPattern(actionsReportProperties.getFormat().getDate()));
                    }
                    return f.toString();
                })
                .orElse("");
    }

    private static String getReportTimeStamp() {
        var dateFormat = new SimpleDateFormat(TIME_FORMAT, new Locale(TIME_LANGUAGE, TIME_COUNTRY));
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        String timeStamp = dateFormat.format(Date.from(Instant.now()));
        timeStamp = timeStamp.substring(0, 1).toUpperCase() + timeStamp.substring(1);
        return timeStamp;
    }
}