package com.gmg.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.gmg.api.type.CacheType;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 기본 TTL (10분)
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        // 캐시 이름별 설정
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        cacheConfigs.put("meeting:detail", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1)));
        cacheConfigs.put("user:profile", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)));
        cacheConfigs.put("statistics", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)      // 기본값
                .withInitialCacheConfigurations(cacheConfigs) // 캐시별 TTL
                .build();
    }

    @Bean(name = "localCacheManager")
    public CacheManager localCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = Arrays.stream(CacheType.values())
                .map(
                        cache -> new CaffeineCache(cache.getCacheName(),
                                Caffeine.newBuilder()
                                        .expireAfterWrite(cache.getExpiredAfterWrite(), TimeUnit.SECONDS)
                                        .maximumSize(cache.getMaximumSize())
                                        .build()
                        )
                )
                .collect(Collectors.toList());
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}




