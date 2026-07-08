package com.techsolutions.incidencias.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Habilitar CORS explícitamente en la cadena de seguridad
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 2. Deshabilitar CSRF
            .csrf(csrf -> csrf.disable())
            // 3. Configurar permisos de rutas
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/incidencias/**","/api/").permitAll() // Permitimos login y las rutas que creamos
                .requestMatchers("/todas", "/tecnicos", "/asignar").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    // 4. Configuración global de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // Permitir Angular
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Métodos permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Cabeceras permitidas
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplicar a todas las rutas
        return source;
    }
}