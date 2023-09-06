package karate.viewactionhistory;

import com.intuit.karate.junit5.Karate;

import static karate.infrastructure.utils.ConstantFeatures.EVENTSACTIONHISTORY;

public class EventActionHistory {

    @Karate.Test
    Karate eventActionHistory() {
        return Karate.run(EVENTSACTIONHISTORY).relativeTo(getClass());
    }
}