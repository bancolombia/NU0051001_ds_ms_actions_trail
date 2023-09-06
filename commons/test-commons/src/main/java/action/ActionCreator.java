package action;

import co.com.bancolombia.model.actions.Action;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class ActionCreator {

    private static final double DETAIL_NUMBER = 400000000000.22;

    public static Action createTestAction() {
        var action = new Action();
        action.put("Id", "aef827ea-f8e9-4a24-a78f-ca75a4c71baa");
        action.put("transactionTracker", "1323232133432");
        action.put("sessionId", "ca75a4c71baa");
        action.put("finalDate", LocalDateTime.now());
        action.put("transactionCode", "9610");
        action.put("detailOne", "9611");
        action.put("detailTwo", "9612");
        action.put("detailThree", "9613");
        action.put("detailNumber", DETAIL_NUMBER);
        action.put("historyOne", "9611-History");
        action.put("historyTwo", "9612-History");
        action.put("historyThree", "9613-History");
        action.put("customerName", "John Doe");
        action.put("documentType", "CC");
        action.put("documentNumber", "123456789");
        return action;
    }
}
