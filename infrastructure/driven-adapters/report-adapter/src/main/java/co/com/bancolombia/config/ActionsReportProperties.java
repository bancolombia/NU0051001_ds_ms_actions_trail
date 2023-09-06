package co.com.bancolombia.config;

import co.com.bancolombia.exception.technical.TechnicalException;
import co.com.bancolombia.exception.technical.message.TechnicalErrorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;


@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties("report")
@Getter
public class ActionsReportProperties {
    private final Images images;
    private final Header header;
    private final Fields fields;
    private final Format format;
    private final DetailedReport detailedReport;
    private final Filename filename;
    private final String cloudName;

    @Getter
    @RequiredArgsConstructor
    public static class Images {
        private final String bankLogo;
        private final String supervisedLogo;
        private final String strokeImage;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Header {
        private final String reportByCriteriaTitle;
        private final String reportHistoryTitle;
        private final String reportDetailedTitle;
        private final String personalizedChannelName;
    }

    @RequiredArgsConstructor
    public static class Fields {
        private final String basicInformation;
        private final String detailedInformation;
        private final String historyInformation;
        private final ObjectMapper objectMapper = new ObjectMapper();

        public Map<String, String> getBasicInformation(){
            return getReportInformationMap(this.basicInformation);
        }
        public Map<String, String> getDetailedInformation(){
            return getReportInformationMap(this.detailedInformation);
        }
        public Map<String, String> getHistoryInformation(){
            return getReportInformationMap(this.historyInformation);
        }
        @SuppressWarnings("fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS")
        private Map<String, String> getReportInformationMap(String reportInformationJson) {
            try {
                return objectMapper.readValue(reportInformationJson, Map.class);
            } catch (JsonProcessingException e) {
                throw new TechnicalException(e, TechnicalErrorMessage.INVALID_REPORT_FIELD_FORMAT);
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Format {
        private final String date;
        private final String number;
        private final String pageIndex;
    }

    @Getter
    @RequiredArgsConstructor
    public static class DetailedReport {
        public final String basicReportSubtitle;
        public final String detailedReportSubtitle;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Filename {
        public final String byCriteria;
        public final String history;
        public final String detailed;
        public final String secureMailBoxTransactionCode;
    }

}
