package icelik.quota.apigw;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class BlockedCacheConfig {
	@Bean
	@NonNull
	Cache<String, Long> blockedCache() {

		return Caffeine.newBuilder().expireAfter(new Expiry<String, Long>() {
			@Override
			public long expireAfterCreate(@NonNull String key, @NonNull Long value, long currentTime) {

				long l = value - System.currentTimeMillis();

				if (l < 1)
					l = 0;

				return TimeUnit.MILLISECONDS.toNanos(l);
			}

			@Override
			public long expireAfterUpdate(@NonNull String key, @NonNull Long value, long currentTime, @NonNegative long currentDuration) {
				return currentDuration;
			}

			@Override
			public long expireAfterRead(@NonNull String key, @NonNull Long value, long currentTime, @NonNegative long currentDuration) {
				return currentDuration;
			}
		}).build();
	}
}
