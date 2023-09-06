package co.com.bancolombia.event.search.builder;

import co.com.bancolombia.d2b.model.events.AbstractEventSpec;
import co.com.bancolombia.event.properties.EventNameProperties;
import co.com.bancolombia.event.properties.EventNameProperties.EventBusinessAction;
import co.com.bancolombia.event.properties.EventNameProperties.Suffix;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDetailedQueryDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDetailedQueryFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionQueryFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataDetailedReportGenerationDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataDetailedReportGenerationFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionHistoryGenerationDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionHistoryGenerationFailureDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDownloadDTO;
import co.com.bancolombia.event.search.dto.body.BodyDataActionDownloadFailureDTO;
import co.com.bancolombia.event.search.dto.commons.ErrorDTO;
import co.com.bancolombia.event.search.dto.commons.MetaDTO;
import co.com.bancolombia.event.search.dto.criteria.SearchCriteriaDTO;
import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.criteria.DateCriterion;
import co.com.bancolombia.model.search.criteria.TransactionIdCriterion;
import co.com.bancolombia.model.search.criteria.TransactionTrackerCriterion;
import context.ContextCreator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ActionsQueryEventSpecBuilderTest {

    private ActionsQueryEventSpecBuilder actionsQueryEventSpecBuilder;
    private ActionsReportEventSpecBuilder actionsReportEventSpecBuilder;

    @Mock
    private Suffix eventSuffix;
    private static Context testContext;
    private static final String EXPECTED_EVENTS_SPEC_VERSION = "1.0";


    @BeforeAll
    static void beforeAll() {
        testContext = ContextCreator.createTestContext();
    }

    @BeforeEach
    void setUp() {
        EventBusinessAction eventBusinessAction = new EventBusinessAction("action","report", eventSuffix);
        EventNameProperties eventNameProperties = new EventNameProperties("d2b", "distributionMicroservice",
                "business.action-trail", eventBusinessAction);
        EventSpecBuilder eventSpecBuilder = new EventSpecBuilder("ds-ms-actions-trail", eventNameProperties);
        this.actionsQueryEventSpecBuilder = new ActionsQueryEventSpecBuilder(eventSpecBuilder, eventNameProperties);
        this.actionsReportEventSpecBuilder = new ActionsReportEventSpecBuilder(eventSpecBuilder, eventNameProperties);
    }

    @Test
    void shouldBuildSuccessEventSpec() {
        Mockito.when(eventSuffix.getActionHistoryQueryDone()).thenReturn("actionHistoryQueryDone");
        String transactionTrackerId = "9640";
        var searchCriterion = new TransactionTrackerCriterion(transactionTrackerId);
        AbstractEventSpec<BodyDataActionQueryDTO> successSpec = actionsQueryEventSpecBuilder
                .buildSuccessSpec(testContext, searchCriterion);
        String searchCriterionCode = searchCriterion.getCode();
        String searchValue = searchCriterion.getSearchValue().getValue();

        var expectedEventData = new BodyDataActionQueryDTO(
                new MetaDTO(testContext, "200", "OK", "No Monetaria", "Exitosa"),
                new BodyDataActionQueryDTO.Request(new SearchCriteriaDTO(Map.of(searchCriterionCode, searchValue))),
                new BodyDataActionQueryDTO.Response("Actions History Query Generated Successfully"));

        assertThatEventStructureIsOk(successSpec);
        assertThat(successSpec.getType())
                .isEqualTo("business.action-trail.action.d2b.distributionMicroservice.actionHistoryQueryDone");

        assertThatEventMetaDataIsEqualIgnoringResponseTimestamp(expectedEventData.getMeta(),
                successSpec.getData().getMeta());
        assertThat(expectedEventData.getRequest()).isEqualTo(successSpec.getData().getRequest());
        assertThat(expectedEventData.getResponse()).isEqualTo(successSpec.getData().getResponse());
    }

    @Test
    void shouldBuildFailureEventSpec() {
        Mockito.when(eventSuffix.getActionHistoryQueryRejected()).thenReturn("actionHistoryQueryRejected");
        String transactionTrackerId = "9640";
        var searchCriterion = new TransactionTrackerCriterion(transactionTrackerId);
        var unexpectedException = new RuntimeException("Fatal Error");
        AbstractEventSpec<BodyDataActionQueryFailureDTO> failureSpec =
                actionsQueryEventSpecBuilder.buildFailureSpec(testContext, searchCriterion, unexpectedException);

        String searchCriterionCode = searchCriterion.getCode();
        String searchValue = searchCriterion.getSearchValue().getValue();
        var errorDTO = new ErrorDTO(unexpectedException, testContext.getDomain());
        var expectedEventData = new BodyDataActionQueryFailureDTO(
                new MetaDTO(testContext, "500",
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "No Monetaria", "Fallida"),
                new BodyDataActionQueryFailureDTO.Request(new SearchCriteriaDTO(Map.of(searchCriterionCode, searchValue))),
                new BodyDataActionQueryFailureDTO.Response(errorDTO));


        assertThatEventStructureIsOk(failureSpec);
        assertThat(failureSpec.getType())
                .isEqualTo("business.action-trail.action.d2b.distributionMicroservice.actionHistoryQueryRejected");
        assertThatEventMetaDataIsEqualIgnoringResponseTimestamp(expectedEventData.getMeta(),
                failureSpec.getData().getMeta());
        assertThat(expectedEventData.getRequest()).isEqualTo(failureSpec.getData().getRequest());
        assertThat(expectedEventData.getResponse()).isEqualTo(failureSpec.getData().getResponse());

    }

    @Test
    void shouldBuildSuccessActionDetailedQueryEventSpec() {
        Mockito.when(eventSuffix.getActionSearchDone()).thenReturn("actionSearchDone");
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("test");
        var expectedEventData = new BodyDataActionDetailedQueryDTO(
                new MetaDTO(testContext, "200", "OK", "No Monetaria", "Exitosa"),
                new BodyDataActionDetailedQueryDTO.Request(transactionIdCriterion.getSearchValue().getValue()),
                new BodyDataActionDetailedQueryDTO.Response("Action detailed Query Generated Successfully"));
        AbstractEventSpec<BodyDataActionDetailedQueryDTO> successSpec =
                actionsQueryEventSpecBuilder.buildSuccessSpec(testContext, transactionIdCriterion);

        assertThatEventStructureIsOk(successSpec);
        assertThat(successSpec.getType())
                .isEqualTo("business.action-trail.action.d2b.distributionMicroservice.actionSearchDone");

        assertThat(expectedEventData.getMeta()).usingRecursiveComparison()
                .ignoringFields("responseTimestamp").isEqualTo(successSpec.getData().getMeta());
        assertThat(expectedEventData.getRequest()).isEqualTo(successSpec.getData().getRequest());
        assertThat(expectedEventData.getResponse()).isEqualTo(successSpec.getData().getResponse());

    }

    @Test
    void shouldBuildSuccessDetailedReportGenerationEventSpec() {
        Mockito.when(eventSuffix.getActionDetailedReportDone()).thenReturn("actionDetailedReportDone");
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("test");
        AvailableFormat availableFormat = AvailableFormat.PDF;
        String format = availableFormat.getFormat();

        var expectedEventData = new BodyDataDetailedReportGenerationDTO(
                new MetaDTO(testContext, "200", "OK", "No Monetaria", "Exitosa"),
                new BodyDataDetailedReportGenerationDTO.Request(transactionIdCriterion.getSearchValue().getValue(),
                        format),
                new BodyDataDetailedReportGenerationDTO.Response("Detailed Report Generated Successfully"));
        AbstractEventSpec<BodyDataDetailedReportGenerationDTO> successSpec = actionsReportEventSpecBuilder
                .buildSuccessSpec(testContext, transactionIdCriterion, availableFormat);

        assertThatEventStructureIsOk(successSpec);
        assertThat(successSpec.getType())
                .isEqualTo("business.action-trail.report.d2b.distributionMicroservice.actionDetailedReportDone");
        assertThatEventMetaDataIsEqualIgnoringResponseTimestamp(expectedEventData.getMeta(),
                successSpec.getData().getMeta());
        assertThat(expectedEventData.getRequest()).isEqualTo(successSpec.getData().getRequest());
        assertThat(expectedEventData.getResponse()).isEqualTo(successSpec.getData().getResponse());

    }

    @Test
    void shouldBuildSuccessActionHistoryGenerationEventSpec() {
        Mockito.when(eventSuffix.getActionHistoryDownloadDone()).thenReturn("actionHistoryDownloadDone");
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion("test");
        AvailableFormat availableFormat = AvailableFormat.XLSX;
        String format = availableFormat.getFormat();

        var expectedEventData = new BodyDataActionHistoryGenerationDTO(
                new MetaDTO(testContext, "200", "OK",
                        "No Monetaria", "Exitosa"),
                new BodyDataActionHistoryGenerationDTO.Request(transactionTrackerCriterion.getSearchValue().getValue(),
                        format),
                new BodyDataActionHistoryGenerationDTO.Response("Actions History Report Generated Successfully"));
        AbstractEventSpec<BodyDataActionHistoryGenerationDTO> successSpec = actionsReportEventSpecBuilder
                .buildSuccessSpec(testContext, transactionTrackerCriterion, availableFormat);

        assertThatEventStructureIsOk(successSpec);
        assertThat(successSpec.getType())
                .isEqualTo("business.action-trail.report.d2b.distributionMicroservice.actionHistoryDownloadDone");
        assertThatEventMetaDataIsEqualIgnoringResponseTimestamp(expectedEventData.getMeta(),
                successSpec.getData().getMeta());
        assertThat(expectedEventData.getRequest()).isEqualTo(successSpec.getData().getRequest());
        assertThat(expectedEventData.getResponse()).isEqualTo(successSpec.getData().getResponse());

    }
    @Test
    void shouldBuildSuccessActionGenerationEventSpec() {
        Mockito.when(eventSuffix.getActionDownloadDone()).thenReturn("actionDownloadDone");
        var searchCriteria = new HashSet<AnySearchCriterion<?>>();
        searchCriteria.add(new DateCriterion(LocalDate.of(2022, 11, 17), LocalDate.of(2022, 12, 17)));
        var availableFormat = AvailableFormat.XLSX;
        var filename = "D2B_8f86702e-f66b-4f76-9d2d-6f7b0650734d_CC_00000009043_consultaDeActividades_08-03-2023-05-54-33.xlsx";

        var expectedEventData = new BodyDataActionDownloadDTO(
                new MetaDTO(testContext, "200", "OK",
                        "No Monetaria", "Exitosa"),
                new BodyDataActionDownloadDTO.Request(availableFormat.getFormat(), searchCriteria),
                new BodyDataActionDownloadDTO.Response("Report Generated Successfully", filename));
        AbstractEventSpec<BodyDataActionDownloadDTO> successSpec = actionsReportEventSpecBuilder
                .buildSuccessSpec(testContext, searchCriteria,filename,availableFormat);

        assertThatEventStructureIsOk(successSpec);
        assertThat(successSpec.getType())
                .isEqualTo("business.action-trail.report.d2b.distributionMicroservice.actionDownloadDone");
        assertThatEventMetaDataIsEqualIgnoringResponseTimestamp(expectedEventData.getMeta(),
                successSpec.getData().getMeta());
        assertThat(expectedEventData.getRequest().getFormat()).isEqualTo(successSpec.getData().getRequest().getFormat());
        assertThat(expectedEventData.getRequest().getSearchCriteria()).isEqualTo(successSpec.getData().getRequest().getSearchCriteria());
        assertThat(expectedEventData.getResponse().getState()).isEqualTo(successSpec.getData().getResponse().getState());
        assertThat(expectedEventData.getResponse().getFilename()).isEqualTo(successSpec.getData().getResponse().getFilename());
    }

    @Test
    void shouldBuildFailureActionDetailedQueryEventSpec() {
        Mockito.when(eventSuffix.getActionSearchRejected()).thenReturn("actionSearchRejected");
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("test");
        var unexpectedException = new RuntimeException("Fatal Error");
        var errorDTO = new ErrorDTO(unexpectedException, testContext.getDomain());
        var expectedEventData = new BodyDataActionDetailedQueryFailureDTO(
                new MetaDTO(testContext, "500",
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "No Monetaria", "Fallida"),
                new BodyDataActionDetailedQueryFailureDTO.Request(transactionIdCriterion.getSearchValue().getValue()),
                new BodyDataActionDetailedQueryFailureDTO.Response(errorDTO)
        );
        AbstractEventSpec<BodyDataActionDetailedQueryFailureDTO> failureSpec =
                actionsQueryEventSpecBuilder.buildFailureSpec(testContext, transactionIdCriterion, unexpectedException);

        assertThatEventStructureIsOk(failureSpec);
        assertThat(failureSpec.getType())
                .isEqualTo("business.action-trail.action.d2b.distributionMicroservice.actionSearchRejected");
        assertThatEventMetaDataIsEqualIgnoringResponseTimestamp(expectedEventData.getMeta(),
                failureSpec.getData().getMeta());
        assertThat(expectedEventData.getRequest()).isEqualTo(failureSpec.getData().getRequest());
        assertThat(expectedEventData.getResponse()).isEqualTo(failureSpec.getData().getResponse());

    }

    @Test
    void shouldBuildFailureDetailedReportGenerationEventSpec() {
        Mockito.when(eventSuffix.getActionDetailedReportRejected()).thenReturn("actionDetailedReportRejected");
        TransactionIdCriterion transactionIdCriterion = new TransactionIdCriterion("test");
        AvailableFormat availableFormat = AvailableFormat.PDF;
        String format = availableFormat.getFormat();
        var unexpectedException = new RuntimeException("Fatal Error");

        AbstractEventSpec<BodyDataDetailedReportGenerationFailureDTO> failureSpec =
                actionsReportEventSpecBuilder.buildFailureSpec(testContext, transactionIdCriterion, availableFormat,
                        unexpectedException);
        var errorDTO = new ErrorDTO(unexpectedException, testContext.getDomain());
        var expectedEventData = new BodyDataDetailedReportGenerationFailureDTO(
                new MetaDTO(testContext, "500",
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "No Monetaria", "Fallida"),
                new BodyDataDetailedReportGenerationFailureDTO.Request(transactionIdCriterion.getSearchValue()
                        .getValue(), format),
                new BodyDataDetailedReportGenerationFailureDTO.Response(errorDTO)
        );

        assertThatEventStructureIsOk(failureSpec);
        assertThat(failureSpec.getType())
                .isEqualTo("business.action-trail.report.d2b.distributionMicroservice.actionDetailedReportRejected");
        assertThatEventMetaDataIsEqualIgnoringResponseTimestamp(expectedEventData.getMeta(),
                failureSpec.getData().getMeta());
        assertThat(expectedEventData.getRequest()).isEqualTo(failureSpec.getData().getRequest());
        assertThat(expectedEventData.getResponse()).isEqualTo(failureSpec.getData().getResponse());

    }

    @Test
    void shouldBuildFailureActionHistoryGenerationEventSpec() {
        Mockito.when(eventSuffix.getActionHistoryDownloadRejected()).thenReturn("actionHistoryDownloadRejected");
        TransactionTrackerCriterion transactionTrackerCriterion = new TransactionTrackerCriterion("test");
        AvailableFormat availableFormat = AvailableFormat.XLSX;
        String format = availableFormat.getFormat();
        var unexpectedException = new RuntimeException("Fatal Error");

        AbstractEventSpec<BodyDataActionHistoryGenerationFailureDTO> failureSpec =
                actionsReportEventSpecBuilder.buildFailureSpec(testContext, transactionTrackerCriterion, availableFormat,
                        unexpectedException);
        var errorDTO = new ErrorDTO(unexpectedException, testContext.getDomain());
        var expectedEventData = new BodyDataActionHistoryGenerationFailureDTO(
                new MetaDTO(testContext, "500",
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "No Monetaria", "Fallida"),
                new BodyDataActionHistoryGenerationFailureDTO.Request(transactionTrackerCriterion.getSearchValue()
                        .getValue(), format),
                new BodyDataActionHistoryGenerationFailureDTO.Response(errorDTO)
        );

        assertThatEventStructureIsOk(failureSpec);
        assertThat(failureSpec.getType())
                .isEqualTo("business.action-trail.report.d2b.distributionMicroservice.actionHistoryDownloadRejected");
        assertThatEventMetaDataIsEqualIgnoringResponseTimestamp(expectedEventData.getMeta(),
                failureSpec.getData().getMeta());
        assertThat(expectedEventData.getRequest()).isEqualTo(failureSpec.getData().getRequest());
        assertThat(expectedEventData.getResponse()).isEqualTo(failureSpec.getData().getResponse());

    }
    @Test
    void shouldBuildFailureActionGenerationEventSpec() {
        Mockito.when(eventSuffix.getActionDownloadRejected()).thenReturn("actionHistoryDownloadRejected");
        var searchCriteria = new HashSet<AnySearchCriterion<?>>();
        searchCriteria.add(new DateCriterion(LocalDate.of(2022, 11, 17), LocalDate.of(2022, 12, 17)));
        AvailableFormat availableFormat = AvailableFormat.XLSX;
        var unexpectedException = new RuntimeException("Fatal Error");

        AbstractEventSpec<BodyDataActionDownloadFailureDTO> failureSpec =
                actionsReportEventSpecBuilder.buildFailureSpec(testContext, searchCriteria, availableFormat,
                        unexpectedException);
        var errorDTO = new ErrorDTO(unexpectedException, testContext.getDomain());
        var expectedEventData = new BodyDataActionDownloadFailureDTO(
                new MetaDTO(testContext, "500",
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "No Monetaria", "Fallida"),
                new BodyDataActionDownloadFailureDTO.Request(availableFormat.getFormat(), searchCriteria),
                new BodyDataActionDownloadFailureDTO.Response(errorDTO)
        );

        assertThatEventStructureIsOk(failureSpec);
        assertThat(failureSpec.getType())
                .isEqualTo("business.action-trail.report.d2b.distributionMicroservice.actionHistoryDownloadRejected");
        assertThatEventMetaDataIsEqualIgnoringResponseTimestamp(expectedEventData.getMeta(),
                failureSpec.getData().getMeta());
        assertThat(expectedEventData.getRequest().getFormat()).isEqualTo(failureSpec.getData().getRequest().getFormat());
        assertThat(expectedEventData.getRequest().getSearchCriteria()).isEqualTo(failureSpec.getData().getRequest().getSearchCriteria());
        assertThat(expectedEventData.getResponse()).isEqualTo(failureSpec.getData().getResponse());

    }

    private void assertThatEventStructureIsOk(AbstractEventSpec<?> eventSpec) {
        assertThat(eventSpec.getSpecVersion()).isEqualTo(EXPECTED_EVENTS_SPEC_VERSION);
        assertThat(eventSpec.getSource()).isEqualTo("ds-ms-actions-trail");
        assertThat(eventSpec.getId()).isEqualTo(testContext.getId());
        assertThat(eventSpec.getTime()).isEqualTo(testContext.getRequestDate());
        assertThat(eventSpec.getDataContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }

    private void assertThatEventMetaDataIsEqualIgnoringResponseTimestamp(MetaDTO expected, MetaDTO actual) {
        assertThat(expected).usingRecursiveComparison().ignoringFields("responseTimestamp").isEqualTo(actual);
    }

}
