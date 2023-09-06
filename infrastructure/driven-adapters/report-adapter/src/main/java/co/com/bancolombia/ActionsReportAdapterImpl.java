package co.com.bancolombia;

import co.com.bancolombia.config.ActionsReportProperties;
import co.com.bancolombia.config.format.ActionsReportFormatConfig;
import co.com.bancolombia.config.format.ActionsReportFormatConfigFactory;
import co.com.bancolombia.d2b.model.storage.CloudFile;
import co.com.bancolombia.d2b.model.storage.CloudFileGateway;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.actions.ActionsReportAdapter;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import lombok.RequiredArgsConstructor;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.sf.dynamicreports.report.builder.DynamicReports.report;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(ActionsReportProperties.class)
public class ActionsReportAdapterImpl implements ActionsReportAdapter {
    private final CloudFileGateway<DataBuffer> cloudFileGateway;
    private final ActionsReportFormatConfigFactory actionsReportFormatConfigFactory;
    private final ActionsReportProperties actionsReportProperties;
    private final ComponentBuilder componentBuilder;

    private static final Integer FIRST_ELEMENT = 0;

    @Override
    public Mono<byte[]> generateReport(List<Action> actions, AvailableFormat format) {
        var titleReport = actionsReportProperties.getHeader().getReportByCriteriaTitle();
        var informationFields = actionsReportProperties.getFields().getBasicInformation();
        informationFields.putAll(actionsReportProperties.getFields().getDetailedInformation());

        return generateReportXLSX(actions, titleReport, informationFields);
    }

    @Override
    public Mono<byte[]> generateReportHistory(List<Action> actions, AvailableFormat format, Context context) {
        var titleReport = actionsReportProperties.getHeader().getReportHistoryTitle();
        var informationFields = actionsReportProperties.getFields().getHistoryInformation();

        return format.equals(AvailableFormat.PDF) ?
                generateHistoryReportPDF(actions, context, titleReport, informationFields) :
                generateReportXLSX(actions, titleReport, informationFields);
    }

    @Override
    public Mono<byte[]> generateDetailedReport(Action action, AvailableFormat format, Context context) {
        var reportFormat = actionsReportFormatConfigFactory.createReportFormatConfig(format);
        var report = report()
                .setDataSource(new JREmptyDataSource())
                .setPageMargin(ActionsReportFormatConfig.getDefaultMargin())
                .setDefaultFont(ActionsReportFormatConfig.getDefaultFont())
                .setPageFormat(PageType.LETTER, PageOrientation.PORTRAIT)
                .title(componentBuilder.buildPDFTitle())
                .pageHeader(componentBuilder.buildPageHeaderDetailedReport(
                        actionsReportProperties.getHeader().getReportDetailedTitle(),
                        actionsReportProperties.getHeader().getPersonalizedChannelName(), action,
                        context.getDevice().getIp()))
                .detailHeader(componentBuilder.buildDetail(
                                actionsReportProperties.getDetailedReport().getBasicReportSubtitle(),
                                componentBuilder.getFields(
                                        actionsReportProperties.getFields().getBasicInformation(), action)))
                .detail(componentBuilder.buildDetail(
                        actionsReportProperties.getDetailedReport().getDetailedReportSubtitle(),
                        componentBuilder.getFields(
                                actionsReportProperties.getFields().getDetailedInformation(), action)))
                .pageFooter(componentBuilder.buildPageFooter(PageOrientation.PORTRAIT));
        return reportFormat.convertToFormat(report);
    }

    @Override
    public Mono<String> uploadReport(byte[] bytes, Context context, AvailableFormat format) {
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(bytes);
        var fileName = ReportFileName.builder()
                .context(context)
                .transactionCode(actionsReportProperties.getFilename().getSecureMailBoxTransactionCode())
                .fileName(actionsReportProperties.getFilename().getByCriteria())
                .format(format)
                .build()
                .getFileNameSecureBox();
        return cloudFileGateway.put(CloudFile
                        .dataFrom(Flux.just(buffer))
                        .bucketName(actionsReportProperties.getCloudName())
                        .key(fileName)
                        .size(bytes.length)
                        .build())
                .thenReturn(fileName)
                .onErrorResume(error -> Mono.error(new TechnicalException(error, TechnicalErrorMessage.SAVE_REPORT)));
    }

    private Mono<byte[]> generateReportXLSX(List<Action> actions
            , String titleReport, Map<String, String> informationFields) {
        return Mono.just(report())
                .flatMap(report -> {
                    var config = actionsReportFormatConfigFactory
                            .createReportFormatConfig(AvailableFormat.XLSX);
                    report
                            .addTitle(componentBuilder.buildXLSXTitle(titleReport))
                            .setPageFormat(PageType.LETTER, PageOrientation.LANDSCAPE)
                            .columns(componentBuilder.getColumns(actions, config, informationFields))
                            .setDataSource(createDataSource(actions));
                    return config.convertToFormat(report);
                });
    }

    private Mono<byte[]> generateHistoryReportPDF(List<Action> actions, Context context,
                                                  String titleReport, Map<String, String> informationFields) {
        return Mono.just(report())
                .flatMap(report -> {
                    var config = actionsReportFormatConfigFactory.createReportFormatConfig(AvailableFormat.PDF);
                    report
                            .setPageFormat(PageType.LETTER, PageOrientation.LANDSCAPE)
                            .setPageMargin(ActionsReportFormatConfig.getDefaultMargin())
                            .setDefaultFont(ActionsReportFormatConfig.getDefaultFont())
                            .title(componentBuilder.buildPDFTitle())
                            .pageHeader(componentBuilder.buildPageHeaderHistoryReport(titleReport,
                                    actionsReportProperties.getHeader().getPersonalizedChannelName(),
                                    actions.get(FIRST_ELEMENT), context.getDevice().getIp()))
                            .pageFooter(componentBuilder.buildPageFooter(PageOrientation.LANDSCAPE))
                            .columns(componentBuilder.getColumns(actions, config, informationFields))
                            .setDataSource(createDataSource(actions));
                    return config.convertToFormat(report);
                });
    }

    private JRDataSource createDataSource(List<Action> actions) {
        Set<String> titleField = componentBuilder.getMaxSizeAction(actions).keySet();
        var dataSource = new DRDataSource(titleField.toArray(new String[FIRST_ELEMENT]));
        actions.forEach(action -> dataSource
                .add(titleField.stream()
                        .map(key -> componentBuilder.applyFormat(action.get(key)))
                        .toArray())
        );
        return dataSource;
    }

}
