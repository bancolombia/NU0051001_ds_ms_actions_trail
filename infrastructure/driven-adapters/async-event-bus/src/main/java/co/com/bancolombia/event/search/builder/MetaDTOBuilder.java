package co.com.bancolombia.event.search.builder;

import co.com.bancolombia.event.search.dto.commons.MetaDTO;
import co.com.bancolombia.model.commons.Context;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

import static co.com.bancolombia.event.helper.ErrorBuildHelper.getStatus;

@UtilityClass
public class MetaDTOBuilder {

    private final String SUCCESSFUL_TRANSACTION_CODE = String.valueOf(HttpStatus.OK.value());
    private final String SUCCESSFUL_TRANSACTION_CODE_DESCRIPTION = HttpStatus.OK.getReasonPhrase();
    private final String NON_MONETARY_TRANSACTION = "No Monetaria";
    private final String SUCCESSFUL_TRANSACTION_STATE = "Exitosa";
    private final String FAILED_TRANSACTION_STATE = "Fallida";


    public MetaDTO buildSuccessMeta(Context context) {
        return new MetaDTO(context, SUCCESSFUL_TRANSACTION_CODE, SUCCESSFUL_TRANSACTION_CODE_DESCRIPTION,
                NON_MONETARY_TRANSACTION, SUCCESSFUL_TRANSACTION_STATE);
    }

    public MetaDTO buildErrorMeta(Context context, Throwable error) {
        var httpStatus = getStatus(error);
        var transactionCode = String.valueOf(httpStatus.value());
        String transactionCodeDescription = httpStatus.getReasonPhrase();
        return new MetaDTO(context, transactionCode, transactionCodeDescription, NON_MONETARY_TRANSACTION,
                FAILED_TRANSACTION_STATE);
    }
}
