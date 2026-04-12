package com.playlandpark.authservice.integration.keycloak.dto;

public record KeycloakUserRequest(
        String username,
        String password,
        String email,
        String firstName,
        String lastName,
        String realmRole
) {}