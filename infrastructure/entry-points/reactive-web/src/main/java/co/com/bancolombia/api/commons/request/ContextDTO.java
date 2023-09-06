package co.com.bancolombia.api.commons.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
public class ContextDTO {

    @NotBlank(message = "%MESSAGE_ID_IS_NULL%")
    private final String id;
    @NotBlank(message = "%SESSION_TRACKER_IS_NULL%")
    private final String sessionId;
    @NotBlank(message = "%REQUEST_TIMESTAMP_IS_NULL%")
    private final String requestDate;
    @NotBlank(message = "%CHANNEL_IS_NULL%")
    private final String channel;
    @NotNull
    private final String domain;
    @Valid
    @NotNull
    private final Agent agent;
    @Valid
    @NotNull
    private final Device device;
    @Valid
    @NotNull
    private final Customer customer;

    @Getter
    @Builder
    public static class Agent {

        @NotBlank(message = "%USER_AGENT_IS_NULL%")
        private final String name;
        @NotBlank(message = "%APP_VERSION_IS_NULL%")
        private final String appVersion;
    }

    @Getter
    @Builder
    public static class Device {

        @NotBlank(message = "%DEVICE_ID_IS_NULL%")
        private final String id;
        @NotBlank(message = "%IP_IS_NULL%")
        private final String ip;
        @NotBlank(message = "%PLATFORM_TYPE_IS_NULL%")
        private final String type;
    }

    @Getter
    @Builder
    public static class Customer {

        @Valid
        @NotNull
        private final Identification identification;

        @Getter
        @Builder
        public static class Identification {

            @NotBlank(message = "%DOCUMENT_TYPE_CUSTOMER_IS_NULL%")
            private final String type;
            @NotBlank(message = "%DOCUMENT_NUMBER_CUSTOMER_IS_NULL%")
            private final String number;
        }
    }

}