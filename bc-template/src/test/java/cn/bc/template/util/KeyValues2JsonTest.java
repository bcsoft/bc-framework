package cn.bc.template.util;

import org.commontemplate.util.Assert;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class KeyValues2JsonTest {

	@Test
	public void test1() throws JSONException {
		String[] items = new String[] { "a.a1.a11=v1", "a.a2=v2", "b=v3" };
		JSONObject global = new JSONObject();

		String key, value;
		String[] kv;
		for (String item : items) {
			kv = item.split("=");
			key = kv[0];
			value = kv[1];
			setValue(global, key.split("\\."), value);
		}

		System.out.println("json=" + global.toString());
		Assert.assertEquals(
				"{\"b\":\"v3\",\"a\":{\"a1\":{\"a11\":\"v1\"},\"a2\":\"v2\"}}",
				global.toString());
	}

	private void setValue(JSONObject global, String[] ks, String value)
			throws JSONException {
		JSONObject upper = global;
		for (int i = 0; i < ks.length; i++) {
			// 如果是.后的最后一个，就设置返回
			if (i == ks.length - 1) {
				upper.put(ks[i], value);
				return;
			}

			// 如果当前key没有就初始化一个
			if (!upper.has(ks[i])) {
				upper.put(ks[i], new JSONObject());
			}

			// 设为找到的对象或最后创建的对象
			upper = upper.getJSONObject(ks[i]);
		}
	}

	@Test
	public void test2() throws JSONException {
		// String[] items = new String[] { "a.a1.a11=v1", "a.a2=v2", "b=v3" };
		JSONObject json = new JSONObject();

		JSONObject a = new JSONObject();
		json.put("a", a);
		JSONObject a1 = new JSONObject();
		a.put("a1", a1);
		a1.put("a11", "v1");

		a.put("a2", "v2");

		json.put("b", "v3");

		System.out.println("json=" + json.toString());
	}
}
