package com.nhnacademy.inkbridge.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * class: GatewayConfig.
 *
 * @author jangjaehun
 * @version 2024/02/15
 */
@Configuration
public class GatewayConfig {

    @Value("${auth.url}")
    private String authUrl;

    /**
     * API 서버를 결정하는 메소드입니다.
     *
     * @param builder RouteLocatorBuilder
     * @return RouteLocator
     */
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("backend", r -> r.path("/api/**")
                .and()
                .uri("lb://INKBRIDGE-BACKEND"))
            .route("auth", r -> r.path("/auth/**")
                .uri(authUrl))
            .build();
    }
}
