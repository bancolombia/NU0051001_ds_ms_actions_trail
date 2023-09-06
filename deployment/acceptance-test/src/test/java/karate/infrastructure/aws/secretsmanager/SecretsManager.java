package karate.infrastructure.aws.secretsmanager;

import co.com.bancolombia.secretsmanager.api.GenericManager;
import co.com.bancolombia.secretsmanager.connector.AWSSecretManagerConnector;
import karate.infrastructure.aws.AwsProperties;
import karate.infrastructure.eventbus.RabbitMqProperties;
import lombok.SneakyThrows;

public class SecretsManager {

    private final GenericManager genericManager;

    public SecretsManager(AwsProperties properties) {
        this.genericManager = properties.hasEndpoint() ?
                new AWSSecretManagerConnector(properties.getRegion(), properties.getEndpoint()) :
                new AWSSecretManagerConnector(properties.getRegion());
    }

    @SneakyThrows
    public RabbitMqProperties buildRabbitMqProperties (String secretName) {
        return this.genericManager.getSecret(secretName, RabbitMqProperties.class);
    }
}
