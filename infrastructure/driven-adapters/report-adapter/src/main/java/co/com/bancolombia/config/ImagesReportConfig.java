package co.com.bancolombia.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ActionsReportProperties.class)
public class ImagesReportConfig {

    private static final String BANK_LOGO_KEY = "bankLogo";
    private static final String SUPERVISED_LOGO_KEY = "supervisedLogo";
    private static final String STROKE_IMAGE_KEY = "strokeImage";

    private final ActionsReportProperties actionsReportProperties;

    @Bean
    public ReportImageLoader imageLoader(){
        Map<String, String> reportImages = new HashMap<>();

        reportImages.put(BANK_LOGO_KEY, actionsReportProperties.getImages().getBankLogo());
        reportImages.put(SUPERVISED_LOGO_KEY, actionsReportProperties.getImages().getSupervisedLogo());
        reportImages.put(STROKE_IMAGE_KEY, actionsReportProperties.getImages().getStrokeImage());
        return new ReportImageLoader(reportImages, actionsReportProperties.getFormat().getPageIndex());
    }
}
