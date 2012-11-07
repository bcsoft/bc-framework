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

	//
	@Test
	public void testFormat() {
		String tpl = "${bsType?string('承包□合作□挂靠√','123')}";
		// String tpl = "${bsType == 3 ? \"承包□合作□挂靠√\":\"123\"}";

		// Date类型,不支持Calendar类型
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("bsType", true);
		Assert.assertEquals("承包□合作□挂靠√", FreeMarkerUtils.format(tpl, args));
	}

	//
	@Test
	public void testFormatByIfElse() {
		String tpl = "<#if logoutReason == \"转蓝\">√<#else>□</#if>";
		// String tpl =
		// "&lt;#if vs.logoutReason == \"转蓝\"&gt;√&lt;#else&gt;□&lt;/#if&gt;";
		// String tpl =
		// "&lt;#if logoutOwner??&gt;${logoutOwner}&lt;#else&gt;123&lt;/#if&gt;";
		// String tpl = "${bsType == 3 ? \"承包□合作□挂靠√\":\"123\"}";

		// Date类型,不支持Calendar类型
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("logoutReason", "转蓝");
		args.put("logoutOwner", "公司");
		Assert.assertEquals("√", FreeMarkerUtils.format(tpl, args));
	}

	@Test
	public void testFormatByMethod() {
		String tpl = "${a2}-${a1}";
		Map<String, Object> args = new HashMap<String, Object>();
		T a1 = new T();
		a1.setA1("name");
		args.put("a1", a1);
		args.put("a2", "a2");
		Assert.assertEquals("name", FreeMarkerUtils.format(tpl, args));
	}

	class T {
		private String a1;

		public String doIt() {
			return "ok";
		}

		public String getA1() {
			return a1;
		}

		public void setA1(String name) {
			this.a1 = name;
		}
	}
}
