package co.com.bancolombia.model.commons;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import lombok.Getter;
import lombok.NonNull;

/**
 * PageRequest represents the request to bring the data from a specific page with the final purpose
 * to limit the information.
 */
@Getter
public class PageRequest {

    private final Integer number;
    private final Integer size;

    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_SIZE = 10;

    public PageRequest(@NonNull Integer number, @NonNull Integer size) {
        if (number < 1) {
            throw new BusinessException(BusinessErrorMessage.INVALID_PAGE_NUMBER);
        }
        if (size < 1) {
            throw new BusinessException(BusinessErrorMessage.INVALID_PAGE_SIZE);
        }
        this.number = number;
        this.size = size < MAX_SIZE ? size : MAX_SIZE;
    }

    public PageRequest(@NonNull Integer number) {
        if (number < 1) {
            throw new BusinessException(BusinessErrorMessage.INVALID_PAGE_NUMBER);
        }
        this.number = number;
        this.size = DEFAULT_SIZE;
    }
}