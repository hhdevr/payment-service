package com.iprody.paymentserviceapp.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.oauth2.jwt.JwtValidators.createDefaultWithIssuer;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());

        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm ->
                                       sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth ->
                                           auth.requestMatchers("/payments/**")
                                               .hasRole("user")
                                               .anyRequest()
                                               .authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                                          oauth2.jwt(jwt ->
                                                             jwt.decoder(jwtDecoder())
                                                                .jwtAuthenticationConverter(jwtConverter))
            );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri("http://keycloak:8080/realms/iprody-lms/protocol/openid-connect/certs")
                .build();

        OAuth2TokenValidator<Jwt> withIssuer = createDefaultWithIssuer("http://localhost:8085/realms/iprody-lms");
        decoder.setJwtValidator(withIssuer);

        return decoder;
    }
}