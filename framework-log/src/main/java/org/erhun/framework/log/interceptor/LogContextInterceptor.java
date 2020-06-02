package org.erhun.framework.log.interceptor;

import org.erhun.framework.log.LogContext;
import org.erhun.framework.log.LogContextHolder;
import org.erhun.framework.log.annotation.RestInterceptor;
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

	/**
	 * 用户标识
	 **/
	private final static String IDENTITY = "biz_identity";

	/**
	 * IP
	 * */
	private final static String IP = "ip";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
			return true;
		}

		String identity = request.getHeader(IDENTITY);
		//String ip = request.getHeader(IP);

		String traceId = MDC.get(LogContext.TRACE_ID);
		String spanId = MDC.get(LogContext.SPAN_ID);
		Instant spanInstant = Instant.now();
		String nodeHost = this.nodeHost;
		int nodePort = this.nodePort;
		String apiUri = request.getRequestURI();
		LogContext logContext = new LogContext(traceId, spanId, spanInstant, nodeHost, nodePort, apiUri, identity);
		LogContextHolder.setContext(logContext);
		return true;
	}

}