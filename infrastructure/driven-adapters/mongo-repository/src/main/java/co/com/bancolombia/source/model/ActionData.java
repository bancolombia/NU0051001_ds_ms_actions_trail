package co.com.bancolombia.source.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Document(collection = "actions")
@Getter
@Setter
public class ActionData extends ActionBusinessData {
    @Id
    private String id;
    private String sessionId;
    private String transactionTracker;
    private LocalDateTime finalDate;
    private String channel;
    private String deviceNameId;
    private String ip;
    private String authenticationType;
    private String token;
    private String serialToken;
    private String batchName;
    private BigInteger totalBatchRecords;
    private String loadMechanism;
    private String responseCode;
    private String responseCodeDesc;
    private String transactionCode;
    private String transactionCodeDesc;
    private String transactionGroup;
    private String transactionType;
    private String transactionState;
}
