package icelik.quota.apigw.interceptor;

import com.github.benmanes.caffeine.cache.Cache;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
		List<LimitHolder> limitHolders = buildLimitHolders(request, (HandlerMethod) handler);

		limitHolders.forEach(limitHolder -> {
			if (blockedCache.getIfPresent(limitHolder.getKey()) == null) {
				ProducerRecord<String, String> producerRecord =
						new ProducerRecord<>("requests", limitHolder.getKey(), limitHolder.toJsonString());

				kafkaTemplate.send(producerRecord);
			}

		});
		return true;
	}


}
