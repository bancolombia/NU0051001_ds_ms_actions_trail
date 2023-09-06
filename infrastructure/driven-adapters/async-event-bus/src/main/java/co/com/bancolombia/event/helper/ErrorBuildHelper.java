package co.com.bancolombia.event.helper;

import co.com.bancolombia.event.search.dto.commons.ErrorDTO;
import co.com.bancolombia.exception.status.HttpStatusExceptionMap;
import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.model.commons.Context;
import co.com.bancolombia.model.exception.BusinessException;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

@UtilityClass
public class ErrorBuildHelper {

    public static HttpStatus getStatus(Throwable error) {

        if(error instanceof BusinessException) {
            var businessError = (BusinessException) error;
            return HttpStatusExceptionMap.get(businessError.getBusinessErrorMessage().getCode());
        } else if (error instanceof TechnicalException) {
            var technicalError = (TechnicalException) error;
            return HttpStatusExceptionMap.get(technicalError.getTechnicalErrorMessage().getCode());
        }
        return HttpStatusExceptionMap.getDefaultStatus();
    }

    public static ErrorDTO getError(Throwable error, Context context) {

        if(error instanceof BusinessException) {
            return new ErrorDTO((BusinessException) error, context.getDomain());
        } else if (error instanceof TechnicalException) {
            return new ErrorDTO((TechnicalException) error, context.getDomain());
        }
        return new ErrorDTO(error, context.getDomain());
    }

}