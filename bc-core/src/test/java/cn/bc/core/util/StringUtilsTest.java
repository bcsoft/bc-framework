/**
 * 
 */
package cn.bc.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author dragon
 * 
 */
public class StringUtilsTest {
	@Test
	public void countMatches() {
		Assert.assertEquals(1, StringUtils.countMatches("?", "?"));
		Assert.assertEquals(1, StringUtils.countMatches("c.a=?", "?"));
		Assert.assertEquals(2, StringUtils.countMatches("?,?", "?"));
		Assert.assertEquals(2, StringUtils.countMatches("c.a in (?,?)", "?"));
		Assert.assertEquals(2,
				StringUtils.countMatches("c.a>=? or c.bb >=?", "?"));
	}

	@Test
	public void wenhaoReplace() {
		Assert.assertEquals("a1>? and a2<?",
				"a1>?0 and a2<?1".replaceAll("\\?\\d+", "\\?"));
		Assert.assertEquals("a1>? and a2<?",
				"a1>?0 and a2<?11".replaceAll("\\?\\d+", "\\?"));
		Assert.assertEquals("a1 in (?,?)",
				"a1 in (?0,?1)".replaceAll("\\?\\d+", "\\?"));
	}

	@Test
	public void wenhaoMatch() {
		String candidate = "a1>? and a2<?71 and a2<?0";
		String regex = "\\?\\d+";//匹配?[num]模式
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(candidate);
		String val = null;
		while (m.find()) {
			val = m.group();
			System.out.println("MATCH: " + val);
		}
		if (val == null) {
			System.out.println("NO MATCHES: ");
		}
	}
}
