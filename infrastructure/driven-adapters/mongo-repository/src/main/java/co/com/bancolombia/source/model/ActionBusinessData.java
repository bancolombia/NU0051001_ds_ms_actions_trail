package co.com.bancolombia.source.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActionBusinessData {

    private String documentType;
    private String documentNumber;
    private String customerName;
    private String authorizedUserDocumentType;
    private String authorizedUserDocumentNumber;
    private String authorizedUserName;
    private String originProductType;
    private String originProductNumber;
    private String destinyProductType;
    private String destinyProductNumber;
    private String destinyBankCode;
    private String originBankCode;
    private String entitlement;
    private Double localAmount;
    private Float internationalAmount;
    private String currency;
    private String targetCurrency;
    private Double changeRate;
    private String paymentType;
    private String transactionVoucherNumber;
}
