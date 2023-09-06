package karate.infrastructure.eventbus;

import lombok.Data;

@Data
public class RabbitMqProperties {
    String virtualhost;
    String hostname;
    String password;
    String username;
    boolean ssl;
    Integer port;
}
