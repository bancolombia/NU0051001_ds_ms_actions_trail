package co.com.bancolombia.event;

import co.com.bancolombia.event.properties.EventNameProperties;
import co.com.bancolombia.event.properties.EventNameProperties.Suffix;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.Customer;
import co.com.bancolombia.model.commons.PageRequest;
import co.com.bancolombia.model.search.AnySearchCriterion;
import co.com.bancolombia.model.search.SearchValue;
import co.com.bancolombia.model.search.criteria.TransactionCodeCriterion;
import co.com.bancolombia.model.search.enums.ComparisonOperator;

import java.util.Set;

public class DataBuilder {

    private static final String ID = "id";
    private static final String SESSION_ID = "sessionId";
    private static final String REQUEST_DATE = "requestDate";
    private static final String CHANNEL = "channel";
    private static final String AGENT_NAME = "agentName";
    private static final String APP_VERSION = "appVersion";
    private static final String DEVICE_ID = "deviceId";
    private static final String DEVICE_IP = "deviceIp";
    private static final String IDENTIFICATION_TYPE = "identificationType";
    private static final String IDENTIFICATION_NUMBER = "identificationNumber";
    private static final String DOMAIN = "myPath";
    private static final String NUMBER_EXCEPTION_MESSAGE = "Number format exception";
    private static final String DEVICE_TYPE = "mobile";

    public static Throwable getThrowable() {
        return new Throwable(NUMBER_EXCEPTION_MESSAGE, new NumberFormatException(NUMBER_EXCEPTION_MESSAGE));
    }

    public static PageRequest getPageRequest() {
        return new PageRequest(2, 10);
    }

    public static TransactionCodeCriterion getTransactionCodeCriterion() {
        SearchValue<String> searchValue = new SearchValue<>("Test", ComparisonOperator.EQUALS);
        return new TransactionCodeCriterion("searchValue");
    }

    public static Set<AnySearchCriterion<?>> getAbstractCriterion() {

        return Set.of(getTransactionCodeCriterion());
    }

    public static Suffix getSuffix() {
        return new Suffix(
                "ActionHistoryQueryDone", "ActionHistoryQueryRejected",
                "ActionSearchDone","ActionSearchRejected",
                "ActionHistoryDownloadDone", "ActionHistoryDownloadRejected",
                "ActionDetailedReportDone", "ActionDetailedReportRejected",
                "ActionDownloadDone", "ActionDownloadRejected");
    }

    public static EventNameProperties getEventNameProperties() {
        return new EventNameProperties("DBB", "distributionMicroservice", "prefix",
                new EventNameProperties.EventBusinessAction("prefix","action", getSuffix()));

    }

    public static Context getContext() {
        return Context.builder()
                .id(ID)
                .sessionId(SESSION_ID)
                .requestDate(REQUEST_DATE)
                .channel(CHANNEL)
                .domain(DOMAIN)
                .agent(Context.Agent.builder()
                        .name(AGENT_NAME)
                        .appVersion(APP_VERSION)
                        .build())
                .device(Context.Device.builder()
                        .id(DEVICE_ID)
                        .ip(DEVICE_IP)
                        .type(DEVICE_TYPE)
                        .build())
                .customer(Customer.builder()
                        .identification(Customer.Identification.builder()
                                .type(IDENTIFICATION_TYPE)
                                .number(IDENTIFICATION_NUMBER)
                                .build())
                        .build())
                .build();
    }
}
