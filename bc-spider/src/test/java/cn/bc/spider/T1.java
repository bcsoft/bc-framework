package cn.bc.spider;

import org.json.JSONObject;
import org.junit.Test;

public class T1 {
	@Test
	public void test() throws Exception {
		String tpl = "{\"successExpression\": \"#root.containsKey('returnCode')\",\"headers\": []";
		tpl += "\"Host\": \"www.gzjd.gov.cn\"";
		tpl += "}";
		System.out.println(tpl);
		System.out.println(new JSONObject(tpl));
	}

	@Test
	public void testEnv() {
		System.out.println("BC_PROXY_PORT = " + System.getenv("BC_PROXY_PORT"));
		System.out.println("BC_PROXY_HOST = " + System.getenv("BC_PROXY_HOST"));
	}
}