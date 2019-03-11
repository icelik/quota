package icelik.quota.apigw.interceptor;

import icelik.quota.apigw.Limit;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractBaseInterceptor extends HandlerInterceptorAdapter {

	private ExpressionParser expressionParser = new SpelExpressionParser();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		if (checkLimitedAnnotation(handler))
			return preHandleInternal(request, response, handler);

		return true;
	}

	private boolean checkLimitedAnnotation(Object handler) {

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Limit[] limitAnnotations = handlerMethod.getMethod().getAnnotationsByType(Limit.class);

		return limitAnnotations != null && limitAnnotations.length > 0;
	}

	List<LimitHolder> buildLimitHolders(HttpServletRequest request, HandlerMethod handlerMethod) {

		EvaluationContext context = new StandardEvaluationContext(handlerMethod.getBean());
		context.setVariable("IP", request.getRemoteAddr());

		Limit[] limitAnnotations = handlerMethod.getMethod().getAnnotationsByType(Limit.class);

		return Arrays.stream(limitAnnotations).map(limit ->
				new LimitHolder(expressionParser.parseExpression(limit.key()).getValue(context, String.class),
						expressionParser.parseExpression(limit.treshold()).getValue(context, Long.class),
						expressionParser.parseExpression(limit.blockDurationInMilis()).getValue(context, Long.class)))
				.collect(Collectors.toList());

	}


	abstract boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, Object handler);
}
