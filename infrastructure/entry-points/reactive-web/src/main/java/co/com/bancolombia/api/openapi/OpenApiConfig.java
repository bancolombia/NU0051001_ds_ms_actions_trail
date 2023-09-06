package co.com.bancolombia.api.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title("Actions trail")
                        .version(appVersion)
                        .description("Actions trail facilitates the experience for digital banking users to " +
                                "search all kind of information that they or other users have made over their " +
                                "financial products, such as monetary and non monetary transactions, and the " +
                                "different actions made available to users. The information that actions " +
                                "trail provides is a subset of the last 3 months collected by the channel's " +
                                "central repository."));
    }
}