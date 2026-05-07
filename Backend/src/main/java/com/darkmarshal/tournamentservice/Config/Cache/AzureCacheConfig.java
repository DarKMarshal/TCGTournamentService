package com.darkmarshal.tournamentservice.Config.Cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Azure profile: uses Redis (Azure Cache for Redis) as a distributed cache.
 * All application instances share the same cache, ensuring consistency.
 */
@Configuration
@EnableCaching
@Profile("azure")
public class AzureCacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("events",
                        defaultConfig.entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("eventDetails",
                        defaultConfig.entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("leaderboards",
                        defaultConfig.entryTtl(Duration.ofMinutes(10)))
                .build();
    }
}
