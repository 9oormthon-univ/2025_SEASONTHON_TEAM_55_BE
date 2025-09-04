package com.fingrow.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fingrowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fingrow API")
                        .description("Fingrow 핀테크 서비스 API 문서\n\n" +
                                "## 🏦 금융상품 API\n" +
                                "- 예금/적금 상품 조회 및 관리\n" +
                                "- 맞춤형 상품 추천 서비스\n" +
                                "- 금융감독원 공시 데이터 기반\n\n" +
                                "## 🔧 사용법\n" +
                                "1. 먼저 데이터 동기화 API를 호출하여 최신 상품 정보를 가져오세요\n" +
                                "2. 상품 조회 및 검색 기능을 이용하세요\n" +
                                "3. 목표 금액과 기간을 입력하여 맞춤 추천을 받으세요")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Fingrow Team")
                                .email("contact@fingrow.com")
                                .url("https://fingrow.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.fingrow.com")
                                .description("Production server")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 인증 토큰")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"));
    }
}