package co.com.bancolombia.event.search.dto.commons;

import co.com.bancolombia.model.commons.PageRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class PageRequestDTO implements Serializable {

    @NonNull private final Integer number;
    @NonNull private final Integer size;

    public PageRequestDTO(PageRequest pageRequest) {
        this.number = pageRequest.getNumber();
        this.size = pageRequest.getSize();
    }
}