package co.com.bancolombia.event.search.dto.commons;

import co.com.bancolombia.model.commons.Context;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class MetaDTO implements Serializable {

    @NonNull private final String sessionId;
    @NonNull private final String transactionId;
    @NonNull private final String transactionCode;
    @NonNull private final String transactionCodeDesc;
    @NonNull private final String requestTimestamp;
    @NonNull private final String responseTimestamp;
    @NonNull private final String channel;
    @NonNull private final String deviceId;
    @NonNull private final String ip;
    @NonNull private final String transactionType;
    @NonNull private final String transactionState;
    @NonNull private final String documentType;
    @NonNull private final String documentNumber;
    @NonNull private final String userAgent;
    @NonNull private final String appVersion;

    public MetaDTO (Context context, String transactionCode, String transactionCodeDesc,
                             String transactionType, String transactionState) {

        var identification = context.getCustomer().getIdentification();

        this.sessionId = context.getSessionId();
        this.transactionId = context.getId();
        this.transactionCode = transactionCode;
        this.transactionCodeDesc = transactionCodeDesc;
        this.requestTimestamp = context.getRequestDate();
        this.responseTimestamp = LocalDateTime.now().toString();
        this.channel = context.getChannel();
        this.deviceId = context.getDevice().getId();
        this.ip = context.getDevice().getIp();
        this.transactionType = transactionType;
        this.transactionState = transactionState;
        this.documentType =  identification.getType();
        this.documentNumber = identification.getNumber();
        this.userAgent = context.getAgent().getName();
        this.appVersion = context.getAgent().getAppVersion();
    }

}