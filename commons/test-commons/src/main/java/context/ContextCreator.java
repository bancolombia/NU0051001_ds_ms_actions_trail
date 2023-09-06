package context;

import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.Customer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ContextCreator {

    private static final String ID = "8f86702e-f66b-4f76-9d2d-6f7b0650734d";
    private static final String SESSION_ID = "448589";
    private static final String CHANNEL = "D2B";
    private static final String DOMAIN = "localhost";
    private static final String REQUEST_DATE = "1669940362";
    private static final String APP_VERSION = "2.0";
    private static final String NAME = "Google Chrome";
    private static final String IP = "127.0.0.1";
    private static final String DEVICE_ID = "D001";
    private static final String IDENTIFICATION_TYPE = "CC";
    private static final String IDENTIFICATION_NUMBER = "00000009043";
    private static final String DEVICE_TYPE = "mobile";

    public Context createTestContext() {
        return Context.builder()
                .id(ID)
                .sessionId(SESSION_ID)
                .channel(CHANNEL)
                .domain(DOMAIN)
                .requestDate(REQUEST_DATE)
                .agent(Context.Agent.builder()
                        .appVersion(APP_VERSION)
                        .name(NAME)
                        .build())
                .device(Context.Device.builder()
                        .ip(IP)
                        .id(DEVICE_ID)
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
