package org.example.genericcontroller.config;

import org.example.genericcontroller.support.caching.CacheDisabler;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfiguration {

//    private final ObjectFactory<CacheDisabler> cacheDisabler;
//
//    public CachingConfiguration(ObjectFactory<CacheDisabler> cacheDisabler) {
//        this.cacheDisabler = cacheDisabler;
//    }
//
//    @Bean
//    public CacheManager cacheManager() {
//        SimpleCacheManager cacheManager = new SimpleCacheManager();
//
//        Cache cache = new  CaffeineCache("name-of-the-cache", Caffeine.newBuilder()
//                .recordStats()
//                .expireAfterWrite(1, TimeUnit.HOURS)
//                .maximumSize(10_000)
//                .build());
//
//        cacheManager.setCaches(Collections.singletonList(new DeactivatableCache(cacheDisabler, cache, false));
//        return cacheManager;
//    }
}
