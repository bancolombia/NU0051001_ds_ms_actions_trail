package co.com.bancolombia;

import action.ActionCreator;
import co.com.bancolombia.config.builder.ActionsReportPropertiesBuilder;
import co.com.bancolombia.config.builder.ReportImageLoaderBuilder;
import co.com.bancolombia.config.format.ActionsReportFormatConfig;
import co.com.bancolombia.config.format.ActionsReportPdfConfig;
import co.com.bancolombia.model.actions.Action;
import net.sf.dynamicreports.report.base.component.DRImage;
import net.sf.dynamicreports.report.base.component.DRLine;
import net.sf.dynamicreports.report.base.component.DRList;
import net.sf.dynamicreports.report.base.component.DRPageXofY;
import net.sf.dynamicreports.report.base.component.DRTextField;
import net.sf.dynamicreports.report.base.style.DRStyle;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.expression.ValueExpression;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class ComponentBuilderTest {

    private final ComponentBuilder componentBuilder = new ComponentBuilder(
            ActionsReportPropertiesBuilder.buildTestActionReportProperties(),
            ReportImageLoaderBuilder.buildReportImageLoader());
    private static final String TEST_LABEL = "label";
    private static final String TEST_VALUE = "value";
    private static final String TEST_TITLE = "title";
    private static final String TEST_SUBTITLE = "subtitle";
    private static final String TEST_CHANNEL = "channel";
    private static final String TEST_IP = "127.0.0.1";
    private static final String CUSTOMER_NAME_FIELD = "customerName";
    private static final String DOCUMENT_TYPE_FIELD = "documentType";
    private static final String DOCUMENT_NUMBER_FIELD = "documentNumber";
    private static final String CUSTOMER_NAME_COLUMN_TITLE = "Customer Name";
    private static final String CUSTOMER_ID_TYPE_COLUMN_TITLE = "Customer ID Type";
    private static final String CUSTOMER_ID_NUMBER_COLUMN_TITLE = "Customer ID Number";
    private static final Color SECONDARY_FONT_COLOR = new java.awt.Color(0x454648);
    private final Map<String, String> informationFields = new LinkedHashMap<>();
    private ActionsReportFormatConfig configPDF;
    private List<Action> actions;

    @BeforeEach
    void setUp() {
        Action action = ActionCreator.createTestAction();
        actions = List.of(
                action,
                action
        );
        configPDF = new ActionsReportPdfConfig(10, 10);
        informationFields.put(CUSTOMER_NAME_FIELD, CUSTOMER_NAME_COLUMN_TITLE);
        informationFields.put(DOCUMENT_TYPE_FIELD, CUSTOMER_ID_TYPE_COLUMN_TITLE);
        informationFields.put(DOCUMENT_NUMBER_FIELD, CUSTOMER_ID_NUMBER_COLUMN_TITLE);
    }

    @Test
    void shouldBuildFieldSuccessfully() {
        VerticalListBuilder field = componentBuilder.buildField(TEST_LABEL, TEST_VALUE);
        DRTextField<String> labelField = (DRTextField) field.getList().getListCells().get(0).getComponent();
        DRTextField<String> valueField = (DRTextField) field.getList().getListCells().get(1).getComponent();

        Assertions.assertEquals("FIXED", field.getComponent().getWidthType().name());
        Assertions.assertEquals(TEST_LABEL, ((ValueExpression<String>) labelField.getValueExpression()).evaluate(null));
        Assertions.assertEquals(TEST_VALUE, ((ValueExpression<String>) valueField.getValueExpression()).evaluate(null));
        Assertions.assertEquals(SECONDARY_FONT_COLOR, ((DRStyle) valueField.getStyle()).getForegroundColor());
    }

    @Test
    void shouldBuildActionCardSuccessfully() {
        var fieldsMap = new TreeMap<String, String>();
        fieldsMap.put(TEST_LABEL + "1", TEST_VALUE + "1");
        fieldsMap.put(TEST_LABEL + "2", TEST_VALUE + "2");
        fieldsMap.put(TEST_LABEL + "3", TEST_VALUE + "3");
        fieldsMap.put(TEST_LABEL + "4", TEST_VALUE + "4");

        var verticalListBuilder = componentBuilder.buildActionCard(fieldsMap);

        var firstHorizontalList = ((DRList) verticalListBuilder.getComponent().getListCells().get(0).getComponent());
        var firstVerticalList = ((DRList) firstHorizontalList.getListCells().get(0).getComponent());
        var firstField = ((DRList) firstVerticalList.getListCells().get(0).getComponent());
        DRTextField<String> firstLabelField = (DRTextField) firstField.getListCells().get(0).getComponent();
        DRTextField<String> firstValueField = (DRTextField) firstField.getListCells().get(1).getComponent();

        var lastHorizontalList = ((DRList) verticalListBuilder.getComponent().getListCells().get(1).getComponent());
        var lastVerticalList = ((DRList) lastHorizontalList.getListCells().get(0).getComponent());
        var lastField = ((DRList) lastVerticalList.getListCells().get(0).getComponent());
        DRTextField<String> lastLabelField = (DRTextField) lastField.getListCells().get(0).getComponent();
        DRTextField<String> lastValueField = (DRTextField) lastField.getListCells().get(1).getComponent();

        Assertions.assertEquals(TEST_LABEL + "1", ((ValueExpression<String>) firstLabelField.getValueExpression()).evaluate(null));
        Assertions.assertEquals(TEST_VALUE + "1", ((ValueExpression<String>) firstValueField.getValueExpression()).evaluate(null));
        Assertions.assertEquals(TEST_LABEL + "4", ((ValueExpression<String>) lastLabelField.getValueExpression()).evaluate(null));
        Assertions.assertEquals(TEST_VALUE + "4", ((ValueExpression<String>) lastValueField.getValueExpression()).evaluate(null));
    }

    @Test
    void shouldBuildDetailSuccessfully() {
        var fieldsMap = new TreeMap<String, String>();
        fieldsMap.put(TEST_LABEL, TEST_VALUE);

        var verticalListBuilder = componentBuilder.buildDetail(TEST_SUBTITLE, fieldsMap);
        DRTextField<String> subtitle = (DRTextField) verticalListBuilder.getComponent().getListCells().get(0).getComponent();

        Assertions.assertEquals(TEST_SUBTITLE, ((ValueExpression<?>) subtitle.getValueExpression()).evaluate(null));
        Assertions.assertEquals(ActionsReportPdfConfig.SUBTITLE_FONT_COLOR, ((DRStyle) subtitle.getStyle()).getForegroundColor());
        Assertions.assertEquals(ActionsReportPdfConfig.SUBTITLE_FONT_SIZE, ((DRStyle) subtitle.getStyle()).getFont().getFontSize());
    }

    @Test
    void shouldBuildPDFTitleSuccessfully() {
        int expectedImageHeight = 60;
        int expectedImageWidth = 250;

        var horizontalListBuilder = componentBuilder.buildPDFTitle();

        var row = (DRList) horizontalListBuilder.getList().getListCells().get(0).getComponent();
        var bankLogoImage = (DRImage) row.getListCells().get(0).getComponent();
        var strokeImage = (DRImage) row.getListCells().get(1).getComponent();

        Assertions.assertEquals(expectedImageHeight, bankLogoImage.getHeight());
        Assertions.assertEquals(expectedImageWidth, strokeImage.getWidth());
        Assertions.assertEquals(HorizontalImageAlignment.RIGHT, strokeImage.getHorizontalImageAlignment());
    }

    @Test
    void shouldBuildXLSXTitleSuccessfully() {
        var cmpBuilder = componentBuilder.buildXLSXTitle(TEST_TITLE);
        DRTextField<String> titleComponent = (DRTextField) cmpBuilder.getComponent();

        Assertions.assertEquals(TEST_TITLE, ((ValueExpression<?>) titleComponent.getValueExpression()).evaluate(null));
    }

    @Test
    void shouldBuildPageHeaderDetailedReportSuccessfully() {
        Action action = ActionCreator.createTestAction();

        String expectedCustomer = action.get(CUSTOMER_NAME_FIELD).toString();
        String expectedDocument = action.get(DOCUMENT_TYPE_FIELD).toString() + ": " + action.get(DOCUMENT_NUMBER_FIELD).toString();
        String expectedIPText = "Dirección IP " + TEST_IP;

        var verticalListBuilder = componentBuilder.buildPageHeaderDetailedReport(TEST_TITLE, TEST_CHANNEL, action, TEST_IP);

        var horizontalListBuilder = (DRList) verticalListBuilder.getComponent().getListCells().get(0).getComponent();
        var row = (DRList) horizontalListBuilder.getListCells().get(0).getComponent();
        var firstVerticalList = (DRList) row.getListCells().get(0).getComponent();
        var secondVerticalList = (DRList) row.getListCells().get(1).getComponent();
        DRTextField<String> channel = (DRTextField) firstVerticalList.getListCells().get(0).getComponent();
        DRTextField<String> title = (DRTextField) firstVerticalList.getListCells().get(2).getComponent();
        DRTextField<String> ipText = (DRTextField) secondVerticalList.getListCells().get(1).getComponent();
        DRTextField<String> customer = (DRTextField) secondVerticalList.getListCells().get(2).getComponent();
        DRTextField<String> documentAndNumber = (DRTextField) secondVerticalList.getListCells().get(3).getComponent();

        Assertions.assertEquals(TEST_CHANNEL, ((ValueExpression<?>) channel.getValueExpression()).evaluate(null));
        Assertions.assertEquals(TEST_TITLE, ((ValueExpression<?>) title.getValueExpression()).evaluate(null));
        Assertions.assertEquals(expectedCustomer, ((ValueExpression<?>) customer.getValueExpression()).evaluate(null));
        Assertions.assertEquals(expectedDocument, ((ValueExpression<?>) documentAndNumber.getValueExpression()).evaluate(null));
        Assertions.assertEquals(expectedIPText, ((ValueExpression<?>) ipText.getValueExpression()).evaluate(null));
    }

    @Test
    void shouldBuildPageHeaderHistoryReportSuccessfully() {
        Action action = ActionCreator.createTestAction();

        String expectedCustomer = action.get(CUSTOMER_NAME_FIELD).toString();
        String expectedDocument = action.get(DOCUMENT_TYPE_FIELD).toString() + ": " + action.get(DOCUMENT_NUMBER_FIELD).toString();
        String expectedIPText = "Dirección IP " + TEST_IP;

        var verticalListBuilder = componentBuilder.buildPageHeaderHistoryReport(TEST_TITLE, TEST_CHANNEL, action, TEST_IP);

        var horizontalListBuilder = (DRList) verticalListBuilder.getComponent().getListCells().get(0).getComponent();
        var row = (DRList) horizontalListBuilder.getListCells().get(0).getComponent();
        var firstVerticalList = (DRList) row.getListCells().get(0).getComponent();
        var secondVerticalList = (DRList) row.getListCells().get(1).getComponent();
        var thirdVerticalList = (DRList) row.getListCells().get(2).getComponent();
        DRTextField<String> channel = (DRTextField) firstVerticalList.getListCells().get(1).getComponent();
        DRTextField<String> title = (DRTextField) secondVerticalList.getListCells().get(1).getComponent();
        DRTextField<String> ipText = (DRTextField) thirdVerticalList.getListCells().get(1).getComponent();
        DRTextField<String> customer = (DRTextField) thirdVerticalList.getListCells().get(2).getComponent();
        DRTextField<String> documentAndNumber = (DRTextField) thirdVerticalList.getListCells().get(3).getComponent();

        Assertions.assertEquals(TEST_CHANNEL, ((ValueExpression<?>) channel.getValueExpression()).evaluate(null));
        Assertions.assertEquals(TEST_TITLE, ((ValueExpression<?>) title.getValueExpression()).evaluate(null));
        Assertions.assertEquals(expectedCustomer, ((ValueExpression<?>) customer.getValueExpression()).evaluate(null));
        Assertions.assertEquals(expectedDocument, ((ValueExpression<?>) documentAndNumber.getValueExpression()).evaluate(null));
        Assertions.assertEquals(expectedIPText, ((ValueExpression<?>) ipText.getValueExpression()).evaluate(null));
    }

    @Test
    void shouldBuildLandScapePageFooterSuccessfully() {
        int expectedLineWidth = PageType.LETTER.getHeight() - 40;
        int expectedImageHeight = 15;

        var verticalListBuilder = componentBuilder.buildPageFooter(PageOrientation.LANDSCAPE);
        var drline = (DRLine) verticalListBuilder.getComponent().getListCells().get(1).getComponent();
        var horizontalListBuilder = (DRList) verticalListBuilder.getComponent().getListCells().get(3).getComponent();
        var drList = (DRList) horizontalListBuilder.getListCells().get(0).getComponent();
        var image = (DRImage) drList.getListCells().get(0).getComponent();
        var index = (DRPageXofY) verticalListBuilder.getComponent().getListCells().get(4).getComponent();

        Assertions.assertEquals(expectedLineWidth, drline.getWidth());
        Assertions.assertEquals(expectedImageHeight, image.getHeight());
        Assertions.assertEquals(HorizontalTextAlignment.CENTER, index.getHorizontalTextAlignment());
    }

    @Test
    void shouldBuildPortraitPageFooterSuccessfully() {
        int expectedLineWidth = PageType.LETTER.getWidth() - 40;
        int expectedImageHeight = 15;

        var verticalListBuilder = componentBuilder.buildPageFooter(PageOrientation.PORTRAIT);
        var drline = (DRLine) verticalListBuilder.getComponent().getListCells().get(1).getComponent();
        var horizontalListBuilder = (DRList) verticalListBuilder.getComponent().getListCells().get(3).getComponent();
        var drList = (DRList) horizontalListBuilder.getListCells().get(0).getComponent();
        var image = (DRImage) drList.getListCells().get(0).getComponent();
        var index = (DRPageXofY) verticalListBuilder.getComponent().getListCells().get(4).getComponent();

        Assertions.assertEquals(expectedLineWidth, drline.getWidth());
        Assertions.assertEquals(expectedImageHeight, image.getHeight());
        Assertions.assertEquals(HorizontalTextAlignment.CENTER, index.getHorizontalTextAlignment());
    }

    @Test
    void shouldGetColumnsSuccessfully() {
        ActionsReportFormatConfig configPDFMock = spy(configPDF);

        when(configPDFMock.getStyleColumn()).thenReturn(stl.style());
        when(configPDFMock.getStyleColumnTitle()).thenReturn(stl.style());

        var columnBuilder = componentBuilder.getColumns(actions, configPDF, informationFields);
        var firstTextColumnBuilder = (TextColumnBuilder) columnBuilder[0];
        var firstTitleExpression = (ValueExpression) firstTextColumnBuilder.getColumn().getTitleExpression();
        var lastTextColumnBuilder = (TextColumnBuilder) columnBuilder[2];
        var lastTitleExpression = (ValueExpression) lastTextColumnBuilder.getColumn().getTitleExpression();

        Assertions.assertEquals(CUSTOMER_NAME_COLUMN_TITLE, firstTitleExpression.evaluate(null));
        Assertions.assertEquals(CUSTOMER_ID_NUMBER_COLUMN_TITLE, lastTitleExpression.evaluate(null));
    }

    @Test
    void shouldGetFieldsSuccessfully() {
        var action = actions.get(0);
        var fieldsMap = componentBuilder.getFields(informationFields, action);

        Assertions.assertEquals(action.get(CUSTOMER_NAME_FIELD), fieldsMap.get(CUSTOMER_NAME_COLUMN_TITLE));
        Assertions.assertEquals(action.get(DOCUMENT_TYPE_FIELD), fieldsMap.get(CUSTOMER_ID_TYPE_COLUMN_TITLE));
        Assertions.assertEquals(action.get(DOCUMENT_NUMBER_FIELD), fieldsMap.get(CUSTOMER_ID_NUMBER_COLUMN_TITLE));
    }

    @Test
    void shouldApplyFormatSuccessfully() {
        String expectedDoubleResult = "40000000000.22";
        String expectedDateResult = "30/01/2023";

        double doubleNumber = 4.000000000022E10;
        String dateTest = "2023-01-30T10:00:00";
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTest, formateador);


        var formattedDouble = componentBuilder.applyFormat(doubleNumber);
        var formattedDate = componentBuilder.applyFormat(localDateTime);

        Assertions.assertEquals(expectedDoubleResult, formattedDouble);
        Assertions.assertEquals(expectedDateResult, formattedDate);
    }

}