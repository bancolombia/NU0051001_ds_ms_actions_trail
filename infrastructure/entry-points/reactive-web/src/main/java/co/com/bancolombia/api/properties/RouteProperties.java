package co.com.bancolombia.api.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import co.com.bancolombia.api.properties.RouteProperties.RouteActionProperties;
import co.com.bancolombia.api.properties.RouteProperties.RouteParameterProperties;

@Configuration
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EnableConfigurationProperties({RouteActionProperties.class, RouteParameterProperties.class})
public class RouteProperties {

    @Getter
    @ConstructorBinding
    @RequiredArgsConstructor
    @ConfigurationProperties(prefix = "routes.path-mapping")
    public static class RouteActionProperties {

        private final Search actionSearch;
        private final Report actionReport;

        @Getter
        @RequiredArgsConstructor
        public static class Search {
            private final String byCriteria;
            private final String byTrackerId;
        }

        @Getter
        @RequiredArgsConstructor
        public static class Report {
            private final String byCriteria;
            private final String history;
            private final String detail;
        }
    }

    @Getter
    @ConstructorBinding
    @RequiredArgsConstructor
    @ConfigurationProperties(prefix = "routes.path-mapping.parameter")
    public static class RouteParameterProperties {

        private final String product;
        private final String allParameters;

    }
}