package com.arpit.spring.cache.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.cluster.ClusterManager;
import com.couchbase.client.spring.cache.CacheBuilder;
import com.couchbase.client.spring.cache.CouchbaseCacheManager;

@Configuration
@EnableCaching
@EnableScheduling
@PropertySource(value = { "classpath:/cache.properties" })
@ComponentScan(basePackages = { "com.arpit.spring.cache.controller",
		"com.arpit.spring.cache.service" })
public class ApplicationConfig {

	@Value("#{'${couchbase.cluster.host}'}")
	private String couchbaseClusterHost;

	@Value("#{'${couchbase.bucket.default}'}")
	private String couchbaseBucketDefault;

	@Value("#{'${couchbase.bucket.default.username}'}")
	private String couchbaseBucketDefaultUsername;

	@Value("#{'${couchbase.bucket.default.password}'}")
	private String couchbaseBucketDefaultPassword;

	@Value("#{'${couchbase.cache}'}")
	private String couchbaseCache;

	@Value("#{'${couchbase.cache.flush}'}")
	private String couchbaseCacheFlush;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean(destroyMethod = "disconnect")
	public Cluster cluster() {
		final List<String> nodes = Arrays.asList(couchbaseClusterHost
				.split(","));
		return CouchbaseCluster.create(nodes);
	}

	@Bean(destroyMethod = "close")
	public Bucket bucket() {
		return cluster().openBucket(couchbaseBucketDefault,
				couchbaseBucketDefaultPassword);
	}

	@CacheEvict(allEntries = true, cacheResolver = "customCacheResolver")
	@Scheduled(fixedDelayString = "${couchbase.cache.flush.fixed.delay}")
	public void cacheEvict() {
	}

	@Bean
	public ClusterManager clusterManager() {
		return cluster().clusterManager(couchbaseBucketDefaultUsername,
				couchbaseBucketDefaultPassword);
	}

	@Bean
	public CacheManager cacheManager() {
		final Map<String, CacheBuilder> cache = new HashMap<>();
		for (final String appCache : couchbaseCache.split(",")) {
			cache.put(appCache, CacheBuilder.newInstance(bucket()));
		}
		return new CouchbaseCacheManager(cache);
	}

	@Bean(name = "customCacheResolver")
	public CacheResolver cacheResolver() {
		CacheResolver cacheResolver = new AbstractCacheResolver(cacheManager()) {
			@Override
			protected Collection<String> getCacheNames(
					CacheOperationInvocationContext<?> context) {
				return Arrays.asList(couchbaseCacheFlush.split(","));
			}
		};
		return cacheResolver;
	}

}
