package co.com.bancolombia.source.config;

import co.com.bancolombia.secretsmanager.api.GenericManager;
import co.com.bancolombia.secretsmanager.api.exceptions.SecretException;
import co.com.bancolombia.source.config.model.MongoDataConnection;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;


@Configuration
@EnableConfigurationProperties(PersistenceProperties.class)
public class MongoConfig  {
    private static final String USERNAME_TAG="<username>";
    private static final String PASSWORD_TAG="<password>";
    @Bean
    public MongoDataConnection getMongoSecret(@Value("${d2b.secrets.mongodb}") String secretName,
                                              GenericManager genericManager) throws SecretException {

        return genericManager.getSecret(secretName, MongoDataConnection.class);
    }

    @Bean
    @Primary
    public MongoProperties produceMongoDataConnection(@Qualifier("getMongoSecret") MongoDataConnection dataConnection) {
        var properties = new MongoProperties();
        properties.setUri(dataConnection.getHost());
        properties.setUsername(dataConnection.getUsername());
        properties.setPort(dataConnection.getPort());
        properties.setPassword(dataConnection.getPassword().toCharArray());
        properties.setDatabase(dataConnection.getDatabase());
        return properties;
    }

    @Bean
    public MongoClient produceMongoClient(@Qualifier("produceMongoDataConnection") MongoProperties mongoProperties) {
        return MongoClients.create(mongoProperties.getUri()
            .replace(USERNAME_TAG, mongoProperties.getUsername())
            .replace(PASSWORD_TAG, String.valueOf(mongoProperties.getPassword())));
    }

    @Bean
    public ReactiveMongoTemplate mongoTemplate(@Qualifier("produceMongoClient") MongoClient mongoClient,
                                               @Qualifier("produceMongoDataConnection") MongoProperties properties) {

        return new ReactiveMongoTemplate(mongoClient, properties.getDatabase());
    }

}