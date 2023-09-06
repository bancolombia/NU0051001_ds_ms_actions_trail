package co.com.bancolombia.usecase.utils;

import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.Customer;

public class DataBuilder {

    private static final String id = "id";
    private static final String sessionId = "sessionId";
    private static final String requestDate = "requestDate";
    private static final String channel = "channel";
    private static final String agentName = "agentName";
    private static final String appVersion = "appVersion";
    private static final String deviceId = "deviceId";
    private static final String deviceIp = "deviceIp";
    private static final String identificationType = "identificationType";
    private static final String identificationNumber = "identificationNumber";
    private static final String domain = "myPath";
    private static final String deviceType = "mobile";

    public static Context buildContext() {
        return Context.builder()
                .id(id)
                .sessionId(sessionId)
                .requestDate(requestDate)
                .channel(channel)
                .domain(domain)
                .agent(Context.Agent.builder()
                        .name(agentName)
                        .appVersion(appVersion)
                        .build())
                .device(Context.Device.builder()
                        .id(deviceId)
                        .ip(deviceIp)
                        .type(deviceType)
                        .build())
                .customer(Customer.builder()
                        .identification(Customer.Identification.builder()
                                .type(identificationType)
                                .number(identificationNumber)
                                .build())
                        .build())
                .build();
    }
}
