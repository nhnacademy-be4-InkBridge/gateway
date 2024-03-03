package com.nhnacademy.inkbridge.gateway.config;

import com.nhnacademy.inkbridge.gateway.filter.JwtAuthorizationFilter;
import com.nhnacademy.inkbridge.gateway.utils.JwtUtils;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

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

    @Value("${backend.url}")
    private String backendUrl;

    /**
     * API 서버를 결정하는 메소드입니다.
     *
     * @param builder RouteLocatorBuilder
     * @return RouteLocator
     */
    @Bean
    public RouteLocator gatewayRoutes(JwtAuthorizationFilter authorizationFilter,
                                      RedisTemplate<String, Object> redisTemplate,
                                      JwtUtils jwtUtils, RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth", r -> r.path("/auth/**")
                        .uri(authUrl))
                .route("jwt", r -> r.path("/api/mypage/**","/api/admin/**","/api/auth/**")
                        .filters(jwtFilter(authorizationFilter, redisTemplate, jwtUtils))
                        .uri(backendUrl))
                .route("backend", r -> r.path("/api/**")
                        .and()
                        .uri(backendUrl))
                .build();
    }

    private Function<GatewayFilterSpec, UriSpec> jwtFilter(JwtAuthorizationFilter jwtAuthorizationFilter,
                                                           RedisTemplate<String, Object> redisTemplate,
                                                           JwtUtils jwtUtils) {
        return f -> f.filter(
                jwtAuthorizationFilter.apply(new JwtAuthorizationFilter.Config(redisTemplate, jwtUtils))
        );
    }
}
