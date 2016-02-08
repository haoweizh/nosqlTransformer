package com.qnvip.mybatis.util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;

public class ObjectToJson {

	private static Log log = LogFactory.getLog(ObjectToJson.class);

	private static ObjectMapper objectMapper = null;

	static {
		objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
		objectMapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	}

	/**
	 * 针对Jdbc4array的特殊处理
	 * 
	 * @param json
	 * @param elementClass
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static Object[] parsePGJsonList(String json, Class elementClass)
			throws JsonParseException, JsonMappingException, IOException {
		json = json.replaceAll("\"\\{", "{").replaceAll("\\}\"", "}").trim();
		json = "[" + json.substring(1, json.length() - 1) + "]";
		json = json.replaceAll("\\\\", "");
		List result = objectMapper.readValue(json,
				objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass));
		return result.toArray();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object parsePGJson(String json, Class to)
			throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(json, to);
	}

	@SuppressWarnings("rawtypes")
	public static String postgresSqlStringify(Object object)
			throws JsonGenerationException, JsonMappingException, IOException {
		if (object instanceof String) {
			return "'" + (String) object + "'";
		}
		if (object instanceof Collection) {
			object = ((Collection) object).toArray();
		}
		if (object instanceof AbstractCollection) {
			object = ((AbstractCollection) object).toArray();
		}
		if (object instanceof Object[]) {
			if (((Object[]) object).length > 0 && ((Object[]) object)[0].getClass().getName().startsWith("java.lang")) {
				return "array" + objectMapper.writeValueAsString(object);
			}
			String result = "array[";
			for (int i = 0; i < ((Object[]) object).length; i++) {
				result += postgresSqlStringify(((Object[]) object)[i]) + "::jsonb,";
			}
			if (result.endsWith(",")) {
				result = result.substring(0, result.length() - 1) + "]";
			}
			return result;
		}
		return "'" + objectMapper.writeValueAsString(object) + "'";
	}

	public static String stringify(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static void stringify(OutputStream out, Object object) {

		try {
			objectMapper.writeValue(out, object);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static <T> T parse(String json, Class<T> clazz) {

		if (json == null || json.length() == 0) {
			return null;
		}

		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

}