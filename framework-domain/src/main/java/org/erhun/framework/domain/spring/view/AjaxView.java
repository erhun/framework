package org.erhun.framework.domain.spring.view;

import org.erhun.framework.basic.utils.json.JsonUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 *
 * @author weichao (gorilla@aliyun.com)
 */
public class AjaxView implements View {

	public static final String DEFAULT_CONTENT_TYPE = "application/json;charset=utf-8";

	private static final String prefix = "ajax:";

	private Object value;

	public AjaxView(Object value){
		this.value = value;
	}

	@Override
	public String getContentType() {
		return DEFAULT_CONTENT_TYPE;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void render(Map model, HttpServletRequest request, HttpServletResponse response ) throws Exception {

		PrintWriter out = response.getWriter();

		if(value instanceof String){
			String val = (String) value;
			String tmpPrefix = prefix;
			if(val.startsWith("error")){
				tmpPrefix = "error";
			}
			if( val.length() > tmpPrefix.length() && val.charAt( tmpPrefix.length() ) == '$' ){
				WebApplicationContext context = RequestContextUtils.findWebApplicationContext(request);
				String msg = context.getMessage( val.substring( tmpPrefix.length() + 2, val.length() - 1 ), null, request.getLocale());
				out.write(msg);
			}else{
				out.write(val,tmpPrefix.length(),val.length()-tmpPrefix.length());
			}
		}else{
			response.setContentType(getContentType());
			out.write(JsonUtils.toJSONString(value) );
		}

		out.flush();

	}

}
