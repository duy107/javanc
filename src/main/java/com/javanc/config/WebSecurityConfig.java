package com.javanc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.http.client.HttpClientAutoConfiguration;
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

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@PropertySource("classpath:application.yml")
@EnableAutoConfiguration(exclude = {
        HttpClientAutoConfiguration.class,
        org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration.class
})
public class WebSecurityConfig {

    @Value("${jwt.sign_key}")
    private String SIGN_KEY;
    private final String[] PUBLIC_ENPOINTS = {"/users", "/auth/login", "/auth/register", "/auth/social-login", "/auth/refreshToken", "/auth/logout", "/api/upload", "/auth/sendEmail", "/api/otp/send", "/api/otp/verify"};
    private CustomJwtDecoder customerJwtDecoder;
    private final String apiPrefix = "/api/admin";
    private final String userApiPrefix = "/api";

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
//                .oauth2Login(Customizer.withDefaults())
                .authorizeHttpRequests(request ->

                request.requestMatchers(HttpMethod.POST, PUBLIC_ENPOINTS).permitAll()

                // ADMIN
                        .requestMatchers(HttpMethod.GET, "/auth/profile").authenticated()

                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/cities").permitAll()

                        // account
                        .requestMatchers(String.format("%s/accounts/**", apiPrefix)).authenticated()
                        //role
                        .requestMatchers(String.format("%s/roles/**", apiPrefix)).authenticated()

                        // category
                        .requestMatchers(GET ,String.format("%s/categories/**", apiPrefix)).permitAll()
                        .requestMatchers(String.format("%s/categories/**", apiPrefix)).authenticated()

                        // product
                        .requestMatchers(String.format("%s/products/**", apiPrefix)).authenticated()


                        // discount
                        .requestMatchers(GET, String.format("%s/discounts/**", apiPrefix)).hasRole("ADMIN")
                        .requestMatchers(POST, String.format("%s/discounts/**", apiPrefix)).hasRole("ADMIN")
                        .requestMatchers(PATCH, String.format("%s/discounts/**", apiPrefix)).hasRole("ADMIN")
                        .requestMatchers(DELETE, String.format("%s/discounts/**", apiPrefix)).hasRole("ADMIN")


                        // common
                        .requestMatchers(GET, String.format("%s/common/colors/**", userApiPrefix)).permitAll()
                        .requestMatchers(GET, String.format("%s/common/sizes/**", userApiPrefix)).permitAll()
                        .requestMatchers(GET, String.format("%s/common/categories/**", userApiPrefix)).permitAll()
                        .requestMatchers(GET, String.format("%s/common/products/**", apiPrefix)).permitAll()


                        // USER
                        //product
                        .requestMatchers(GET, String.format("%s/products/**", userApiPrefix)).permitAll()

                        //shopping cart
                        .requestMatchers(POST, String.format("%s/shopping-cart/**", userApiPrefix)).permitAll()

                        //
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
