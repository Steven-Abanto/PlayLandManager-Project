package com.playlandpark.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()

                        // Auth público
                        .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/registro/cliente").permitAll()

                        // Catálogo público - productos
                        .pathMatchers(HttpMethod.GET, "/api/catalogo/productos").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/catalogo/productos/summary").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/catalogo/productos/type").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/catalogo/productos/*").permitAll()

                        // Catálogo público - promociones
                        .pathMatchers(HttpMethod.GET, "/api/catalogo/promociones").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/catalogo/promociones/active-today").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/catalogo/promociones/*").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/catalogo/promociones/codigo/*").permitAll()

                        // Catálogo privado - productos
                        .pathMatchers(HttpMethod.GET, "/api/catalogo/productos/sku/**").authenticated()
                        .pathMatchers(HttpMethod.GET, "/api/catalogo/productos/upc/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/catalogo/productos").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/catalogo/productos/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/catalogo/productos/delete/**").hasRole("ADMIN")

                        // Catálogo privado - promociones
                        .pathMatchers(HttpMethod.POST, "/api/catalogo/promociones").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/catalogo/promociones/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/catalogo/promociones/delete/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/catalogo/promociones/**").hasRole("ADMIN")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(
                                new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter)
                        )
                ))
                .build();
    }
}