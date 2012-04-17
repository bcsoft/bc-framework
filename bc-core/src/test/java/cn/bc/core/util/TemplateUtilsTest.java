/**
 * 
 */
package cn.bc.core.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author dragon
 * 
 */
public class TemplateUtilsTest {
	@Test
	public void testFormatByMapParams() {
		String tpl = "${k1}-${k2}";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("k1", "v1");
		args.put("k2", 2);
		Assert.assertEquals("v1-2", TemplateUtils.format(tpl, args));
	}

	@Test
	public void testFormatByArrayParams() {
		String tpl = "{0}-{1}";
		Object[] args = new Object[] { "v1", 2 };
		Assert.assertEquals("v1-2", TemplateUtils.format(tpl, args));
	}
}
