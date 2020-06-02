package org.erhun.framework.log;

import org.erhun.framework.basic.utils.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * 日志工具(支持分布式日志追踪)
 * @author gorilla
 *
 */
public class LogUtils {

	private final static String className = LogUtils.class.getName();

	private final static Logger logger = LoggerFactory.getLogger(LogUtils.class);

	private final static String CLASS_METHOD = "classMethod";

	private final static String LOG_CONTEXT = "logContext";

	/**
	 *
	 * 打印调试日志
	 * @param template
	 * @param parameters
	 */
	public static void debug(String template, Object... parameters) {
		if (logger.isDebugEnabled()) {
			String logContext = JsonUtils.toJSONString(LogContextHolder.getContext());
			if (logContext != null) {
				MDC.put(LOG_CONTEXT, logContext);
			}
			StackTraceElement element = getStackTraceElement();
			MDC.put(CLASS_METHOD, element.getClassName() + "." + element.getMethodName() + "()@" + element.getLineNumber());
			template = format(template, parameters);
			logger.debug(template);
			MDC.remove(LOG_CONTEXT);
			MDC.remove(CLASS_METHOD);
		}
	}

	/**
	 * 打印重要日志
	 * 
	 * @param template
	 * @param parameters
	 */
	public static void info(String template, Object... parameters) {
		if (logger.isInfoEnabled()) {
			String logContext = JsonUtils.toJSONString(LogContextHolder.getContext());
			if (logContext != null) {
				MDC.put(LOG_CONTEXT, logContext);
			}
			StackTraceElement element = getStackTraceElement();
			MDC.put(CLASS_METHOD, element.getClassName() + "." + element.getMethodName() + "()@" + element.getLineNumber());
			template = format(template, parameters);
			logger.info(template);
			MDC.remove(LOG_CONTEXT);
			MDC.remove(CLASS_METHOD);
		}
	}

	/**
	 * 打印警告日志
	 * 
	 * @param exception
	 * @param template
	 * @param parameters
	 */
	public static void warn(Throwable exception, String template, Object... parameters) {
		if (logger.isWarnEnabled()) {
			String logContext = JsonUtils.toJSONString(LogContextHolder.getContext());
			if (logContext != null) {
				MDC.put(LOG_CONTEXT, logContext);
			}
			StackTraceElement element = getStackTraceElement();
			MDC.put(CLASS_METHOD, element.getClassName() + "." + element.getMethodName() + "()@" + element.getLineNumber());
			template = format(template, parameters);
			logger.warn(template, exception);
			MDC.remove(LOG_CONTEXT);
			MDC.remove(CLASS_METHOD);
		}
	}

	/**
	 * 根据打印错误日志
	 * 
	 * @param exception
	 * @param template
	 * @param parameters
	 */
	public static void error(Throwable exception, String template, Object... parameters) {
		if (logger.isErrorEnabled()) {
			String logContext = JsonUtils.toJSONString(LogContextHolder.getContext());
			if (logContext != null) {
				MDC.put(LOG_CONTEXT, logContext);
			}
			StackTraceElement element = getStackTraceElement();
			MDC.put(CLASS_METHOD, element.getClassName() + "." + element.getMethodName() + "()@" + element.getLineNumber());
			template = format(template, parameters);
			logger.error(template, exception);
			MDC.remove(LOG_CONTEXT);
			MDC.remove(CLASS_METHOD);
		}
	}

	/**
	 * 获取当前线程栈信息
	 * @return
	 */
	private static StackTraceElement getStackTraceElement() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		StackTraceElement element = null;
		// 从栈的最上开始 往下找 找到第一个不为Log线程的类
		for (int index = 0, size = elements.length; index < size; index++) {
			element = elements[index];
			String className = element.getClassName();
			if ("java.lang.Thread".equals(className) || className.equals(className)) {
				if (index != size) {
					continue;
				}
			}
			break;
		}
		return element;
	}

	private static final String format(String template, Object... parameters) {
		FormattingTuple formatter = MessageFormatter.arrayFormat(template, parameters);
		return formatter.getMessage();
	}

	public static boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public static boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public static boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	public static boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

}
