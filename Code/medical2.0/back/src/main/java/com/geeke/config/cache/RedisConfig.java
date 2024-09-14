package com.geeke.config.cache;

import java.lang.reflect.Method;
import java.time.Duration;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>redis缓存配置</p>
 * 
 */
@Configuration
@Data
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig extends CachingConfigurerSupport {
//
//	@Value("${spring.cache.type:redis}")
//	private String cacheType;		// 缓存类型
//
//    @Value("${spring.redis.host:61.172.179.73}")
//    private String host;
//
//    @Value("${spring.redis.port:5012}")
//    private String port;
//
//    @Value("${spring.redis.database:0}")
//    private int database;
//
//    @Value("${spring.redis.password:bW9Ivzt0CAQdoJTDZ2wAnduvi}")
//    private String password;
//
//    @Value("${spring.redis.timeout:3600}")
//    private int timeout;

    private  final  RedisProperties redisProperties;

    @Bean
    public KeyGenerator KeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @Bean
    CacheManager cacheManager(LettuceConnectionFactory connectionFactory) {
    	if(isRedisCache()) {
            //user信息缓存配置
            //RedisCacheConfiguration userCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)).disableCachingNullValues().prefixKeysWith("user");
            //product信息缓存配置
           // RedisCacheConfiguration productCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)).disableCachingNullValues().prefixKeysWith("product");
           // Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
           // redisCacheConfigurationMap.put("user", userCacheConfiguration);
            ///redisCacheConfigurationMap.put("product", productCacheConfiguration);
            //初始化一个RedisCacheWriter
            RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
            //设置CacheManager的值序列化方式为JdkSerializationRedisSerializer,但其实RedisCacheConfiguration默认就是使用StringRedisSerializer序列化key，JdkSerializationRedisSerializer序列化value,所以以下注释代码为默认实现
            //ClassLoader loader = this.getClass().getClassLoader();
            //JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(loader);
            //RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(jdkSerializer);
            //RedisCacheConfiguration defaultCacheConfig=RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);

            //设置默认超过期时间是30秒
            RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30));

            //初始化RedisCacheManager
            RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
            return cacheManager;
    	}

    	return new NoOpCacheManager();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }


	public boolean isRedisCache() {
		return "redis".equals(redisProperties.getCacheType());
	}


}