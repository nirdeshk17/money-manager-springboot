package com.alienCoders.moneymanger.config;

import com.alienCoders.moneymanger.security.JwtRequestFilter;
import com.alienCoders.moneymanger.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration  // tells Spring this is a config class (loaded at startup)
@RequiredArgsConstructor


public class SecurityConfig {
    final AppUserDetailsService appUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;


    @Bean  // makes this method return value available in Spring (like a ready-to-use object stored in spring memory)
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // allow frontend (React, Angular, etc.) to talk to backend because they may be running in different port
                .cors(Customizer.withDefaults())

                // turn off CSRF (needed for REST APIs, otherwise it blocks requests because we are using jwt(json web token) token in every request)
                .csrf(AbstractHttpConfigurer::disable)

                // decide which URLs are public and which need login
                .authorizeHttpRequests(auth -> auth
                        // no login required for these
                        .requestMatchers("/status", "/health", "/register", "/login","/activate","/categories").permitAll()
                        // everything else requires authentication
                        .anyRequest().authenticated()
                )

                // don’t keep any session on the server (REST = stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // finally, apply these security rules
        return httpSecurity.build();
    }

    @Bean
    //Encrypts passwords before saving in DB.
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        // allow requests from all origins (frontend apps, mobile apps, etc.)
        configuration.setAllowedOriginPatterns(List.of("*"));

        // allow these HTTP methods
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));

        // allow sending cookies / tokens with requests
        configuration.setAllowCredentials(true);

        // apply this CORS config to all paths (/** means every URL)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // This method defines a custom AuthenticationManager bean that tells Spring Security
// how to authenticate a user (i.e., where to load user details from and how to verify passwords).

    @Bean
    public AuthenticationManager authenticationManager() {

        // DaoAuthenticationProvider is a built-in Spring Security provider
        // that uses a UserDetailsService and a PasswordEncoder to authenticate users.
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        // Here we set our custom implementation of UserDetailsService.
        // This service is responsible for loading user details (username, password, roles)
        // from the database or any other source during authentication.
        authenticationProvider.setUserDetailsService(appUserDetailsService);

        // We also set the password encoder (e.g., BCryptPasswordEncoder)
        // so Spring Security can verify hashed passwords securely.
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        // Finally, we create and return an AuthenticationManager.
        // This manager delegates the authentication process to our configured provider(s).
        // When a login request is made, Spring Security uses this manager to validate credentials.
        return new ProviderManager(authenticationProvider);
    }


}
