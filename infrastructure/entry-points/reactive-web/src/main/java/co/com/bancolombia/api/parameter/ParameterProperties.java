package co.com.bancolombia.api.parameter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Validated
@Configuration
@ConfigurationProperties(prefix = "channel-management")
public class ParameterProperties {

    @NotBlank private String transactionCode;
    @NotBlank private String parameterName;
}