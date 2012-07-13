package cn.bc.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.core.gson.NaturalDeserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Json辅助工具类
 * <p>
 * 这个工具类使用Gson进行相关的处理，封装了可以将json字符串转换为Map对象的方法，并支持嵌套Map对象，也就是说凡事字符串中含{}
 * 的部分都会转换为Map对象
 * </p>
 * 
 * @author dragon
 */
public class JsonUtils {
	protected static Log logger = LogFactory.getLog(JsonUtils.class);

	private JsonUtils() {
	}

	/**
	 * 获取对象的Json字符串表示
	 * 
	 * @param source
	 *            原始对象
	 * @return
	 */
	public static String toJson(Object source) {
		return getGson().toJson(source);
	}

	/**
	 * 将json字符串转换为Map对象
	 * <p>
	 * 封装了可以将json字符串转换为Map对象的方法，并支持嵌套Map对象，也就是说凡是字符串中含{}
	 * 的部分都会转换为Map对象，内部实现中使用了LinkedHashMap
	 * </p>
	 * 
	 * @param json
	 *            标准格式的json字符串
	 * @return
	 * @throws com.google.gson.JsonParseException
	 *             如果输入的字符串不是json格式将会抛出此异常
	 */
	public static Map<String, Object> toMap(String json) {
		return getGson().fromJson(json,
				new TypeToken<HashMap<String, Object>>() {
				}.getType());
	}

	/**
	 * 将json字符串转换为数组对象
	 * 
	 * @param json
	 *            标准格式的json字符串
	 * @return
	 * @throws com.google.gson.JsonParseException
	 *             如果输入的字符串不是json数组格式将会抛出此异常
	 */
	public static Object[] toArray(String json) {
		return getGson().fromJson(json, new TypeToken<Object[]>() {
		}.getType());
	}

	/**
	 * 将json字符串转换为集合对象
	 * 
	 * @param json
	 *            标准格式的json字符串
	 * @return
	 * @throws com.google.gson.JsonParseException
	 *             如果输入的字符串不是json数组格式将会抛出此异常
	 */
	public static Collection<Object> toCollection(String json) {
		return getGson().fromJson(json, new TypeToken<Collection<Object>>() {
		}.getType());
	}

	/**
	 * 构建默认的Gson处理器
	 * 
	 * @return
	 */
	private static Gson getGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder
				.registerTypeAdapter(Object.class, new NaturalDeserializer());
		Gson gson = gsonBuilder.create();
		return gson;
	}
}