package com.erhun.framework.log.filter;

import com.erhun.framework.basic.utils.string.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 日志过滤器
 * 
 * @author gorilla
 *
 */
public class LogFilter extends OncePerRequestFilter {

	/**
	 * 获取内容
	 * 
	 * @param buffer
	 * @param charset
	 * @return
	 */
	private String getContent(byte[] buffer, String charset) {
		if (buffer == null || buffer.length == 0) {
			return StringUtils.EMPTY;
		}
		try {
			return new String(buffer, charset);
		} catch (UnsupportedEncodingException exception) {
			throw new RuntimeException("内容编解码异常", exception);
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		long instant = System.currentTimeMillis();

		if (logger.isDebugEnabled()) {
			// 缓存请求与响应内容
			ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
			ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

			logger.debug(StringUtils.format("request url is:{}", request.getRequestURL()));
			String requestQuery = request.getQueryString();
			if (requestQuery != null) {
				logger.debug(StringUtils.format("request query is:{}", requestQuery));
			}

			filterChain.doFilter(wrappedRequest, wrappedResponse);

			String requestBody = this.getContent(wrappedRequest.getContentAsByteArray(), request.getCharacterEncoding());
			if (requestBody.length() > 0) {
				logger.debug(StringUtils.format("request body is:{}", requestBody));
			}

			logger.debug(StringUtils.format("response status is:{}", response.getStatus()));
			String responseBody = this.getContent(wrappedResponse.getContentAsByteArray(), response.getCharacterEncoding());
			if (responseBody.length() > 0) {
				logger.debug(StringUtils.format("response body is:{}", responseBody));
			}

			wrappedResponse.copyBodyToResponse();
		} else {
			filterChain.doFilter(request, response);
		}

		if (logger.isWarnEnabled()) {
		    long useTime = System.currentTimeMillis() - instant;
		    if (useTime > 500) {
		        logger.warn(StringUtils.format("handle url [{}] use [{}] ms", request.getRequestURL(), System.currentTimeMillis() - instant));
		    }
		}
	}

}
