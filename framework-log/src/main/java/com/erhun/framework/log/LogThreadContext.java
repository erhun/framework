package com.erhun.framework.log;

import java.time.Instant;

/**
 * 日志上下文信息
 * 
 * @author gorilla
 *
 */
public class LogThreadContext {

	public final static String TRACE_ID = "X-B3-TraceId";

	public final static String SPAN_ID = "X-B3-SpanId";

	/**
	 * 上下文
	 */
	private final static ThreadLocal<LogThreadContext> contexts = new ThreadLocal<>();

	/**
	 * 追踪标识
	 */
	private String traceId;

	/**
	 * 跨度标识
	 */
	private String spanId;

	/**
	 * 跨度时间戳
	 */
	private Instant spanInstant;

	/**
	 * 节点主机
	 */
	private String nodeHost;

	/**
	 * 节点端口
	 */
	private int nodePort;

	/**
	 * 接口路径
	 */
	private String apiUri;

	/**
	 * 用户标识
	 */
	private Long userId;

	LogThreadContext() {
	}

	public LogThreadContext(String traceId, String spanId, Instant spanInstant, String nodeHost, int nodePort, String apiUri, Long userId) {
		this.traceId = traceId;
		this.spanId = spanId;
		this.spanInstant = spanInstant;
		this.nodeHost = nodeHost;
		this.nodePort = nodePort;
		this.apiUri = apiUri;
		this.userId = userId;
	}

	public String getTraceId() {
		return traceId;
	}

	public String getSpanId() {
		return spanId;
	}

	public String getNodeHost() {
		return nodeHost;
	}

	public int getNodePort() {
		return nodePort;
	}

	public String getApiUri() {
		return apiUri;
	}

	public Long getUserId() {
		return userId;
	}

	public Instant getSpanInstant() {
		return spanInstant;
	}

	/**
	 * 设置API上下文信息(由Controller拦截器调用)
	 *
	 * @param context
	 */
	public final static void setContext(LogThreadContext context) {
		if (context == null) {
			contexts.remove();
		} else {
			contexts.set(context);
		}
	}

	/**
	 * 获取API上下文信息(由LogUtils调用)
	 *
	 * @return
	 */
	public final static LogThreadContext getContext() {
		return contexts.get();
	}

}
