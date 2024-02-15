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

    @Value("${backend.url}")
    private String backendUrl;

    @Value("${auth.url}")
    private String authUrl;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("backend_route", r -> r.path("/api/**")
                .uri(backendUrl))
            .route("auth_route", r -> r.path("/auth/**")
                .uri(authUrl))
            .build();
    }
}
