package co.com.bancolombia.source.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;


@ConfigurationProperties("adapter.persistence")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class PersistenceProperties {
    private final Map<String, String> searchCriteriaCodesMapping;

}
