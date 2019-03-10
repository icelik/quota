package icelik.quota.apigw.kafka.listener;


import com.github.benmanes.caffeine.cache.Cache;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BlockedTopicListener {
	private final Cache<String, Long> blockedCache;

	public BlockedTopicListener(Cache<String, Long> blockedCache) {
		this.blockedCache = blockedCache;
	}

	@KafkaListener(topics = "blocked", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
	public void consumeBlocked(ConsumerRecord<String, String> blocked) {
		blockedCache.put(blocked.key(), Long.valueOf(blocked.value()));
	}

}
