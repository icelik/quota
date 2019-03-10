package icelik.quota.apigw.interceptor;

import com.github.benmanes.caffeine.cache.Cache;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class KafkaSenderInterceptor extends HandlerInterceptorAdapter {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final Cache<String, Long> blockedCache;

	public KafkaSenderInterceptor(KafkaTemplate<String, String> kafkaTemplate,
								  Cache<String, Long> blockedCache) {
		this.kafkaTemplate = kafkaTemplate;
		this.blockedCache = blockedCache;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);

		if (requestMapping == null)
			return true;

		if (requestMapping.value().length == 0)
			return true;

		if (requestMapping.value().length == 0)
			return true;

		if (requestMapping.value()[0] == null)
			return true;


		String remoteAddr = request.getRemoteAddr();
		String key = remoteAddr + ":" + requestMapping.value()[0];


		if (blockedCache.getIfPresent(key) != null)
			return true;

		ProducerRecord<String, String> producerRecord =
				new ProducerRecord<>("requests", key, key);


		kafkaTemplate.send(producerRecord);

		return true;
	}

}
