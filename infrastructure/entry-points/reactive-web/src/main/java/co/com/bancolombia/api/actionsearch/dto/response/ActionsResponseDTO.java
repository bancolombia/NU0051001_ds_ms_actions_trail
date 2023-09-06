package co.com.bancolombia.api.actionsearch.dto.response;

import co.com.bancolombia.model.actions.Action;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ActionsResponseDTO {
    private final List<Action> data;
}
