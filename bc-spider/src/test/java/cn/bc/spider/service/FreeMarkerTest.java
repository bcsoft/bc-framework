package cn.bc.spider.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import cn.bc.template.util.FreeMarkerUtils;

public class FreeMarkerTest {
	private static Log logger = LogFactory.getLog(FreeMarkerTest.class);

	public FreeMarkerTest() {
	}

	@Test
	public void test() throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		Object[] data = new Object[2];
		args.put("data", data);
		args.put("ok", true);
		args.put("i", 1);
		Map<String, Object> one = new LinkedHashMap<String, Object>();
		one.put("k11", "v11");
		one.put("k12", "v12");
		data[0] = one;
		one = new LinkedHashMap<String, Object>();
		one.put("k21", "v21");
		one.put("k22", 2);
		data[1] = one;
		if (logger.isDebugEnabled()) {
			logger.debug("args=" + args);
			logger.debug("data=" + data);
		}

		String tpl = "";//2.3.16
//		tpl += "<#if (data?size > 0)>";
//		tpl += "<#assign keys = data[0]?keys><#list data[0]?keys as key><td>${key}</td></#list>";
//		tpl += "</#if>";

		tpl += "<#list data as r><tr><td>${r_index + 1}</td><#list r?values as k><td>${k}</td></#list></tr></#list>";

		//		tpl += "${data@toString()}";
//		tpl += "${data?size}";
//		tpl += "<#list data as r>${r_index}.${r?string}<#if r_has_next>,</#if></#list>";
		if (logger.isDebugEnabled()) {
			logger.debug("tpl0=" + tpl);
		}
		tpl = FreeMarkerUtils.format(tpl, args);
		if (logger.isDebugEnabled()) {
			logger.debug("tpl1=" + tpl);
		}
	}
}