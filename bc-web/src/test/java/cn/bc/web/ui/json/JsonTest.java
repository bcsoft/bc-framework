package cn.bc.web.ui.json;

import org.junit.Assert;
import org.junit.Test;

public class JsonTest {
	@Test
	public void test01() {
		Json json = new Json();
		json.put("code", "code1");
		Assert.assertEquals("{\"code\":\"code1\"}", json.toString());
		
		json.put("name", "name1");
		Assert.assertEquals("{\"code\":\"code1\",\"name\":\"name1\"}", json.toString());
	}
}
