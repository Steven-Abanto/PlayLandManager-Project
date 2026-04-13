package com.playlandpark.apigateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null || realmAccess.get("roles") == null) {
            return Collections.emptySet();
        }

        Object rolesObject = realmAccess.get("roles");
        if (!(rolesObject instanceof Collection<?> roles)) {
            return Collections.emptySet();
        }

        return roles.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(role -> !role.startsWith("default-roles-"))
                .filter(role -> !"offline_access".equals(role))
                .filter(role -> !"uma_authorization".equals(role))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}