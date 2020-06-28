package org.example.genericcontroller.support.caching;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;


//@Component
public class SimpleCacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    private final ObjectFactory<CacheDisabler> cacheDisabler;

    public SimpleCacheCustomizer(ObjectFactory<CacheDisabler> cacheDisabler) {
        this.cacheDisabler = cacheDisabler;
    }

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {

    }
}
