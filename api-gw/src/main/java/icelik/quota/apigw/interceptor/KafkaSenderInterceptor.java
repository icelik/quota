package icelik.quota.apigw.interceptor;

import com.github.benmanes.caffeine.cache.Cache;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class KafkaSenderInterceptor extends AbstractBaseInterceptor {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final Cache<String, Long> blockedCache;

	public KafkaSenderInterceptor(KafkaTemplate<String, String> kafkaTemplate,
								  Cache<String, Long> blockedCache) {
		this.kafkaTemplate = kafkaTemplate;
		this.blockedCache = blockedCache;
	}


	@Override
	boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String key = calculateKey(request, (HandlerMethod) handler);

		if (blockedCache.getIfPresent(key) == null) {
			ProducerRecord<String, String> producerRecord =
					new ProducerRecord<>("requests", key, key);

			kafkaTemplate.send(producerRecord);

		}

		return true;
	}


}
