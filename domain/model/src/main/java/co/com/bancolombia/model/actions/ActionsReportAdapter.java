package co.com.bancolombia.model.actions;

import co.com.bancolombia.model.actions.report.AvailableFormat;
import co.com.bancolombia.model.commons.Context;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Communicates the domain with the adapter responsible for generating and uploading reports in different formats.
 */
public interface ActionsReportAdapter {

    Mono<byte[]> generateReport(List<Action> actions, AvailableFormat format);
    Mono<byte[]> generateReportHistory(List<Action> actions, AvailableFormat format, Context context);
    Mono<byte[]> generateDetailedReport(Action action, AvailableFormat format, Context context);
    Mono<String> uploadReport(byte[] bytes, Context context, AvailableFormat format);
}
