package cn.bc.core.gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GsonTest {

	@Test
	public void testObject2String() {
		Gson gson = new Gson();
		GsonPerson p = new GsonPerson("name1", 10);
		String str = gson.toJson(p);
		// System.out.println(str);
		Assert.assertEquals("{\"name\":\"name1\",\"age\":10}", str);
	}

	@Test
	public void testArray2String() {
		Gson gson = new Gson();
		GsonPerson[] persons = new GsonPerson[2];
		for (int i = 1; i < 3; i++) {
			persons[i - 1] = new GsonPerson("name" + i, i);
		}
		String str = gson.toJson(persons);
		// System.out.println(str);
		Assert.assertEquals(
				"[{\"name\":\"name1\",\"age\":1},{\"name\":\"name2\",\"age\":2}]",
				str);
	}

	@Test
	public void testList2String() {
		Gson gson = new Gson();
		List<GsonPerson> persons = new ArrayList<GsonPerson>();
		for (int i = 1; i < 3; i++) {
			GsonPerson p = new GsonPerson("name" + i, i);
			persons.add(p);
		}
		String str = gson.toJson(persons);
		// System.out.println(str);
		Assert.assertEquals(
				"[{\"name\":\"name1\",\"age\":1},{\"name\":\"name2\",\"age\":2}]",
				str);
	}

	@Test
	public void testJsonObject() {
		JsonObject json = new JsonObject();
		json.addProperty("name", "name1");
		json.addProperty("age", 1);
		json.addProperty("ok", true);
		// System.out.println(json.toString());
		Assert.assertEquals("{\"name\":\"name1\",\"age\":1,\"ok\":true}",
				json.toString());
	}

	@Test
	public void testJsonArray() {
		JsonArray jsons = new JsonArray();
		JsonObject json = new JsonObject();
		json.addProperty("name", "name1");
		json.addProperty("age", 1);
		jsons.add(json);

		json = new JsonObject();
		json.addProperty("name", "name2");
		json.addProperty("age", 2);
		jsons.add(json);
		// System.out.println(jsons.toString());
		Assert.assertEquals(
				"[{\"name\":\"name1\",\"age\":1},{\"name\":\"name2\",\"age\":2}]",
				jsons.toString());
	}

	@Test
	public void testMap2String() {
		HashMap<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("name", "name1");
		map.put("age", 1);
		// System.out.println(map);
		Gson gson = new Gson();
		String str = gson.toJson(map);
		// System.out.println(str);
		Assert.assertEquals("{\"name\":\"name1\",\"age\":1}", str);
	}

	@Test
	public void testNestedMap2String() {
		HashMap<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("name", "name1");
		map.put("age", 1);
		HashMap<String, Object> nestedMap = new LinkedHashMap<String, Object>();
		nestedMap.put("name", "name1");
		nestedMap.put("age", 1);
		map.put("map", nestedMap);
		// System.out.println(map);
		Gson gson = new Gson();
		String str = gson.toJson(map);
		// System.out.println(str);
		Assert.assertEquals(
				"{\"name\":\"name1\",\"age\":1,\"map\":{\"name\":\"name1\",\"age\":1}}",
				str);
	}
}
