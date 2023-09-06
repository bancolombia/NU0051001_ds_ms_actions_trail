package karate.viewactionhistory;

import com.intuit.karate.junit5.Karate;

import static karate.infrastructure.utils.ConstantFeatures.VIEWACTIONHISTORY;


public class ViewActionHistory {


    @Karate.Test
    Karate viewActionHistory() {
        return Karate.run(VIEWACTIONHISTORY).relativeTo(getClass());
    }
}