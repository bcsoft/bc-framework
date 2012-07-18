package cn.bc.core.gson;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * 支持字符串反序列化为Map类型对象的Gson反序列化处理器
 * 
 * @author dragon
 * 
 */
public class NaturalDeserializer implements JsonDeserializer<Object> {
	public Object deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) {
		if (json.isJsonNull())
			return null;
		else if (json.isJsonPrimitive())
			return handlePrimitive(json.getAsJsonPrimitive());
		else if (json.isJsonArray())
			return handleArray(json.getAsJsonArray(), context);
		else
			return handleObject(json.getAsJsonObject(), context);
	}

	private Object handlePrimitive(JsonPrimitive json) {
		if (json.isBoolean())
			return json.getAsBoolean();
		else if (json.isString())
			return json.getAsString();
		else {
			BigDecimal bigDec = json.getAsBigDecimal();
			// Find out if it is an int type
			try {
				bigDec.toBigIntegerExact();
				try {
					return bigDec.intValueExact();
				} catch (ArithmeticException e) {
				}
				return bigDec.longValue();
			} catch (ArithmeticException e) {
			}
			// Just return it as a double
			return bigDec.doubleValue();
		}
	}

	private Object handleArray(JsonArray json,
			JsonDeserializationContext context) {
		Object[] array = new Object[json.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = context.deserialize(json.get(i), Object.class);
		return array;
	}

	private Object handleObject(JsonObject json,
			JsonDeserializationContext context) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (Map.Entry<String, JsonElement> entry : json.entrySet())
			map.put(entry.getKey(),
					context.deserialize(entry.getValue(), Object.class));
		return map;
	}
}