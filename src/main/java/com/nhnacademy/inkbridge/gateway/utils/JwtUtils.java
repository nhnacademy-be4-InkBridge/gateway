package com.nhnacademy.inkbridge.gateway.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * class: JwtUtils.
 *
 * @author devminseo
 * @version 2/28/24
 */
@Component
@RequiredArgsConstructor
public class JwtUtils {
    @Value("${inkbridge.jwt.secret.key}")
    String secretKey;


    private Key key() {
        byte[] byteSecretKey = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String getUUID(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody()
                    .get("UUID", String.class);
        } catch (Exception e) {
            return null;
        }
    }

}
