package co.com.bancolombia.exception;

import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RestErrorMapper {

    private final Map<String, TechnicalErrorMessage> restErrorCodes;

    public RestErrorMapper() {
        restErrorCodes = new HashMap<>();
        restErrorCodes.put("CTB0014", TechnicalErrorMessage.PARAMETER_NOT_FOUND);
    }

    public TechnicalErrorMessage getTechnicalErrorMessage(String code) {
        return restErrorCodes.getOrDefault(code, TechnicalErrorMessage.REST_CLIENT_EXCEPTION);
    }

}
