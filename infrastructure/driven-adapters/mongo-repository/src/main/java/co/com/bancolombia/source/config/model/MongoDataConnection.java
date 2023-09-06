package co.com.bancolombia.source.config.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class MongoDataConnection {

    private String host;
    private Integer port;
    private String database;
    private String username;
    private String password;

}