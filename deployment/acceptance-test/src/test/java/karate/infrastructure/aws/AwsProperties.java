package karate.infrastructure.aws;

import lombok.Builder;
import lombok.Value;
import karate.infrastructure.aws.properties.S3Properties;

import java.util.Map;

@Value
@Builder
public class AwsProperties {

    String region;
    String endpoint;
    S3Properties s3;
    private static final String REGION = "region";
    private static final String ENDPOINT = "endpoint";

    @SuppressWarnings("unchecked")
    public static AwsProperties fromMap(Map<String, Object> properties) {
        return AwsProperties.builder()
                .region((String) properties.get(REGION))
                .endpoint((String) properties.get(ENDPOINT))
                .s3(S3Properties.fromMap((Map<String, Object>) properties.get("s3")))
                .build();
    }

    public boolean hasEndpoint() {
        return getEndpoint() != null && !getEndpoint().isEmpty();
    }
}
