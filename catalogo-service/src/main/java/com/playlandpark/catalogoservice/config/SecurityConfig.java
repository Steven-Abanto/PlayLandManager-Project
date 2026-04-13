package com.playlandpark.catalogoservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()

                        // Productos públicos
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/productos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/productos/summary").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/productos/type").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/productos/*").permitAll()

                        // Promociones públicas
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/promociones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/promociones/active-today").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/promociones/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/promociones/codigo/*").permitAll()

                        // Privados productos
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/productos/sku/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/productos/upc/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/catalogo/productos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/catalogo/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/catalogo/productos/delete/**").hasRole("ADMIN")

                        // Privados promociones
                        .requestMatchers(HttpMethod.POST, "/api/catalogo/promociones").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/catalogo/promociones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/catalogo/promociones/delete/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/catalogo/promociones/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }
}