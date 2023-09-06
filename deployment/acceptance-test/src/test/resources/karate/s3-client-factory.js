function fn() {
    const AwsProperties = Java.type('karate.infrastructure.aws.AwsProperties');
    const S3Client = Java.type('karate.infrastructure.aws.s3.S3Client');

    const awsProperties = AwsProperties.fromMap(karate.get('aws'));

    return new S3Client(awsProperties);
}