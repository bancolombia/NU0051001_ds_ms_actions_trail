package co.com.bancolombia.event.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "adapter")
public class EventNameProperties {

    private final String channel;
    private final String publisher;
    private final String prefix;
    private final EventBusinessAction eventBusinessAction;

    @Getter
    @RequiredArgsConstructor
    public static class EventBusinessAction {
        private final String actionPrefix;
        private final String reportPrefix;
        private final Suffix suffix;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Suffix {
        private final String actionHistoryQueryDone;
        private final String actionHistoryQueryRejected;
        private final String actionHistoryDownloadDone;
        private final String actionHistoryDownloadRejected;
        private final String actionSearchDone;
        private final String actionSearchRejected;
        private final String actionDetailedReportDone;
        private final String actionDetailedReportRejected;
        private final String actionDownloadDone;
        private final String actionDownloadRejected;
    }

}