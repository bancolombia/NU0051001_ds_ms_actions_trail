function fn(secretName) {
    const AwsProperties = Java.type('karate.infrastructure.aws.AwsProperties');
    const SecretsManager = Java.type('karate.infrastructure.aws.secretsmanager.SecretsManager');
    const EventConsumer = Java.type('karate.infrastructure.eventbus.ManagementEvents');

    const awsProperties = AwsProperties.fromMap(karate.get('aws'));
    const secretsManager = new SecretsManager(awsProperties);
    const rabbitMqProperties = secretsManager.buildRabbitMqProperties(secretName);

    return new EventConsumer(rabbitMqProperties);
}
