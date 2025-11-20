package org.nishgrid.clienterp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(AbstractHttpConfigurer::disable)


                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/**", "/h2-console/**","/api/photo/**","/updates/**").permitAll()
                        .requestMatchers("/static/**", "/css/**", "/js/**").permitAll()

                        .anyRequest().authenticated()
                );


        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}














