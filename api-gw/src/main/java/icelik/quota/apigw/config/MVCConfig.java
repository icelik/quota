package icelik.quota.apigw.config;

import icelik.quota.apigw.interceptor.KafkaSenderInterceptor;
import icelik.quota.apigw.interceptor.LimiterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MVCConfig implements WebMvcConfigurer {
	private final KafkaSenderInterceptor kafkaSenderInterceptor;
	private final LimiterInterceptor limiterInterceptor;

	public MVCConfig(KafkaSenderInterceptor kafkaSenderInterceptor,
					 LimiterInterceptor limiterInterceptor) {
		this.kafkaSenderInterceptor = kafkaSenderInterceptor;
		this.limiterInterceptor = limiterInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(kafkaSenderInterceptor);
		registry.addInterceptor(limiterInterceptor);
	}
}
