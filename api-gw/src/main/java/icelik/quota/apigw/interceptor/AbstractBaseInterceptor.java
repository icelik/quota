package icelik.quota.apigw.interceptor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractBaseInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		if (checkParameters(handler))
			return preHandleInternal(request, response, handler);

		return true;
	}

	private boolean checkParameters(Object handler) {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);

		if (requestMapping == null)
			return false;

		if (requestMapping.value().length == 0)
			return false;

		if (requestMapping.value().length == 0)
			return false;

		return requestMapping.value()[0] != null;
	}

	String calculateKey(HttpServletRequest request, HandlerMethod handler) {
		HandlerMethod handlerMethod = handler;
		RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);

		String remoteAddr = request.getRemoteAddr();
		return remoteAddr + ":" + requestMapping.value()[0];
	}


	abstract boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, Object handler);
}
