package com.lt.boot.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @description: JWT 工具类
 * @author: ~Teng~
 * @date: 2024/1/27 14:45
 */
public class JwtUtils {
    /**
     * Token 请求头
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * TOKEN的有效期 2个小时 2h _表示支持的语法 可读性更强
     */
    private static final int TOKEN_TIME_OUT = 2 * 3_600;

    /**
     * 加密KEY
     */
    private static final String TOKEN_ENCRYPTION_KEY = "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY";

    /**
     * 最小刷新间隔(S)
     */
    private static final int REFRESH_TIME = 300;

    /**
     * 生成token
     *
     * @param id 用户id
     * @return token
     */
    public static String getToken(Long id) {
        String jti = UUID.randomUUID().toString().replace("-", "");
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                // JWT ID
                .id(jti)
                // 签发时间
                .issuedAt(new Date(currentTime))
                // 用户ID作为主体
                .subject(String.valueOf(id))
                // 签发者信息
                .issuer("muziteng")
                // 接收用户
                .audience().add("boot").and()
                // 数据压缩方式
                .compressWith(Jwts.ZIP.GZIP)
                // 加密方式 加密算法，密钥
                .signWith(generalKey(), Jwts.SIG.HS256)
                // 过期时间戳
                .expiration(new Date(currentTime + TOKEN_TIME_OUT * 1000))
                .compact();
    }

    /**
     * 获取 token 中的 claims 信息
     */
    private static Jws<Claims> getJws(String token) {
        return Jwts.parser()
                .verifyWith(generalKey())
                .build()
                .parseSignedClaims(token);
    }

    /**
     * 获取 payload body信息
     */
    public static Claims getClaimsBody(String token) {
        try {
            return getJws(token).getPayload();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    /**
     * 获取 token 签发时间
     *
     * @param token JWT token
     * @return 签发时间，解析失败返回 null
     */
    public static Date getIssuedAt(String token) {
        try {
            return getJws(token).getPayload().getIssuedAt();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取 header body信息
     */
    public static JwsHeader getHeaderBody(String token) {
        return getJws(token).getHeader();
    }

    /**
     * 是否过期
     *
     * @return -1：有效，0：有效，1：过期，2：过期
     */
    public static int verifyToken(Claims claims) {
        if (claims == null) {
            return 1;
        }
        try {
            claims.getExpiration().before(new Date());
            // 需要自动刷新TOKEN
            if ((claims.getExpiration().getTime() - System.currentTimeMillis()) > REFRESH_TIME * 1000) {
                return -1;
            } else {
                return 0;
            }
        } catch (ExpiredJwtException ex) {
            return 1;
        } catch (Exception e) {
            return 2;
        }
    }

    /**
     * 由字符串生成加密 key
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getEncoder().encode(TOKEN_ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8));
        // Use HS256 compatible key
        return Keys.hmacShaKeyFor(encodedKey);
    }

    public static void main(String[] args) {
        // 生成token
        String token = JwtUtils.getToken(1L);
        System.out.println(token);
        try {
            // 获取 payload 信息
            Claims claimsBody = JwtUtils.getClaimsBody(token);
            // 判断是否过期
            int res = JwtUtils.verifyToken(claimsBody);
            if (res < 1) {
                String subject = claimsBody.getSubject();
                System.out.println("token 解析成功，userId = " + subject);
            } else {
                System.out.println("token 已过期");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("token 解析失败");
        }
    }
}
