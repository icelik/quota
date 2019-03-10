package icelik.quota.apigw.interceptor;

import com.github.benmanes.caffeine.cache.Cache;
import icelik.quota.apigw.QuotaExceededException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LimiterInterceptor extends AbstractBaseInterceptor {
	private final Cache<String, Long> blockedCache;

	public LimiterInterceptor(Cache<String, Long> blockedCache) {
		this.blockedCache = blockedCache;
	}

	@Override
	boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String key = calculateKey(request, (HandlerMethod) handler);

		if (blockedCache.getIfPresent(key) == null)
			return true;

		throw new QuotaExceededException();
	}


}
