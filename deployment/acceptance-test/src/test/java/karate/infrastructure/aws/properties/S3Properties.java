package karate.infrastructure.aws.properties;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class S3Properties {
    String bucketName;
    private static final String bucketNameString = "bucketName";

    public static S3Properties fromMap(Map<String, Object> properties) {
        return S3Properties.builder()
                .bucketName((String) properties.get(bucketNameString))
                .build();
    }
}