package co.com.bancolombia.event.search.builder;

import co.com.bancolombia.d2b.messaging.GalateaEventSpec;
import co.com.bancolombia.d2b.model.events.AbstractEventSpec;
import co.com.bancolombia.event.properties.EventNameProperties;
import co.com.bancolombia.model.commons.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class EventSpecBuilder {

    private final String applicationName;
    private final EventNameProperties eventNameProperties;

    public EventSpecBuilder(@Value("${spring.application.name}")
                            String applicationName, EventNameProperties eventNameProperties) {
        this.applicationName = applicationName;
        this.eventNameProperties = eventNameProperties;
    }

    private static final String EVENTS_SPEC_VERSION = "1.0";
    private static final String SECURITY_FILTERS = "securityFilters";
    private static final String DOT = ".";

    public <T extends Serializable> AbstractEventSpec<T> buildEventSpec(Context context, String eventSuffix,
                                                                        String prefix, T eventData) {
        return GalateaEventSpec.<T>builder()
                .specVersion(EVENTS_SPEC_VERSION)
                .type(produceBusinessEventName(prefix, eventSuffix))
                .source(applicationName)
                .id(context.getId())
                .time(context.getRequestDate())
                .invoker(SECURITY_FILTERS)
                .dataContentType(MediaType.APPLICATION_JSON_VALUE)
                .data(eventData)
                .build();
    }

    private String produceBusinessEventName(String prefix, String suffix) {
        return eventNameProperties.getPrefix()
                .concat(DOT)
                .concat(prefix)
                .concat(DOT)
                .concat(eventNameProperties.getChannel())
                .concat(DOT)
                .concat(eventNameProperties.getPublisher())
                .concat(DOT)
                .concat(suffix);
    }
}
