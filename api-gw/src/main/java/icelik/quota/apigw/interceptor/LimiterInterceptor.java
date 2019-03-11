package icelik.quota.apigw.interceptor;

import com.github.benmanes.caffeine.cache.Cache;
import icelik.quota.apigw.LimitExceededException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class LimiterInterceptor extends AbstractBaseInterceptor {
	private final Cache<String, Long> blockedCache;

	public LimiterInterceptor(Cache<String, Long> blockedCache) {
		this.blockedCache = blockedCache;
	}

	@Override
	boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, Object handler) {
		List<LimitHolder> limitHolders = buildLimitHolders(request, (HandlerMethod) handler);

		boolean limitFound = limitHolders
				.stream()
				.anyMatch(limitHolder -> blockedCache.getIfPresent(limitHolder.getKey()) != null);


		if (limitFound)
			throw new LimitExceededException();

		return true;
	}


}
