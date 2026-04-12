package com.playlandpark.authservice.integration.keycloak;

import com.playlandpark.authservice.integration.keycloak.dto.KeycloakUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.create();

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.admin.client-id}")
    private String adminClientId;

    public void createUser(KeycloakUserRequest request) {
        String adminToken = getAdminToken();

        Integer roleId = null;
        String roleName = request.realmRole();

        String userPayload = """
                {
                  "username": "%s",
                  "email": "%s",
                  "enabled": true,
                  "firstName": "%s",
                  "lastName": "%s",
                  "credentials": [
                    {
                      "type": "password",
                      "value": "%s",
                      "temporary": false
                    }
                  ]
                }
                """.formatted(
                escape(request.username()),
                escape(request.email()),
                escape(request.firstName()),
                escape(request.lastName()),
                escape(request.password())
        );

        var response = restClient.post()
                .uri(serverUrl + "/admin/realms/" + realm + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .body(userPayload)
                .retrieve()
                .toBodilessEntity();

        if (!response.getStatusCode().is2xxSuccessful() && response.getStatusCode().value() != 201) {
            throw new IllegalStateException("No se pudo crear usuario en Keycloak.");
        }

        String userId = findUserIdByUsername(request.username(), adminToken);
        assignRealmRole(userId, roleName, adminToken);
    }

    public void deleteUserIfExists(String username) {
        String adminToken = getAdminToken();
        String userId = findUserIdByUsernameOrNull(username, adminToken);
        if (userId == null) return;

        restClient.delete()
                .uri(serverUrl + "/admin/realms/" + realm + "/users/" + userId)
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .toBodilessEntity();
    }

    private String getAdminToken() {
        String body = "grant_type=password" +
                "&client_id=" + adminClientId +
                "&username=" + adminUsername +
                "&password=" + adminPassword;

        String response = restClient.post()
                .uri(serverUrl + "/realms/master/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(String.class);

        try {
            JsonNode json = objectMapper.readTree(response);
            return json.get("access_token").asText();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo obtener token admin de Keycloak.");
        }
    }

    private String findUserIdByUsername(String username, String adminToken) {
        String userId = findUserIdByUsernameOrNull(username, adminToken);
        if (userId == null) {
            throw new IllegalStateException("Usuario no encontrado en Keycloak después de crearlo: " + username);
        }
        return userId;
    }

    private String findUserIdByUsernameOrNull(String username, String adminToken) {
        String response = restClient.get()
                .uri(serverUrl + "/admin/realms/" + realm + "/users?username=" + username + "&exact=true")
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .body(String.class);

        try {
            JsonNode arr = objectMapper.readTree(response);
            if (!arr.isArray() || arr.isEmpty()) {
                return null;
            }
            return arr.get(0).get("id").asText();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo consultar usuario en Keycloak.");
        }
    }

    private void assignRealmRole(String userId, String roleName, String adminToken) {
        String roleResponse = restClient.get()
                .uri(serverUrl + "/admin/realms/" + realm + "/roles/" + roleName)
                .header("Authorization", "Bearer " + adminToken)
                .retrieve()
                .body(String.class);

        try {
            JsonNode role = objectMapper.readTree(roleResponse);

            List<Map<String, Object>> payload = List.of(
                    Map.of(
                            "id", role.get("id").asText(),
                            "name", role.get("name").asText()
                    )
            );

            restClient.post()
                    .uri(serverUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminToken)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception e) {
            throw new IllegalStateException("No se pudo asignar rol de Keycloak: " + roleName);
        }
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }
}