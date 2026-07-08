package com.geeke.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Jwt工具类
 * 
 * @author Lining
 * @date 2017/11/2
 */
@Component
public class JwtUtils {

	@Value(value = "${jwtUtils.id:jwt}")
	private String id;
	
	@Value(value = "${jwtUtils.secret}")
	private String secret;
	
	/**
	 * 存活时间（毫秒）
	 */
	@Value(value = "${jwtUtils.ttlMillis:3600000}")
	private long ttlMillis;
	

	/**
	 * 生成加密key
	 * 
	 * @return
	 */
	private SecretKey generalKey() {
		if (secret == null || secret.trim().isEmpty()) {
			throw new IllegalStateException("JWT secret is not configured. Set 'jwtUtils.secret' in application config.");
		}
		byte[] encodedKey = Base64.getDecoder().decode(secret);
		SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
		return key;
	}

	/**
	 * 创建jwt
	 * 
	 * @param subject
	 * @return
	 */
	public String createJWT(String subject) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		SecretKey key = generalKey();
		JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).signWith(signatureAlgorithm,
				key);
		if (ttlMillis >= 0) {
			long expMillis = nowMillis + ttlMillis;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);

			SecurityUtils.getSubject().getSession().setTimeout(ttlMillis);  // session到期时间
		}
		return builder.compact();
	}

	/**
	 * 解密jwt
	 *
	 * @param jwt
	 * @return
	 * @throws ExpiredJwtException token过期时抛出
	 * @throws io.jsonwebtoken.SignatureException 签名验证失败时抛出
	 */
	public Claims parseJWT(String jwt) {
		SecretKey key = generalKey();
		// 不再吞掉 ExpiredJwtException — 过期token应被视为无效
		Claims claims = Jwts.parser()
				.setSigningKey(key)
				.parseClaimsJws(jwt)
				.getBody();
		return claims;
	}

	/**
	 * 解密jwt，过期时返回null而非抛出异常
	 * 用于需要静默处理过期token的场景（如从过期token中提取sessionId做清理）
	 *
	 * @param jwt
	 * @return claims，过期时返回null
	 */
	public Claims parseJWTLenient(String jwt) {
		SecretKey key = generalKey();
		try {
			return Jwts.parser()
					.setSigningKey(key)
					.parseClaimsJws(jwt)
					.getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	public long getTtlMillis() {
		return ttlMillis;
	}

}
