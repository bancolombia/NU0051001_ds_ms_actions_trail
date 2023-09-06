package co.com.bancolombia.exception.status;

import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class HttpStatusExceptionMap {

    private static final Map<String, HttpStatus> httpStatusException;

    static {
        httpStatusException = new HashMap<>();

        httpStatusException
                .put(TechnicalErrorMessage.UNEXPECTED_EXCEPTION.getCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        httpStatusException
                .put(TechnicalErrorMessage.DATABASE_CONNECTION.getCode(), HttpStatus.CONFLICT);
        httpStatusException
                .put(TechnicalErrorMessage.PARAMETER_NOT_FOUND.getCode(), HttpStatus.NOT_FOUND);
        httpStatusException
                .put(BusinessErrorMessage.INVALID_PAGE_NUMBER.getCode(), HttpStatus.UNPROCESSABLE_ENTITY);
        httpStatusException
                .put(BusinessErrorMessage.INVALID_PAGE_SIZE.getCode(), HttpStatus.UNPROCESSABLE_ENTITY);
        httpStatusException
                .put(BusinessErrorMessage.INVALID_OPERATOR.getCode(), HttpStatus.UNPROCESSABLE_ENTITY);
        httpStatusException
                .put(BusinessErrorMessage.NON_ACTION_FOUND.getCode(), HttpStatus.NOT_FOUND);
    }

    public static HttpStatus get(String code) {
        return httpStatusException.containsKey(code) ? httpStatusException.get(code) : getDefaultStatus();
    }

    public static HttpStatus getDefaultStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}