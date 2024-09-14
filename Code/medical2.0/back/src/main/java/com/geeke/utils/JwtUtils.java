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
	
	@Value(value = "${jwtUtils.secret:6686df7fc3a34e26a61c034d5ec82488}")
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
			// long expMillis = nowMillis + ttlMillis;
			// Date exp = new Date(expMillis);
			// builder.setExpiration(exp);
			
			SecurityUtils.getSubject().getSession().setTimeout(ttlMillis);  // session到期时间
		}
		return builder.compact();
	}

	/**
	 * 解密jwt
	 * 
	 * @param jwt
	 * @return
	 * @throws Exception
	 */
	public Claims parseJWT(String jwt) {
		SecretKey key = generalKey();
		Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(key) // 设置标识名
                    .parseClaimsJws(jwt)  //解析token
                    .getBody();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }
		
		return claims;
	}

	public long getTtlMillis() {
		return ttlMillis;
	}

}
