package com.utime.memoBom.common.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.utime.memoBom.common.vo.AppDefine;

@Configuration
public class CacheConfig {

//    @Bean
//    public CacheManager cacheManager() {
//    	
//        CaffeineCacheManager manager = new CaffeineCacheManager("userProfileImage");
//        
//        manager.setCaffeine(
//                Caffeine.newBuilder()
//                        .maximumSize(10_000)
//                        .expireAfterWrite(30, TimeUnit.MINUTES)
//                        .recordStats()
//        );
//        
//        
//        return manager;
//    }
//    
    @Bean
    public CacheManager cacheManager() {
        var nativeCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(20, TimeUnit.MINUTES)
                .build();

        var cm = new SimpleCacheManager();
        cm.setCaches(List.of(new CaffeineCache(AppDefine.KeyUserProfileImage, nativeCache)));
        return cm;
    }
}
