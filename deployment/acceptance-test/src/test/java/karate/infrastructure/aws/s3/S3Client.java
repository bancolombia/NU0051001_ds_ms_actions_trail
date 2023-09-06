package karate.infrastructure.aws.s3;

import karate.infrastructure.aws.AwsProperties;
import karate.infrastructure.aws.properties.S3Properties;
import lombok.SneakyThrows;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;


import java.net.URI;

public class S3Client {
    private final software.amazon.awssdk.services.s3.S3Client awsS3Client;
    private final S3Properties s3Properties;


    public S3Client(AwsProperties awsProperties) {
        var s3ClientBuilder = software.amazon.awssdk.services.s3.S3Client.builder();

        if (awsProperties.hasEndpoint()) {
            s3ClientBuilder = s3ClientBuilder.endpointOverride(URI.create(awsProperties.getEndpoint()));
        }
        awsS3Client = s3ClientBuilder.build();
        s3Properties = awsProperties.getS3();
    }

    @SneakyThrows
    public Boolean checkFileExistInS3(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(key)
                .build();
        try {
            return awsS3Client.getObject(request).readAllBytes().length > 0;
        } catch (Exception e){
            return  false;
        }
    }

}
