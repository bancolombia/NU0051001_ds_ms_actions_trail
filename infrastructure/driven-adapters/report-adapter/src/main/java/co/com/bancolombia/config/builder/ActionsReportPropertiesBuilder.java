package co.com.bancolombia.config.builder;

import co.com.bancolombia.config.ActionsReportProperties;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ActionsReportPropertiesBuilder {

    private static final String INVALID_FIELDS_FORMAT = "invalid fields format";

    public static ActionsReportProperties buildTestActionReportProperties() {
        return new ActionsReportProperties(
                new ActionsReportProperties.Images(
                        "BankLogoURL",
                        "SupervisedLogoURL",
                        "StrokeImageURL"
                ),
                new ActionsReportProperties.Header(
                        "REPORTE DE ACCIONES",
                        "HISTORIAL DE TRANSACCIÓN",
                        "DETALLE DE ACCIÓN",
                        "GALATEA - ENGAGEMENT SERVICES"
                ),
                new ActionsReportProperties.Fields(
                        "{\"basicOne\": \"básico 1\", \"basicTwo\": \"básico 2\"}",
                        "{\"detailedOne\": \"detallado 1\", \"detailedTwo\": \"detallado 2\"}",
                        "{\"historyOne\": \"historico 1\", \"historicTwo\": \"historico 2\"}"
                ),
                new ActionsReportProperties.Format("dd/MM/yyyy", "#.##################", "Página {0} de {1}"),
                new ActionsReportProperties.DetailedReport("Información básica","Detalle de la actividad"),
                new ActionsReportProperties.Filename(
                        "fileNameCriteria",
                        "fileNameHistory",
                        "fileNameDetailed",
                        "6161"
                ),
                "cloudTest"
        );

    }

    public static ActionsReportProperties buildInvalidTestActionReportProperties() {
        return new ActionsReportProperties(
                null,
                null,
                new ActionsReportProperties.Fields(
                        INVALID_FIELDS_FORMAT,
                        INVALID_FIELDS_FORMAT,
                        INVALID_FIELDS_FORMAT
                ),
                null,
                null,
                null,
                null
        );
    }

}
