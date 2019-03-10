package icelik.quota.apigw.kafka.listener;


import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class BlockedTopicListener {
	private final Cache<String, Long> blockedCache;

	public BlockedTopicListener(Cache<String, Long> blockedCache) {
		this.blockedCache = blockedCache;
	}

	@KafkaListener(topics = "blocked",
			groupId = "#{T(java.util.UUID).randomUUID().toString()}")
	public void consumeBlocked(
			@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
			Long value) {
		blockedCache.put(key, value);
	}

}
