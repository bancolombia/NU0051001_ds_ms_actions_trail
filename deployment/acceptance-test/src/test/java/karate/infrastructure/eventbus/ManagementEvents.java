package karate.infrastructure.eventbus;

import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Map;

@RequiredArgsConstructor
public class ManagementEvents {
    private final RabbitMqProperties rabbitMqProperties;

    @SneakyThrows
    public void listen(String exchangeName, String eventName, java.util.function.Consumer<String> handler) {
        var connectionFactory = buildConnectionFactory();

        var connection = connectionFactory.newConnection();
        var channel = connection.createChannel();

        var declareOk = channel.queueDeclare();
        channel.queueBind(declareOk.getQueue(), exchangeName, eventName);

        channel.basicConsume(
                declareOk.getQueue(),
                true,
                (consumerTag, message) -> {
                    handler.accept(new String(message.getBody()));
                    connection.close();
                },
                consumerTag -> {
                });
    }

    @SneakyThrows
    private ConnectionFactory buildConnectionFactory() {
        var factory = new ConnectionFactory();
        factory.setHost(rabbitMqProperties.getHostname());
        factory.setPort(rabbitMqProperties.getPort());
        factory.setUsername(rabbitMqProperties.getUsername());
        factory.setPassword(rabbitMqProperties.getPassword());
        if (rabbitMqProperties.isSsl()) {
            factory.useSslProtocol();
        }
        return factory;
    }
}
