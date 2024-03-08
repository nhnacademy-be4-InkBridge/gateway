package com.nhnacademy.inkbridge.gateway.filter;

import com.nhnacademy.inkbridge.gateway.utils.JwtUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * class: JwtAuthorizationFilter.
 *
 * @author devminseo
 * @version 2/28/24
 */
@Component
@Slf4j
public class JwtAuthorizationFilter extends AbstractGatewayFilterFactory<JwtAuthorizationFilter.Config> {
    private static final String MEMBER_ID = "member_id";

    /**
     * 필요한 설정 클래스 .
     */
    @RequiredArgsConstructor
    public static class Config {
        private final RedisTemplate<String, Object> redisTemplate;
        private final JwtUtils jwtUtils;
    }

    /**
     * 인증 필터 생성.
     */
    public JwtAuthorizationFilter() {
        super(Config.class);
    }

    /**
     * 1. Authorization 헤더가 없을때.
     * 2. 식별자 Bearer 이 존재하지 않을때.
     * 3. 시크릿키를 통한 토큰 검증
     * 4. 토큰에서 uuid 추출후 redis 에 멤버 아이디가 없을때.
     * 최종. header 에 멤버 아이디값 넣어서 보낸다.
     * @param config config
     * @return 인가된 요청
     */

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String accessToken = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            log.info("accessToken -> {}", accessToken);

            if (Objects.isNull(accessToken)) {
                return unAuthorizedHandle(exchange);
            }

            accessToken = prefixRemoveBearer(accessToken);

            if (Objects.isNull(accessToken)) {
                return unAuthorizedHandle(exchange);
            }

            String uuid = config.jwtUtils.getUUID(accessToken);

            if (isMemberId(config, uuid)) {
                return unAuthorizedHandle(exchange);
            }
            String memberId = String.valueOf(config.redisTemplate.opsForHash().get(uuid, MEMBER_ID));

            setAuthorizationHeader(exchange,memberId);

            return chain.filter(exchange);
        });
    }

    /**
     * 멤버 아이디를 커스텀 헤더에 추가해서 보내줌.
     * @param exchange exchange
     * @param memberId 사용자 식별 아이디
     */
    private static void setAuthorizationHeader(ServerWebExchange exchange,String memberId) {
        exchange.getRequest()
                .mutate()
                .header("Authorization-Id", memberId)
                .build();
    }

    /**
     * redis 에 memberId 있는지 uuid 통해 확인
     * @param config config
     * @param uuid uuid
     * @return 결과값
     */
    private static boolean isMemberId(Config config, String uuid) {
        return Objects.isNull(config.redisTemplate.opsForHash().get(uuid, MEMBER_ID));
    }

    private static String prefixRemoveBearer(String accessToken) {
        if (!accessToken.startsWith("Bearer ")) {
            return null;
        }
        return accessToken.substring(7);
    }

    /**
     * 인가되지 않은 사용자일 경우 에러 처리.
     *
     * @param exchange 서버 요청 접근
     * @return 401 에러
     */
    private Mono<Void> unAuthorizedHandle(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        return response.setComplete();
    }


}
