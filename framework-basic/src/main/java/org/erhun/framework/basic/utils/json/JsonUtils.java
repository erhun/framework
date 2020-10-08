package org.erhun.framework.basic.utils.json;


import org.erhun.framework.basic.utils.datetime.DateFormatUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author weichao <gorilla@aliyun.com>
 * @date 2016-2-3
 */
public final class JsonUtils {

	private final static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static final ObjectMapper defaultMapper = new ObjectMapper();

	static {
		defaultMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		defaultMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		defaultMapper.setSerializationInclusion(Include.NON_EMPTY);
		defaultMapper.registerModule(new Jdk8Module());
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
			@Override
			public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
				jsonGenerator.writeString(DateFormatUtils.toDateTimeString(localDateTime));
			}
		});
		javaTimeModule.addSerializer(LocalDate.class, new JsonSerializer<LocalDate>() {
			@Override
			public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
				jsonGenerator.writeString(DateFormatUtils.toDateString(localDate));
			}
		});
		javaTimeModule.addSerializer(Instant.class, new JsonSerializer<Instant>() {
			@Override
			public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
				String value = DateTimeFormatter.ofPattern(DateFormatUtils.DATETIME_PATTERN).format(instant.atZone(ZoneId.systemDefault()));
				jsonGenerator.writeString(value);
			}
		});
		defaultMapper.registerModule(javaTimeModule);
	}

	private JsonUtils() {
	}

	public static ObjectMapper build(Include include) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(include);
		return objectMapper;
	}

	public static <T> T parse(String json) {

		if(json == null){
			return null;
		}
		try {
			return (T) defaultMapper.readValue(json, Object.class);
		} catch (Exception e) {
			logger.warn("read json error", e);
			return null;
		}
	}

	public static <T> T parse(byte [] jsonBytes) {

		if(jsonBytes == null || jsonBytes.length == 0){
			return null;
		}
		try {
			return (T) defaultMapper.readValue(jsonBytes, Object.class);
		} catch (Exception e) {
			logger.warn("read json error", e);
			return null;
		}
	}

	public static <T> T parse(String json, Class <T> clazz) {

		if(StringUtils.isEmpty(json)){
			return null;
		}

		try {
			return (T) defaultMapper.readValue(json, clazz);
		} catch (Exception e) {
			logger.warn("read json error", e);
			return null;
		}

	}

	public static <T> T parse(byte [] jsonBytes, Class <T> clazz) {

		if(jsonBytes == null || jsonBytes.length == 0){
			return null;
		}

		try {
			return (T) defaultMapper.readValue(jsonBytes, clazz);
		} catch (Exception e) {
			logger.warn("read json error", e);
			return null;
		}

	}

	public static <T extends List <?> > T parseList(String json, Class <?> elementClass) {
		try {
			JavaType javaType = defaultMapper.getTypeFactory().constructCollectionType(List.class, elementClass);
			return defaultMapper.readValue(json, javaType);
		} catch (Exception e) {
			logger.warn("read json error", e);
			return null;
		}
	}

	public static void main(String[] args) {
		Object o = parse("{\"ss\":{\"ff\":3},\"parameters\":[{\"name\":\"33\"}]}=");
		System.out.println(System.currentTimeMillis());
	}

	public static String toJSONString(Object obj) {
		try {
			return defaultMapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.warn("write object to json error", e);
			return null;
		}
	}

}
