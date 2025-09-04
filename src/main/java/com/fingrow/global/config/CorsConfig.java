package com.fingrow.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("HEAD", "POST", "GET", "DELETE", "PUT", "PATCH", "OPTIONS")
                .allowedHeaders(
                        "Origin",
                        "Content-Type",
                        "Accept",
                        "Authorization",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Headers",
                        "Access-Control-Allow-Methods",
                        "X-Requested-With",
                        "X-Auth-Token",
                        "Cache-Control"
                )
                .exposedHeaders(
                        "Authorization",
                        "Content-Type",
                        "X-Total-Count"
                )
                .allowCredentials(true)
                .maxAge(3600); // 1시간 동안 preflight 결과 캐시
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Flutter 안드로이드 앱을 위한 설정
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Origin",
            "Content-Type", 
            "Accept",
            "Authorization",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "Access-Control-Allow-Methods",
            "X-Requested-With",
            "X-Auth-Token",
            "Cache-Control"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count"
        ));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L); // 1시간 동안 preflight 결과 캐시

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}