package co.com.bancolombia.model.commons;

import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

/**
 * PageSummary represents the summary of the information obtained from a specific request
 * and the meta information of the pagination strategy such as the total number of pages.
 */
@Value
public class PageSummary<T> {

    @NonNull List<T> data;
    @NonNull PageRequest pageRequest;
    @NonNull Long totalRegisters;

    public List<T> getData() {
        return Collections.unmodifiableList(this.data);
    }

    public int getSize() {
        return this.getData().size();
    }

    public int getPageNumber() {
        return this.pageRequest.getNumber();
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) this.totalRegisters/pageRequest.getSize());
    }

    public boolean hasNext() {
        return this.totalRegisters > 0 && !this.isLast();
    }

    public boolean hasPrevious() {
        return this.totalRegisters > 0 && !this.isFirst();
    }

    public boolean isFirst() {
        return this.totalRegisters > 0 && this.getPageNumber() == 1;
    }

    public boolean isLast() {
        return this.totalRegisters > 0 && this.getPageNumber() == this.getTotalPages();
    }
}
