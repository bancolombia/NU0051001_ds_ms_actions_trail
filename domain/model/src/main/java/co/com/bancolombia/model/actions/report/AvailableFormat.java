package co.com.bancolombia.model.actions.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Available Formats to create and return a file with the information of the action reports .
 */
@Getter
@RequiredArgsConstructor
public enum AvailableFormat {

    PDF("pdf"),
    XLSX("xlsx");

    private final String format;
}
