package com.playlandpark.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String serverUrl;
    private String realm;

    private Admin admin = new Admin();
    private Login login = new Login();

    @Getter
    @Setter
    public static class Admin {
        private String username;
        private String password;
        private String clientId;
    }

    @Getter
    @Setter
    public static class Login {
        private String clientId;
        private String clientSecret;
    }
}