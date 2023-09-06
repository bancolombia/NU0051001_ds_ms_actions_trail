package co.com.bancolombia.source.config.model;

import co.com.bancolombia.secretsmanager.api.GenericManager;
import co.com.bancolombia.secretsmanager.api.exceptions.SecretException;
import co.com.bancolombia.source.config.MongoConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MongoConfigTest {

    private static final String SECRET_NAME = "secretName";
    private static final String URI_CONNECTION = "mongodb://";

    private MongoConfig mongoConfig;
    private MongoDataConnection mongoDataConnection;

    @Mock
    GenericManager genericManager;

    @BeforeEach
    void setUp() {
        mongoConfig = new MongoConfig();

        mongoDataConnection = new MongoDataConnection();
        mongoDataConnection.setHost(URI_CONNECTION+"myHost");
        mongoDataConnection.setPort(27017);
        mongoDataConnection.setUsername("myUsername");
        mongoDataConnection.setPassword("myPassword");
        mongoDataConnection.setDatabase("anyDatabase");
    }

    @Test
    void shouldGetMongoSecretFromBean() throws SecretException {

        when(genericManager.getSecret(anyString(), any())).thenReturn(mongoDataConnection);

        var connectionCurrent = mongoConfig.getMongoSecret(SECRET_NAME, genericManager);

        assertNotNull(connectionCurrent);
        assertEquals(mongoDataConnection, connectionCurrent);
    }

    @Test
    void shouldGetMongoDataConnectionFromBean() {

        var mongoProperties = mongoConfig.produceMongoDataConnection(mongoDataConnection);
        assertNotNull(mongoProperties);
        assertEquals(mongoDataConnection.getUsername(), mongoProperties.getUsername());
        assertEquals(mongoDataConnection.getHost(), mongoProperties.getUri());
        assertEquals(mongoDataConnection.getDatabase(), mongoProperties.getDatabase());
        assertEquals(mongoDataConnection.getPort(), mongoProperties.getPort());
        assertEquals(mongoDataConnection.getPassword(), String.valueOf(mongoProperties.getPassword()));
    }

    @Test
    void shouldGetMongoClientFromBean() {

        var mongoProperties = mongoConfig.produceMongoDataConnection(mongoDataConnection);

        var mongoClient = mongoConfig.produceMongoClient(mongoProperties);
        var serverAddress = mongoClient.getClusterDescription().getClusterSettings().getHosts().get(0);

        assertNotNull(mongoClient);
        assertEquals(mongoDataConnection.getHost().toLowerCase(), URI_CONNECTION+serverAddress.getHost());
        assertEquals(mongoDataConnection.getPort(), serverAddress.getPort());
    }

    @Test
    void shouldGetMongoTemplateFromBean() {

        var mongoProperties = mongoConfig.produceMongoDataConnection(mongoDataConnection);
        var mongoClient = mongoConfig.produceMongoClient(mongoProperties);

        var mongoTemplate = mongoConfig.mongoTemplate(mongoClient, mongoProperties);

        assertNotNull(mongoTemplate);
    }

}
