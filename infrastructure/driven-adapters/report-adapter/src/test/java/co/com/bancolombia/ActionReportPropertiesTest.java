package co.com.bancolombia;


import co.com.bancolombia.config.ActionsReportProperties;
import co.com.bancolombia.config.builder.ActionsReportPropertiesBuilder;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(ActionsReportProperties.class)
@TestPropertySource(properties = {
        "report.images.bankLogo=BankLogoURL",
        "report.images.supervisedLogo=SupervisedLogoURL",
        "report.images.strokeImage=StrokeImageURL",
        "report.header.reportByCriteriaTitle=REPORTE DE ACCIONES",
        "report.header.reportHistoryTitle=HISTORIAL DE TRANSACCIÓN",
        "report.header.reportDetailedTitle=DETALLE DE ACCIÓN",
        "report.header.personalizedChannelName=GALATEA - ENGAGEMENT SERVICES",
        "report.detailed-report.basicReportSubtitle=Información básica",
        "report.detailed-report.detailedReportSubtitle=Detalle de la actividad",
        "report.format.date=dd/MM/yyyy",
        "report.format.number=#.##################",
        "report.format.pageIndex=Página {0} de {1}",
        "report.filename.byCriteria=fileNameCriteria",
        "report.filename.history=fileNameHistory",
        "report.filename.detailed=fileNameDetailed",
        "report.filename.secureMailBoxTransactionCode=6161",
        "report.cloudName=cloudTest",
        "report.fields.basicInformation: {\"basicOne\": \"básico 1\", \"basicTwo\": \"básico 2\"}",
        "report.fields.detailedInformation: {\"detailedOne\": \"detallado 1\", \"detailedTwo\": \"detallado 2\"}",
        "report.fields.historyInformation: {\"historyOne\": \"historico 1\", \"historicTwo\": \"historico 2\"}"
})
class ActionReportPropertiesTest {
    @Autowired
    private ActionsReportProperties actionsReportProperties;

    private ActionsReportProperties invalidTestActionReportProperties;

    @BeforeEach
    void setUp(){
        invalidTestActionReportProperties = ActionsReportPropertiesBuilder
                .buildInvalidTestActionReportProperties();
    }
    @Test
    void shouldGetParametersOfActionsReportPropertiesSuccessfully(){
        assertThat(actionsReportProperties).usingRecursiveComparison()
                .ignoringFields("fields.objectMapper")
                .isEqualTo(ActionsReportPropertiesBuilder.buildTestActionReportProperties());
    }
    @Test
    void shouldThrowTechnicalExceptionWhenBasicInformationFieldsHaveInvalidFormat(){

        var reportFields = invalidTestActionReportProperties.getFields();
        var exception = assertThrows(TechnicalException.class, reportFields::getBasicInformation);
        assertEquals(TechnicalErrorMessage.INVALID_REPORT_FIELD_FORMAT, exception.getTechnicalErrorMessage());
    }
    @Test
    void shouldThrowTechnicalExceptionWhenDetailedInformationFieldsHaveInvalidFormat(){
        var reportFields = invalidTestActionReportProperties.getFields();
        var exception = assertThrows(TechnicalException.class, reportFields::getDetailedInformation);
        assertEquals(TechnicalErrorMessage.INVALID_REPORT_FIELD_FORMAT, exception.getTechnicalErrorMessage());
    }
    @Test
    void shouldThrowTechnicalExceptionWhenHistoryInformationFieldsHaveInvalidFormat(){
        var reportFields = invalidTestActionReportProperties.getFields();
        var exception = assertThrows(TechnicalException.class, reportFields::getHistoryInformation);
        assertEquals(TechnicalErrorMessage.INVALID_REPORT_FIELD_FORMAT, exception.getTechnicalErrorMessage());
    }
}
