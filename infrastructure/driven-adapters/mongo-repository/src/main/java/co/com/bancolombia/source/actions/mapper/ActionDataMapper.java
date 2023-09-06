package co.com.bancolombia.source.actions.mapper;


import co.com.bancolombia.model.actions.Action;
import co.com.bancolombia.source.model.ActionData;
import org.springframework.stereotype.Component;

@Component
public class ActionDataMapper {

    public Action toEntity(ActionData actionData) {
        var action = new Action();
        action.putAll(putTransactionData(actionData));
        action.putAll(putBusinessData(actionData));
        return action;
    }

    private Action putTransactionData(ActionData actionData) {
        var action = new Action();
        action.put("transactionId", actionData.getId());
        action.put("sessionId", actionData.getSessionId());
        action.put("transactionTracker", actionData.getTransactionTracker());
        action.put("finalDate", actionData.getFinalDate());
        action.put("channel", actionData.getChannel());
        action.put("deviceNameId", actionData.getDeviceNameId());
        action.put("ip", actionData.getIp());
        action.put("authenticationType", actionData.getAuthenticationType());
        action.put("token", actionData.getToken());
        action.put("serialToken", actionData.getSerialToken());
        action.put("batchName", actionData.getBatchName());
        action.put("totalBatchRecords", actionData.getTotalBatchRecords());
        action.put("loadMechanism", actionData.getLoadMechanism());
        action.put("responseCode", actionData.getResponseCode());
        action.put("responseCodeDesc", actionData.getResponseCodeDesc());
        action.put("transactionCode", actionData.getTransactionCode());
        action.put("transactionCodeDesc", actionData.getTransactionCodeDesc());
        action.put("transactionType", actionData.getTransactionType());
        action.put("transactionState", actionData.getTransactionState());
        action.put("transactionGroup", actionData.getTransactionGroup());
        action.put("transactionVoucherNumber", actionData.getTransactionVoucherNumber());
        return action;
    }

    private Action putBusinessData(ActionData actionData) {
        var action = new Action();
        action.put("documentType", actionData.getDocumentType());
        action.put("documentNumber", actionData.getDocumentNumber());
        action.put("customerName", actionData.getCustomerName());
        action.put("authorizedUserDocumentType", actionData.getAuthorizedUserDocumentType());
        action.put("authorizedUserDocumentNumber", actionData.getAuthorizedUserDocumentNumber());
        action.put("authorizedUserName", actionData.getAuthorizedUserName());
        action.put("originProductType", actionData.getOriginProductType());
        action.put("originProductNumber", actionData.getOriginProductNumber());
        action.put("originBankCode", actionData.getOriginBankCode());
        action.put("destinyProductType", actionData.getDestinyProductType());
        action.put("destinyProductNumber", actionData.getDestinyProductNumber());
        action.put("destinyBankCode", actionData.getDestinyBankCode());
        action.put("entitlement", actionData.getEntitlement());
        action.put("localAmount", actionData.getLocalAmount());
        action.put("internationalAmount", actionData.getInternationalAmount());
        action.put("changeRate", actionData.getChangeRate());
        action.put("currency", actionData.getCurrency());
        action.put("targetCurrency", actionData.getTargetCurrency());
        action.put("paymentType", actionData.getPaymentType());
        return action;
    }
}
