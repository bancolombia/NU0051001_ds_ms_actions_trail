package co.com.bancolombia.commons;

import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContextTest {

    private static final String ID_ONE = "id1";
    private static final String ID_TWO = "id1";
    private static final String ID_THREE = "id3";
    private static final String SESSION_ID = "sessionId";
    private static final String REQUEST_DATE = "requestDate";
    private static final String CHANNEL = "channel";
    private static final String AGENT_NAME = "agentName";
    private static final String APP_VERSION = "appVersion";
    private static final String DEVICE_ID = "deviceId";
    private static final String DEVICE_IP = "deviceIp";
    private static final String IDENTIFICATION_TYPE = "identificationType";
    private static final String IDENTIFICATION_NUMBER = "identificationNumber";
    private static final String PLATFORM_TYPE = "mobile";
    private static final String DOMAIN = "/myPath";

    @Test
    void shouldCreateContextSuccessfully() {

        Context context = Context.builder()
                .id(ID_ONE)
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
                        .type(PLATFORM_TYPE)
                        .build())
                .customer(Customer.builder()
                        .identification(Customer.Identification.builder()
                                .type(IDENTIFICATION_TYPE)
                                .number(IDENTIFICATION_NUMBER)
                                .build())
                        .build())
                .build();

        assertEquals(ID_ONE, context.getId());
        assertEquals(SESSION_ID, context.getSessionId());
        assertEquals(REQUEST_DATE, context.getRequestDate());
        assertEquals(CHANNEL, context.getChannel());
        assertEquals(DOMAIN, context.getDomain());
        assertEquals(AGENT_NAME, context.getAgent().getName());
        assertEquals(APP_VERSION, context.getAgent().getAppVersion());
        assertEquals(DEVICE_ID, context.getDevice().getId());
        assertEquals(DEVICE_IP, context.getDevice().getIp());
        assertEquals(PLATFORM_TYPE, context.getDevice().getType());
        assertEquals(IDENTIFICATION_TYPE, context.getCustomer().getIdentification().getType());
        assertEquals(IDENTIFICATION_NUMBER, context.getCustomer().getIdentification().getNumber());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenParametersAreNull() {
        Context.Agent agent = Context.Agent.builder().name("").appVersion("").build();
        Context.Device device = Context.Device.builder().ip("").id("").type("").build();

        Customer customer = Customer.builder()
                .identification(Customer.Identification.builder()
                        .number("")
                        .type("")
                        .build())
                .build();

        Context.ContextBuilder withoutID = Context.builder().sessionId("").requestDate("").domain("").channel("").agent(agent).device(device).customer(customer);
        Context.ContextBuilder withoutSessionId = Context.builder().id("").requestDate("").domain("").channel("").agent(agent).device(device).customer(customer);
        Context.ContextBuilder withoutRequestDate = Context.builder().id("").sessionId("").domain("").channel("").agent(agent).device(device).customer(customer);
        Context.ContextBuilder withoutChannel = Context.builder().id("").sessionId("").domain("").requestDate("").agent(agent).device(device).customer(customer);
        Context.ContextBuilder withoutDomain = Context.builder().id("").sessionId("").requestDate("").channel("").agent(agent).device(device).customer(customer);
        Context.ContextBuilder withoutAgent = Context.builder().id("").sessionId("").domain("").requestDate("").channel("").device(device).customer(customer);
        Context.ContextBuilder withoutDevice = Context.builder().id("").sessionId("").domain("").requestDate("").channel("").agent(agent).customer(customer);
        Context.ContextBuilder withoutCustomer = Context.builder().id("").sessionId("").domain("").requestDate("").channel("").device(device).agent(agent);

        assertThrows(NullPointerException.class, withoutID::build);
        assertThrows(NullPointerException.class, withoutSessionId::build);
        assertThrows(NullPointerException.class, withoutRequestDate::build);
        assertThrows(NullPointerException.class, withoutChannel::build);
        assertThrows(NullPointerException.class, withoutDomain::build);
        assertThrows(NullPointerException.class, withoutAgent::build);
        assertThrows(NullPointerException.class, withoutDevice::build);
        assertThrows(NullPointerException.class, withoutCustomer::build);
    }

    @Test
    void testEquals() {

        Context context1 = Context.builder()
                .id(ID_ONE)
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
                        .type(PLATFORM_TYPE)
                        .build())
                .customer(Customer.builder()
                        .identification(Customer.Identification.builder()
                                .type(IDENTIFICATION_TYPE)
                                .number(IDENTIFICATION_NUMBER)
                                .build())
                        .build())
                .build();

        Context context2 = Context.builder()
                .id(ID_TWO)
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
                        .type(PLATFORM_TYPE)
                        .build())
                .customer(Customer.builder()
                        .identification(Customer.Identification.builder()
                                .type(IDENTIFICATION_TYPE)
                                .number(IDENTIFICATION_NUMBER)
                                .build())
                        .build())
                .build();

        Context context3 = Context.builder()
                .id(ID_THREE)
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
                        .type(PLATFORM_TYPE)
                        .build())
                .customer(Customer.builder()
                        .identification(Customer.Identification.builder()
                                .type(IDENTIFICATION_TYPE)
                                .number(IDENTIFICATION_NUMBER)
                                .build())
                        .build())
                .build();

        assertEquals(context1, context1);
        assertEquals(context1, context2);

        assertNotEquals(context1, context3);
        assertNotEquals(context1, new Object());

        assertNotNull(context1);
    }
}
