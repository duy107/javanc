package com.javanc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@PropertySource("classpath:application.yml")
public class WebSecurityConfig {

    @Value("${jwt.sign_key}")
    private String SIGN_KEY;
    private final String[] PUBLIC_ENPOINTS = {"/users", "/auth/login", "/auth/register", "/auth/refreshToken", "/auth/logout", "/api/upload", "/auth/sendEmail", "/api/otp/send", "/api/otp/verify"};
    private CustomJwtDecoder customerJwtDecoder;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:3000"));
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/cities").permitAll()
                        .anyRequest().authenticated()
                );

        //Kích hoạt OAuth2 Resource Server sử dụng JWT
        //Xác thực bằng JWT Bearer token gửi qua header Authorization
        //Dùng custom JwtDecoder để xác minh chữ ký token.
        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customerJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new AuthenticationEntryPointConfig())
                );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
