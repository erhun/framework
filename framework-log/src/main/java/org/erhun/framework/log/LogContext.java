package org.erhun.framework.log;

import java.time.Instant;

/**
 *
 * 日志上下文信息
 * @author gorilla
 *
 */
public class LogContext {

	public final static String TRACE_ID = "X-B3-TraceId";

	public final static String SPAN_ID = "X-B3-SpanId";

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
	private String identity;

	LogContext() {
	}

	public LogContext(String traceId, String spanId, Instant spanInstant, String nodeHost, int nodePort, String apiUri, String identity) {
		this.traceId = traceId;
		this.spanId = spanId;
		this.spanInstant = spanInstant;
		this.nodeHost = nodeHost;
		this.nodePort = nodePort;
		this.apiUri = apiUri;
		this.identity = identity;
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

	public String getIdentity() {
		return identity;
	}

	public Instant getSpanInstant() {
		return spanInstant;
	}

}
