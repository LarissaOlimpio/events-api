package com.manager.events_api.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        //Users
                        .requestMatchers(HttpMethod.POST, "/api/user").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/user/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/user/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/user/**")
                        .hasRole("ADMIN")

                        // Events
                        .requestMatchers(HttpMethod.GET, "/api/event/**")
                        .authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/event/**")
                        .hasAnyRole("ADMIN", "ORGANIZER")

                        .requestMatchers(HttpMethod.PUT, "/api/event/**")
                        .hasAnyRole("ADMIN", "ORGANIZER")

                        .requestMatchers(HttpMethod.DELETE, "/api/event/**")
                        .hasAnyRole("ADMIN", "ORGANIZER")


                        // Coupons
                        .requestMatchers(HttpMethod.GET, "/api/coupon/**")
                        .authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/coupon/**")
                        .hasAnyRole("ADMIN", "ORGANIZER")

                        .requestMatchers(HttpMethod.PUT, "/api/coupon/**")
                        .hasAnyRole("ADMIN", "ORGANIZER")

                        .requestMatchers(HttpMethod.DELETE, "/api/coupon/**")
                        .hasAnyRole("ADMIN", "ORGANIZER")
                        .requestMatchers("/error").permitAll()
                        .anyRequest()
                        .authenticated()

                )
                .httpBasic(withDefaults());
        return http.build();
    }

}
