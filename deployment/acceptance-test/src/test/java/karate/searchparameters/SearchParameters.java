package karate.searchparameters;

import com.intuit.karate.junit5.Karate;

import static karate.infrastructure.utils.ConstantFeatures.SEARCHPARAMETERS;

public class SearchParameters {

    @Karate.Test
    Karate searchParameters() {
        return Karate.run(SEARCHPARAMETERS).relativeTo(getClass());
    }
}