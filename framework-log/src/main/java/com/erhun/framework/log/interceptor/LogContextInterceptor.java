package com.erhun.framework.log.interceptor;

import com.erhun.framework.log.LogThreadContext;
import com.erhun.framework.log.annotation.RestInterceptor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

/**
 * 日志上下文拦截器
 * @author gorilla
 *
 */
@RestInterceptor
public class LogContextInterceptor implements HandlerInterceptor {

	/**
	 * 节点主机
	 */
	@Value("${spring.application.name}")
	private String nodeHost;

	/**
	 * 节点端口
	 */
	@Value("${server.port}")
	private int nodePort;

	/** 用户标识 */
	private final static String USER_ID = "userId";

	/** IP */
	private final static String IP = "ip";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
			return true;
		}

		String userId = request.getHeader(USER_ID);
		String ip = request.getHeader(IP);

		String traceId = MDC.get(LogThreadContext.TRACE_ID);
		String spanId = MDC.get(LogThreadContext.SPAN_ID);
		Instant spanInstant = Instant.now();
		String nodeHost = this.nodeHost;
		int nodePort = this.nodePort;
		String apiUri = request.getRequestURI();
		// 此处需要获取所有的LogThreadContext需要的参数.
		LogThreadContext logContext = new LogThreadContext(traceId, spanId, spanInstant, nodeHost, nodePort, apiUri, userId == null ? null : Long.valueOf(userId));
		LogThreadContext.setContext(logContext);
		return true;
	}

}