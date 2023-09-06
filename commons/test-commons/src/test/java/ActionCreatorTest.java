import action.ActionCreator;
import co.com.bancolombia.model.actions.Action;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class ActionCreatorTest {

    @Test
    void shouldCreateActionWithTestData() {
        Action action = ActionCreator.createTestAction();
        assertThat(action.get("Id")).isNotNull();
        assertThat(action.get("transactionTracker")).isNotNull();
        assertThat(action.get("sessionId")).isNotNull();
        assertThat(action.get("finalDate")).isNotNull();
        assertThat(action.get("transactionCode")).isNotNull();
        assertThat(action.get("detailOne")).isNotNull();
        assertThat(action.get("detailTwo")).isNotNull();
        assertThat(action.get("detailThree")).isNotNull();
        assertThat(action.get("customerName")).isNotNull();
        assertThat(action.get("documentType")).isNotNull();
        assertThat(action.get("documentNumber")).isNotNull();
    }
}
