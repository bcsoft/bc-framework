/**
 * 
 */
package cn.bc.core.util;

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
}
