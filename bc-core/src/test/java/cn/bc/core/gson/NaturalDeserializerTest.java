package cn.bc.core.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NaturalDeserializerTest {
	@Test
	public void testObject2Json() {
		Gson gson = buildGson();
		GsonPerson p = new GsonPerson("name1", 10);
		String str = gson.toJson(p);
		// System.out.println(str);
		Assert.assertEquals("{\"name\":\"name1\",\"age\":10}", str);
	}

	@Test
	public void testJson2Map() {
		String str = "{\"str\":\"str\",\"int\":1,\"bool\":true}";
		Gson gson = buildGson();
		Map<String, Object> map = gson.fromJson(str,
				new TypeToken<HashMap<String, Object>>() {
				}.getType());
		// System.out.println(map);
		// for (Entry<String, Object> e : map.entrySet()) {
		// System.out.println(e.getKey() + "," + e.getValue().getClass());
		// }
		Assert.assertEquals("str", map.get("str"));
		Assert.assertEquals(String.class, map.get("str").getClass());
		Assert.assertEquals(new Integer(1), map.get("int"));
		Assert.assertEquals(Integer.class, map.get("int").getClass());
		Assert.assertEquals(new Boolean(true), map.get("bool"));
		Assert.assertEquals(Boolean.class, map.get("bool").getClass());
	}

	@Test
	public void testJson2MapWithNested() {
		String str = "{\"str\":\"str\",\"int\":1,\"bool\":true,\"sub\":{\"str\":\"str\",\"int\":1,\"bool\":true}}";
		Gson gson = buildGson();
		Map<String, Object> map = gson.fromJson(str,
				new TypeToken<HashMap<String, Object>>() {
				}.getType());
		System.out.println(map);
		for (Entry<String, Object> e : map.entrySet()) {
			System.out.println(e.getKey() + "," + e.getValue().getClass());
		}
		Assert.assertEquals("str", map.get("str"));
		Assert.assertEquals(String.class, map.get("str").getClass());
		Assert.assertEquals(new Integer(1), map.get("int"));
		Assert.assertEquals(Integer.class, map.get("int").getClass());
		Assert.assertEquals(new Boolean(true), map.get("bool"));
		Assert.assertEquals(Boolean.class, map.get("bool").getClass());

		Assert.assertTrue(map.get("sub") instanceof Map);
		@SuppressWarnings({ "unchecked" })
		Map<String, Object> sub = (Map<String, Object>) map.get("sub");
		Assert.assertEquals("str", sub.get("str"));
		Assert.assertEquals(String.class, sub.get("str").getClass());
		Assert.assertEquals(new Integer(1), sub.get("int"));
		Assert.assertEquals(Integer.class, sub.get("int").getClass());
		Assert.assertEquals(new Boolean(true), sub.get("bool"));
		Assert.assertEquals(Boolean.class, sub.get("bool").getClass());
	}

	/**
	 * @return
	 */
	private Gson buildGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder
				.registerTypeAdapter(Object.class, new NaturalDeserializer());
		Gson gson = gsonBuilder.create();
		return gson;
	}
}
