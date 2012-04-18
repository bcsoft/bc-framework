/**
 * 
 */
package cn.bc.template.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.core.util.DateUtils;

/**
 * 
 * @author dragon
 * 
 */
public class FreeMarkerUtilsTest {
	@Test
	public void testFormatByMapParams() {
		String tpl = "${k1}-${k2}";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("k1", "v1");
		args.put("k2", 2);
		Assert.assertEquals("v1-2", FreeMarkerUtils.format(tpl, args));
	}

	// 日期格式化
	@Test
	public void testFormatDate() {
		String tpl = "${d?string(\"yyyy-MM-dd HH:mm:ss\")}";

		// Date类型,不支持Calendar类型
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("d", DateUtils.getDate("2012-01-01 12:10:05"));
		Assert.assertEquals("2012-01-01 12:10:05",
				FreeMarkerUtils.format(tpl, args));
	}
}
