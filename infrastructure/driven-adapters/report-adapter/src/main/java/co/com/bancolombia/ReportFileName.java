package co.com.bancolombia;

import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder(toBuilder = true)
public class ReportFileName {
    private static final String DOT = ".";
    private static final String UNDERSCORE = "_";
    private static final String MIDDLE_DASH = "-";
    private static final String DATE_TIME_FORMAT = "dd-MM-yyyy-hh-mm-ss";
    @NonNull
    private final Context context;
    @NonNull
    private final String transactionCode;
    @NonNull
    private final String fileName;
    @NonNull
    private final AvailableFormat format;

    public String getFileNameSecureBox() {
        return new StringBuilder()
                .append(context.getChannel())
                .append(UNDERSCORE)
                .append(transactionCode)
                .append(UNDERSCORE)
                .append(context.getCustomer().getIdentification().getType())
                .append(UNDERSCORE)
                .append(context.getCustomer().getIdentification().getNumber())
                .append(UNDERSCORE)
                .append(fileName)
                .append(MIDDLE_DASH)
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
                .append(DOT)
                .append(format.getFormat())
                .toString();
    }
}
