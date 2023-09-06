package karate.searchproduct;

import com.intuit.karate.junit5.Karate;

import static karate.infrastructure.utils.ConstantFeatures.SEARCHPRODUCT;

public class SearchProductRunner {

    @Karate.Test
    Karate searchProduct() {
        return Karate.run(SEARCHPRODUCT).relativeTo(getClass());
    }
}