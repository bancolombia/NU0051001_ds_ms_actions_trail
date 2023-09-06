package co.com.bancolombia.model.exception;

import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BusinessErrorMessage businessErrorMessage;

    public BusinessException(BusinessErrorMessage businessErrorMessage) {
        super(businessErrorMessage.getMessage());
        this.businessErrorMessage = businessErrorMessage;
    }
}