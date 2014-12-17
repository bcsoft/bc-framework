package cn.bc.web.struts.beanutils;

import org.junit.Assert;

import org.apache.commons.beanutils.Converter;
import org.junit.Test;

public class StringConverterTest {
	@Test
	public void test() {
		Converter c = new StringConverter();
		Assert.assertEquals(null, c.convert(String.class, ""));
		Assert.assertEquals(null, c.convert(String.class, null));
		Assert.assertEquals(" ", c.convert(String.class, " "));
		Assert.assertEquals("abc", c.convert(String.class, "abc"));
	}
}