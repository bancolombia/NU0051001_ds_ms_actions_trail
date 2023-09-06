package karate.downloadactiondetail;

import com.intuit.karate.junit5.Karate;
import static karate.infrastructure.utils.ConstantFeatures.DOWNLOADACTIONDETAIL;


public class DownloadActionDetail {
    @Karate.Test
    Karate actionsReport() {
        return Karate.run(DOWNLOADACTIONDETAIL).relativeTo(getClass());
    }
}