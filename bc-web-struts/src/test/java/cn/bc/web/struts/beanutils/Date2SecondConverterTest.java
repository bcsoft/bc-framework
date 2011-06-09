package cn.bc.web.struts.beanutils;

import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.beanutils.Converter;
import org.junit.Before;
import org.junit.Test;

import cn.bc.core.exception.CoreException;
import cn.bc.web.struts.beanutils.Date2Second;
import cn.bc.web.struts.beanutils.Date2SecondConverter;


public class Date2SecondConverterTest {
	private Converter c;

	@Before
	public void setUp() {
		c = new Date2SecondConverter();
	}

	@Test
	public void Null_Date2Second() {
		Assert.assertNull(c.convert(null, null));
		Assert.assertNull(c.convert(String.class, null));
		Assert.assertNull(c.convert(Date.class, null));
		Assert.assertNull(c.convert(Date2Second.class, null));
	}

	@Test
	public void String_Date2Second() {
		Assert.assertNull(c.convert(Date2Second.class, ""));
		Object value = c.convert(Date2Second.class, "2010-12-01 20:30:40");
		Assert.assertNotNull(value);
		Assert.assertEquals(Date2Second.class, value.getClass());
		Assert.assertEquals("2010-12-01 20:30:40", value.toString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void Date_Date2Second() {
		Date date = new Date(2010-1900, 12-1, 1, 20, 30, 40);
		Object value = c.convert(Date2Second.class, date);
		Assert.assertNotNull(value);
		Assert.assertEquals(Date2Second.class, value.getClass());
		Assert.assertEquals("2010-12-01 20:30:40", value.toString());
	}

	@Test(expected=CoreException.class)
	public void error01() {
		//字符串长度不足则格式化错误
		c.convert(Date2Second.class, "2010-12-01 20:30");
	}

	@Test(expected=CoreException.class)
	public void error02() {
		//字符串长度不足则格式化错误
		c.convert(Date2Second.class, "2010-12-01");
	}

}
