package cn.bc.web.ui.json;

import org.junit.Assert;
import org.junit.Test;

public class JsonArrayTest {
	@Test
	public void test01() {
		Json json = new Json();
		json.put("code", "code1");
		JsonArray jsons = new JsonArray();
		jsons.add(json);
		Assert.assertEquals("[{\"code\":\"code1\"}]", jsons.toString());
		
		json = new Json();
		json.put("code", "code2");
		jsons.add(json);
		Assert.assertEquals("[{\"code\":\"code1\"},{\"code\":\"code2\"}]", jsons.toString());
	}
}
