package karate.downloadactionsconsulted;

import com.intuit.karate.junit5.Karate;

import static karate.infrastructure.utils.ConstantFeatures.DOWNLOADACTIONSCONSULTED;


public class DownloadActionsConsulted {

    @Karate.Test
    Karate actionsReport() {
        return Karate.run(DOWNLOADACTIONSCONSULTED).relativeTo(getClass());
    }
}