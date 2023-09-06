package co.com.bancolombia.action;

import co.com.bancolombia.model.actions.Action;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ActionTest {

    @Test
    void shouldValidateActionExtendsFromMap() {
        Action action = new Action();
        assertThat(action).isInstanceOf(Map.class);
    }
}
