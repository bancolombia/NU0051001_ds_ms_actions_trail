package co.com.bancolombia.api.openapi;

import co.com.bancolombia.api.parameter.dto.ProductDataResponseDTO;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.experimental.UtilityClass;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;

@UtilityClass
public class OpenAPI {

    private final String TAG_ACTION_SEARCH = "Actions search";
    private final String TAG_ACTION_REPORT = "Actions Report";
    private final String TAG_PRODUCTS = "Products search";
    private final String TAG_PARAMETER = "Parameter search";
    private final String OPERATION_ID_SEARCH_ACTIONS_BY_CRITERIA = "search-actions-by-criteria";
    private final String OPERATION_ID_SEARCH_ACTIONS_BY_TRACKER_ID = "search-action-by-tracker-id";
    private final String OPERATION_ID_REPORT_BY_SEARCH_CRITERIA = "generate-report-by-criteria";
    private final String OPERATION_ID_REPORT_HISTORY_BY_CODE = "generate-report-history-action-by-code";
    private final String OPERATION_ID_REPORT_DETAIL_BY_CODE = "generate-report-detail-action-by-code";
    private final String OPERATION_ID_PRODUCTS = "list-products";
    private final String OPERATION_ID_PARAMETERS = "lists-parameters";
    private final String TEMPLATE_PATH = "template";
    public final String SUCCESS = "Success";


    public void searchActions(Builder builder) {
        builder.operationId(OPERATION_ID_SEARCH_ACTIONS_BY_CRITERIA)
                .tag(TAG_ACTION_SEARCH)
                .description(OpenAPIDescriptions.DESCRIPTION_SEARCH_ACTIONS_BY_CRITERIA.getDescription());
    }

    public void getActionsByTrackerId(Builder builder) {
        builder.operationId(OPERATION_ID_SEARCH_ACTIONS_BY_TRACKER_ID)
                .tag(TAG_ACTION_SEARCH)
                .description(OpenAPIDescriptions.DESCRIPTION_SEARCH_ACTIONS_BY_TRACKER_ID.getDescription());
    }

    public void searchActionReport(Builder builder) {
        builder.operationId(OPERATION_ID_REPORT_BY_SEARCH_CRITERIA)
                .tag(TAG_ACTION_REPORT)
                .description(OpenAPIDescriptions.DESCRIPTION_REPORT_BY_SEARCH_CRITERIA.getDescription());
    }

    public void getActionHistoryByCode(Builder builder) {
        builder.operationId(OPERATION_ID_REPORT_HISTORY_BY_CODE)
                .tag(TAG_ACTION_REPORT)
                .description(OpenAPIDescriptions.DESCRIPTION_REPORT_HISTORY_BY_CODE.getDescription());
    }

    public void getActionDetailByCode(Builder builder) {
        builder.operationId(OPERATION_ID_REPORT_DETAIL_BY_CODE)
                .tag(TAG_ACTION_REPORT)
                .description(OpenAPIDescriptions.DESCRIPTION_REPORT_DETAIL_BY_CODE.getDescription());
    }

    public void getProducts(Builder builder) {
        builder.operationId(OPERATION_ID_PRODUCTS)
                .tag(TAG_PRODUCTS)
                .description(OpenAPIDescriptions.DESCRIPTION_PRODUCTS.getDescription());
    }

    public void getParameters(Builder builder) {
        builder.operationId(OPERATION_ID_PARAMETERS)
                .tag(TAG_PARAMETER)
                .description(OpenAPIDescriptions.DESCRIPTION_PARAMETERS.getDescription())
                .parameter(parameterBuilder().in(ParameterIn.PATH).name(TEMPLATE_PATH)
                        .description(OpenAPIDescriptions.DESCRIPTION_TEMPLATE_PATH.getDescription()).required(true))
                .response(responseBuilder().description(SUCCESS).responseCode(HttpStatus.OK.toString())
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder().implementation(ProductDataResponseDTO.class))));
    }
}