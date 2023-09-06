package co.com.bancolombia.event.search.dto.commons;

import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.exception.BusinessException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class ErrorDTO implements Serializable {

    @NonNull
    private final String reason;
    @NonNull
    private final String domain;
    @NonNull
    private final String code;
    @NonNull
    private final String message;

    public ErrorDTO(TechnicalException technicalException, String domain) {

        this.reason = technicalException.getTechnicalErrorMessage().getMessage();
        this.domain = domain;
        this.code = technicalException.getTechnicalErrorMessage().getCode();
        this.message = technicalException.getTechnicalErrorMessage().getMessage();
    }

    public ErrorDTO(BusinessException businessException, String domain) {

        this.reason = businessException.getBusinessErrorMessage().getMessage();
        this.domain = domain;
        this.code = businessException.getBusinessErrorMessage().getCode();
        this.message = businessException.getBusinessErrorMessage().getMessage();
    }

    public ErrorDTO(Throwable throwable, String domain) {

        this.reason = throwable.getMessage();
        this.domain = domain;
        this.code = TechnicalErrorMessage.UNEXPECTED_EXCEPTION.getCode();
        this.message = TechnicalErrorMessage.UNEXPECTED_EXCEPTION.getMessage();
    }
}