package co.com.bancolombia.api.openapi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OpenAPIDescriptions {

    DESCRIPTION_TEMPLATE_PATH("Name of the template that contains the parameters required to made different " +
            "searches of actions"),
    DESCRIPTION_SEARCH_ACTIONS_BY_CRITERIA("Brings the basic information of each action made for the users in " +
            "each channel. It also allow to add search criteria to return only the necessary information."),
    DESCRIPTION_SEARCH_ACTIONS_BY_TRACKER_ID("Brings the basic information of all actions made for the transaction " +
            "with the specific tracker id."),
    DESCRIPTION_REPORT_BY_SEARCH_CRITERIA("Create in a file the basic information of the actions found by the " +
            "search criteria. The file can be specified in pdf, xlsx format. The file will be uploaded " +
            "asynchronously to the secure mailbox (system external to the microservice in which the files are " +
            "available) when is finished being created."),
    DESCRIPTION_REPORT_HISTORY_BY_CODE("Generate and return the actions information in a file for the transactions " +
            "with the specific code. The file can be specified in pdf, xlsx format."),
    DESCRIPTION_REPORT_DETAIL_BY_CODE("Generates and returns the detailed information in a file for the " +
            "transaction with the specific code. At this time the downloaded format is only available in pdf."),
    DESCRIPTION_PRODUCTS("Returns the list of products that the customer uses."),
    DESCRIPTION_PARAMETERS("Returns the parameters of the specified template, which has been configured in Channel " +
            "Management for the channel.");

    private final String description;
}
