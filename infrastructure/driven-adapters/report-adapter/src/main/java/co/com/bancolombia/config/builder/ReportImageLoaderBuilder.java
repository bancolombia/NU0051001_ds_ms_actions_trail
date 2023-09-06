package co.com.bancolombia.config.builder;

import co.com.bancolombia.config.ReportImageLoader;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ReportImageLoaderBuilder {
    private static final String BANK_LOGO= "bankLogo";
    private static final String SUPERVISED_LOGO = "supervisedLogo";
    private static final String STROKE_IMAGE = "strokeImage";
    private static final String PAGE_INDEX = "";

    public static ReportImageLoader buildReportImageLoader() {
        Map<String, String> reportImages = new HashMap<>();

        reportImages.put(BANK_LOGO, null);
        reportImages.put(SUPERVISED_LOGO, null);
        reportImages.put(STROKE_IMAGE, null);
        return new ReportImageLoader(reportImages, PAGE_INDEX);
    }
}
