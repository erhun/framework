package org.erhun.framework.log.configurer;

import org.erhun.framework.log.filter.LogFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gorilla
 **/
@Configuration
public class LogFilterConfigurer {

	@Bean
	public LogFilter getLogFilter() {
		LogFilter filter = new LogFilter();
		return filter;
	}

}
