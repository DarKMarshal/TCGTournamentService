package com.darkmarshal.tournamentservice.Config.Cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Local profile: uses a simple in-memory ConcurrentMapCacheManager.
 * Suitable for single-instance development; no external infrastructure needed.
 */
@Configuration
@EnableCaching
@Profile("local")
public class LocalCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("events", "eventDetails", "leaderboards");
    }
}
