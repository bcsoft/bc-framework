package cn.bc.core;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class OrgJsonTest {
	@Test
	public void t01_nullValue() {
		JSONObject json = new JSONObject();
		json.put("s", "");
		json.put("n", 1);
		json.put("b", false);


		// null 值会导致该 key 被 remove
		json.put("sn", "123");
		json.put("sn", (String) null);
		json.put("on", (Object) null);

		// 如果需要 null 值，必须使用 JSONObject.NULL
		json.put("rn", JSONObject.NULL);

		//System.out.println(json);

		// jdk1.5 要用 20080701
		// jdk1.6 要用 20090211
		// jdk1.7 要用 20140107
		// jdk1.8 要用 20141113
		Assert.assertEquals("{\"rn\":null,\"b\":false,\"s\":\"\",\"n\":1}", json.toString());
	}

	@Test
	public void t02_mapValue() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("s", "");
		map.put("n", 1);
		map.put("b", false);

		// 同上
		map.put("sn", (String) null);
		map.put("on", (Object) null);
		map.put("rn", JSONObject.NULL);

		JSONObject json = new JSONObject(map);
		Assert.assertEquals("{\"rn\":null,\"b\":false,\"s\":\"\",\"n\":1}", json.toString());
	}
}