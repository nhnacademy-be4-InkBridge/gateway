package com.nhnacademy.inkbridge.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * class: RedisConfig.
 *
 * @author devminseo
 * @version 2/28/24
 */
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${inkbridge.redis.host}")
    private String host;
    @Value("${inkbridge.redis.port}")
    private String port;
    @Value("${inkbridge.redis.password}")
    private String password;
    @Value("${inkbridge.redis.database}")
    private String database;

    /**
     * redis 연결을 위한 빈 메서드.
     * @return 레디스 설정 팩토리
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(Integer.parseInt(port));
        configuration.setPassword(password);
        configuration.setDatabase(Integer.parseInt(database));

        return new LettuceConnectionFactory(configuration);
    }

    /**
     * redis 모듈을 사용하기 위한 설정 메서드.
     * @return redis crud 가능하게 하는 객체
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class)); // JSON 직렬화 사용
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class)); // JSON 직렬화 사용

        return redisTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


}
