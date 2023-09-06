package co.com.bancolombia.api.actionsearch.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.time.LocalDate;

@Getter
public class SearchCriteriaRequestDTO {

    @Valid
    @NotNull(message = "%SEARCH_CRITERIA_NOT_FOUND%")
    private final SearchCriteria searchCriteria;

    @JsonCreator
    public SearchCriteriaRequestDTO(@JsonProperty("searchCriteria") SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    @Getter
    public static class SearchCriteria {
        @Valid
        @NotNull(message = "%DATE_RANGE_IS_NOT_FOUND%")
        private final DateRange dateRange;
        private final String state;
        private final String type;
        private final String name;
        private final String bankEntity;
        private final Product product;
        private final User user;

        @ConstructorProperties({"dateRange","state","type","name", "bankEntity", "product", "user"})
        public SearchCriteria(DateRange dateRange, String state, String type, String name, String bankEntity,
                              Product product, User user) {
            this.dateRange = dateRange;
            this.state = state;
            this.type = type;
            this.name = name;
            this.bankEntity = bankEntity;
            this.product = product;
            this.user = user;
        }
    }

    @Getter
    public static class DateRange {
        @NotNull(message = "%START_DATE_IS_NOT_FOUND_OR_NULL%")
        private final LocalDate startDate;
        @NotNull(message = "%END_DATE_IS_NOT_FOUND_OR_NULL%")
        private final LocalDate endDate;

        @ConstructorProperties({"startDate","endDate"})
        public DateRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    @Getter
    public static class Identification {
        private final String type;
        private final String number;

        @ConstructorProperties({"type","number"})
        public Identification(String type, String number) {
            this.type = type;
            this.number = number;
        }
    }

    @Getter
    public static class User {
        private final String name;
        private final Identification identification;

        @ConstructorProperties({"name","identification"})
        public User(String name, Identification identification) {
            this.name = name;
            this.identification = identification;
        }
    }

    @Getter
    public static class Product {
        private final String type;
        private final String number;

        @ConstructorProperties({"type","number"})
        public Product(String type, String number) {
            this.type = type;
            this.number = number;
        }
    }
}