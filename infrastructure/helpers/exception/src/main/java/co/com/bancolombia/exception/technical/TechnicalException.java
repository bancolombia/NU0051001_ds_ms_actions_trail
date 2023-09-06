package co.com.bancolombia.exception.technical;

import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private final TechnicalErrorMessage technicalErrorMessage;

    public TechnicalException(Throwable cause, TechnicalErrorMessage technicalErrorMessage) {
        super(technicalErrorMessage.getMessage(), cause);
        this.technicalErrorMessage = technicalErrorMessage;
    }
}