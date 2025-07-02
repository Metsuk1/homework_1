package com.example.homework_1.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Configuration {
    private String host;
    private int port;

    //setHost() and  setPort() return Object Configuration and we can call these in one(same) chain
    Configuration configuration = new Configuration()
            .setHost("localhost")
            .setPort(8080);

    /*
    Without @Accessors(chain = true) it will be
     */

    public Configuration  Without_Accessors() {
        /*
        Each call is separate because setters return void
         */
        configuration.setHost("localhost");
        configuration.setPort(8080);

        return this;
    }
}
