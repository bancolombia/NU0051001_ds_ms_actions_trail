package co.com.bancolombia;

import action.ActionCreator;
import co.com.bancolombia.config.format.ActionsReportFormatConfig;
import co.com.bancolombia.config.format.ActionsReportFormatConfigFactory;
import co.com.bancolombia.config.format.ActionsReportPdfConfig;
import co.com.bancolombia.config.format.ActionsReportXlsxConfig;
import co.com.bancolombia.d2b.model.storage.CloudFileGateway;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.model.actions.ActionsReportAdapter;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import context.ContextCreator;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.exception.DRException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = {
                ActionsReportAdapterImpl.class,
                ActionsReportFormatConfigFactory.class,
                ActionsReportFormatConfig.class,
                CloudFileGateway.class
        },
        properties = {
                "report.images.bankLogo=BankLogoURL",
                "report.images.supervisedLogo=SupervisedLogoURL",
                "report.images.strokeImage=StrokeImageURL",
                "report.header.reportByCriteriaTitle=REPORTE DE ACCIONES",
                "report.header.reportHistoryTitle=HISTORIAL DE TRANSACCIÓN",
                "report.header.reportDetailedTitle=DETALLE DE ACCIÓN",
                "report.header.personalizedChannelName=GALATEA - ENGAGEMENT SERVICES",
                "report.detailed-report.basicReportSubtitle=Información básica",
                "report.detailed-report.detailedReportSubtitle=Detalle de la actividad",
                "report.font.size.columnPdf=5",
                "report.font.size.columnXlsx=12",
                "report.style.column.padding=1",
                "report.format.date=dd/MM/yyyy",
                "report.format.number=#.##################",
                "report.filename.byCriteria=fileName",
                "report.filename.secureMailBoxTransactionCode=6161",
                "report.cloudName=cloudTest",
                "report.fields.basicInformation: {" +
                        "    \"transactionTracker\": \"trackeador de trx\"," +
                        "    \"transactionId\":\"id\"," +
                        "    \"sessionId\": \"id de sesión\"," +
                        "    \"transctionCode\": \"codigo de trx\"," +
                        "    \"finalDate\": \"fecha\"" +
                        "}",
                "report.fields.detailedInformation: {" +
                        "    \"detailTwo\": \"detalle 2\"," +
                        "    \"detailOne\": \"detalle 1\"," +
                        "    \"detailNumber\": \"detalle Entero\"," +
                        "    \"detailNoExist\": \"campo no existente\"" +
                        "}",
                "report.fields.historyInformation: {" +
                        "    \"historyTwo\": \"historia 2\"," +
                        "    \"historyOne\": \"historia 1\"," +
                        "    \"historyThree\": \"historia 3\"" +
                        "}"
        }
)
class ActionsReportAdapterTest {
    @Autowired
    private ActionsReportAdapter actionsReportAdapter;
    @MockBean
    private ActionsReportFormatConfigFactory actionsReportFormatConfigFactory;
    @MockBean
    private CloudFileGateway<DataBuffer> cloudFileGateway;
    @MockBean
    private ActionsReportFormatConfig actionsReportFormatConfig;
    @MockBean
    private ComponentBuilder componentBuilder;
    private List<Action> actions;

    @BeforeEach
    void setUp() {
        Action action = ActionCreator.createTestAction();

        actions = List.of(
                action,
                action,
                action,
                action,
                action
        );
    }

    @Test
    void shouldGenerateReportXlsxWhenFormatIsXlsx() {
        when(actionsReportFormatConfigFactory.createReportFormatConfig(AvailableFormat.XLSX))
                .thenReturn(new ActionsReportXlsxConfig(12, 1));
        when(componentBuilder.buildXLSXTitle(anyString())).thenReturn(cmp.text(""));
        when(componentBuilder.getColumns((List<Action>) any(), any(), any())).thenReturn(new ColumnBuilder[(0)]);
        when(componentBuilder.getMaxSizeAction(actions)).thenReturn(actions.get(0));

        Mono<byte[]> report = actionsReportAdapter.generateReport(actions, AvailableFormat.XLSX);
        assertNotEquals(Mono.empty(), report);
        StepVerifier.create(report)
                .consumeNextWith(r -> {
                    assertTrue(r.length > 0);
                }).verifyComplete();
    }

    @Test
    void shouldThrowTechnicalExceptionWhenGenerateReportConvertFormatFail() {
        when(actionsReportFormatConfig.convertToFormat(any()))
                .thenReturn(Mono.error(new TechnicalException(new DRException(" ")
                        , TechnicalErrorMessage.GENERATE_REPORT)));
        when(actionsReportFormatConfigFactory.createReportFormatConfig(any())).thenReturn(actionsReportFormatConfig);
        when(componentBuilder.buildXLSXTitle(anyString())).thenReturn(cmp.text(""));
        when(componentBuilder.getColumns((List<Action>) any(), any(), any())).thenReturn(new ColumnBuilder[(0)]);
        when(componentBuilder.getMaxSizeAction(actions)).thenReturn(actions.get(0));

        Mono<byte[]> report = actionsReportAdapter.generateReport(actions, AvailableFormat.XLSX);
        StepVerifier.create(report)
                .expectErrorMatches(error ->
                        error.getClass().equals(TechnicalException.class) &&
                                error.getMessage().equals(TechnicalErrorMessage.GENERATE_REPORT.getMessage())
                )
                .verify();
    }

    @Test
    void shouldGenerateReportHistoryPdfWhenFormatIsPdf() {
        var context = ContextCreator.createTestContext();
        when(actionsReportFormatConfigFactory.createReportFormatConfig(AvailableFormat.PDF))
                .thenReturn(new ActionsReportPdfConfig(5, 1));
        when(componentBuilder.buildPDFTitle()).thenReturn(cmp.horizontalList());
        when(componentBuilder.buildPageHeaderHistoryReport(anyString(), anyString(), any(), anyString())).thenReturn(cmp.verticalList());
        when(componentBuilder.buildPageFooter(PageOrientation.LANDSCAPE)).thenReturn(cmp.verticalList());
        when(componentBuilder.getColumns((List<Action>) any(), any(), any())).thenReturn(new ColumnBuilder[(0)]);
        when(componentBuilder.getMaxSizeAction(actions)).thenReturn(actions.get(0));

        Mono<byte[]> report = actionsReportAdapter.generateReportHistory(actions, AvailableFormat.PDF, context);
        assertNotEquals(Mono.empty(), report);
        StepVerifier.create(report)
                .expectNextMatches(r -> r.length > 0)
                .verifyComplete();
    }

    @Test
    void shouldGenerateReportHistoryXlsxWhenFormatIsXlsx() {
        var context = ContextCreator.createTestContext();
        when(actionsReportFormatConfigFactory.createReportFormatConfig(AvailableFormat.XLSX))
                .thenReturn(new ActionsReportXlsxConfig(12, 1));
        when(componentBuilder.buildXLSXTitle(anyString())).thenReturn(cmp.text(""));
        when(componentBuilder.getColumns((List<Action>) any(), any(), any())).thenReturn(new ColumnBuilder[(0)]);
        when(componentBuilder.getMaxSizeAction(actions)).thenReturn(actions.get(0));

        Mono<byte[]> report = actionsReportAdapter.generateReportHistory(actions, AvailableFormat.XLSX, context);
        assertNotEquals(Mono.empty(), report);
        StepVerifier.create(report)
                .consumeNextWith(r -> {
                    assertTrue(r.length > 0);
                }).verifyComplete();
    }

    @Test
    void shouldThrowTechnicalExceptionWhenGenerateReportHistoryConvertFormatFail() {
        when(actionsReportFormatConfig.convertToFormat(any()))
                .thenReturn(Mono.error(new TechnicalException(new DRException(" ")
                        , TechnicalErrorMessage.GENERATE_REPORT)));
        when(actionsReportFormatConfigFactory.createReportFormatConfig(any())).thenReturn(actionsReportFormatConfig);
        when(componentBuilder.buildXLSXTitle(anyString())).thenReturn(cmp.text(""));
        when(componentBuilder.getColumns((List<Action>) any(), any(), any())).thenReturn(new ColumnBuilder[(0)]);
        when(componentBuilder.getMaxSizeAction(actions)).thenReturn(actions.get(0));

        Mono<byte[]> report = actionsReportAdapter.generateReport(actions, AvailableFormat.XLSX);
        StepVerifier.create(report)
                .expectErrorMatches(error ->
                        error.getClass().equals(TechnicalException.class) &&
                                error.getMessage().equals(TechnicalErrorMessage.GENERATE_REPORT.getMessage())
                )
                .verify();
    }

    @Test
    void shouldCreateDetailedPDFReportSuccessfully() {
        var context = ContextCreator.createTestContext();
        when(actionsReportFormatConfigFactory.createReportFormatConfig(AvailableFormat.PDF))
                .thenReturn(new ActionsReportPdfConfig(5, 1));
        when(componentBuilder.buildPDFTitle()).thenReturn(cmp.horizontalList());
        when(componentBuilder.buildPageHeaderDetailedReport(anyString(), anyString(), any(), anyString())).thenReturn(cmp.verticalList());
        when(componentBuilder.buildDetail(anyString(), anyMap())).thenReturn(cmp.verticalList());
        when(componentBuilder.buildPageFooter(PageOrientation.PORTRAIT)).thenReturn(cmp.verticalList());
        Mono<byte[]> report = actionsReportAdapter.generateDetailedReport(actions.get(0), AvailableFormat.PDF, context);
        StepVerifier.create(report)
                .expectNextMatches(bytes -> bytes.length > 0)
                .verifyComplete();
    }

    @Test
    void shouldCreateDetailedXlsxReportSuccessfully() {
        when(actionsReportFormatConfigFactory.createReportFormatConfig(AvailableFormat.XLSX))
                .thenReturn(new ActionsReportPdfConfig(5, 1));
        when(componentBuilder.buildPDFTitle()).thenReturn(cmp.horizontalList());
        when(componentBuilder.buildPageHeaderDetailedReport(anyString(), anyString(), any(), anyString())).thenReturn(cmp.verticalList());
        when(componentBuilder.buildDetail(anyString(), anyMap())).thenReturn(cmp.verticalList());
        when(componentBuilder.buildPageFooter(PageOrientation.PORTRAIT)).thenReturn(cmp.verticalList());
        Mono<byte[]> report = actionsReportAdapter.generateDetailedReport(actions.get(0), AvailableFormat.XLSX, ContextCreator.createTestContext());
        StepVerifier.create(report)
                .expectNextMatches(bytes -> bytes.length > 0)
                .verifyComplete();
    }

    @Test
    void shouldUploadReportSuccessfully() {
        when(cloudFileGateway.put(any())).thenReturn(Mono.empty().then());
        StepVerifier.create(actionsReportAdapter.uploadReport(new byte[1], ContextCreator.createTestContext(),
                        AvailableFormat.XLSX))
                .expectNextMatches(filename -> filename
                        .contains("D2B_6161_CC_00000009043_fileName-") &&
                        filename.contains(".xlsx"))
                .verifyComplete();
    }

    @Test
    void shouldThrowTechnicalExceptionWhenUploadReportFail() {
        when(cloudFileGateway.put(any())).thenReturn(Mono.error(new RuntimeException()));
        StepVerifier.create(actionsReportAdapter.uploadReport(new byte[1], ContextCreator.createTestContext(),
                        AvailableFormat.XLSX))
                .expectErrorMatches(error ->
                        error.getClass().equals(TechnicalException.class) &&
                                error.getMessage().equals(TechnicalErrorMessage.SAVE_REPORT.getMessage()))
                .verify();
    }
}
