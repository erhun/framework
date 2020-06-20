package org.erhun.framework.log;

/**
 * 
 * 日志上下文信息
 * @author gorilla
 *
 */
public class LogContextHolder {

	private final static ThreadLocal<LogContext> contextThreadLocal = new ThreadLocal<>();

	public final static void setContext(LogContext context) {
		if (context == null) {
			contextThreadLocal.remove();
		} else {
			contextThreadLocal.set(context);
		}
	}

	public final static LogContext getContext() {
		return contextThreadLocal.get();
	}
}
