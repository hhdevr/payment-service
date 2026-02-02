package com.iprody.paymentserviceapp;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public class TestJwtFactory {

    public static RequestPostProcessor jwtWithRoles(String username, String... roles) {
        List<GrantedAuthority> authorities = Arrays.stream(roles)
                                                   .map(role -> "ROLE_" + role.toUpperCase())
                                                   .map(SimpleGrantedAuthority::new)
                                                   .collect(Collectors.toList());

        return jwt()
                .jwt(jwt -> {
                    jwt.subject(username);
                    jwt.claim("preferred_username", username);
                    jwt.claim("scope", "read");

                })
                .authorities(authorities);
    }
}