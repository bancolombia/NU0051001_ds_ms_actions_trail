package co.com.bancolombia.api.helpers;

import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.commons.Customer;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;


import java.util.function.Consumer;
import java.util.function.Function;

@UtilityClass
public class RequestHelpers {
    public Function<Context, Consumer<HttpHeaders>> contextConsumer = context -> (headers) -> {
        headers.add("message-id", context.getId());
        headers.add("session-tracker", context.getSessionId());
        headers.add("channel", context.getChannel());
        headers.add("request-timestamp", context.getRequestDate());
        headers.add("app-version", context.getAgent().getAppVersion());
        headers.add("user-agent", context.getAgent().getName());
        Customer.Identification customerIdentification = context.getCustomer().getIdentification();
        headers.add("identification-type", customerIdentification.getType());
        headers.add("identification-number", customerIdentification.getNumber());
        headers.add("ip", context.getDevice().getIp());
        headers.add("device-id", context.getDevice().getId());
        headers.add("platform-type", context.getDevice().getType());
    };

}
