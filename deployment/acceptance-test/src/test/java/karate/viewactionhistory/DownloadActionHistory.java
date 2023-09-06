package karate.viewactionhistory;

import com.intuit.karate.junit5.Karate;

import static karate.infrastructure.utils.ConstantFeatures.DOWNLOADACTIONHISTORY;

public class DownloadActionHistory {

    @Karate.Test
    Karate downloadActionHistory() {
        return Karate.run(DOWNLOADACTIONHISTORY).relativeTo(getClass());
    }
}