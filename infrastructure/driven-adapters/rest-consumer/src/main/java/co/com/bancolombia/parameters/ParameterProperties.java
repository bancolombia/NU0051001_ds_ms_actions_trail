package co.com.bancolombia.parameters;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties("adapter.rest-consumer.channel-management")
public class ParameterProperties {

    private final String baseUrl;
    private final String parameterPath;

    public String getParameterPath() {
        return baseUrl.concat(parameterPath);
    }
}
