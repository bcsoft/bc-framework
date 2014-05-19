package cn.bc.web.formater;

import junit.framework.Assert;

import org.junit.Test;

public class NubmerFormaterTest {

	@Test
	public void testFloat() {
		NumberFormater f = new NumberFormater("0.0");
		Assert.assertEquals("1.0", f.format(1));
		Assert.assertEquals("1.0", f.format(1.0));
		Assert.assertEquals("1.0", f.format(1.01));
		Assert.assertEquals("1.2", f.format(1.234));
	}

	@Test
	public void testInt() {
		NumberFormater f = new NumberFormater("#.#");
		Assert.assertEquals("1", f.format(1));
		Assert.assertEquals("1", f.format(1.0));
		Assert.assertEquals("1", f.format(1.01));
		Assert.assertEquals("1.2", f.format(1.234));
	}

	@Test
	public void testMY() {
		NumberFormater f = new NumberFormater("$0.0");
		Assert.assertEquals("$1.0", f.format(1));
		Assert.assertEquals("$1.0", f.format(1.0));
		Assert.assertEquals("$1.0", f.format(1.01));
		Assert.assertEquals("$1.2", f.format(1.234));
	}
}
