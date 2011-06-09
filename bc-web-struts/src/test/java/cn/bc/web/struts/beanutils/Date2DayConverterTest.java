package cn.bc.web.struts.beanutils;

import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.beanutils.Converter;
import org.junit.Before;
import org.junit.Test;

import cn.bc.core.exception.CoreException;
import cn.bc.web.struts.beanutils.Date2Day;
import cn.bc.web.struts.beanutils.Date2DayConverter;


public class Date2DayConverterTest {
	private Converter c;

	@Before
	public void setUp() {
		c = new Date2DayConverter();
	}

	@Test
	public void Null_Date2Day() {
		Assert.assertNull(c.convert(null, null));
		Assert.assertNull(c.convert(String.class, null));
		Assert.assertNull(c.convert(Date.class, null));
		Assert.assertNull(c.convert(Date2Day.class, null));
	}

	@Test
	public void String_Date2Day() {
		Assert.assertNull(c.convert(Date2Day.class, ""));
		Object value = c.convert(Date2Day.class, "2010-12-01");
		Assert.assertNotNull(value);
		Assert.assertEquals(Date2Day.class, value.getClass());
		Assert.assertEquals("2010-12-01", value.toString());

		value = c.convert(Date2Day.class, "2010-12-01abcd");// 超过格式串部分的字符将被忽略
		Assert.assertNotNull(value);
		Assert.assertEquals(Date2Day.class, value.getClass());
		Assert.assertEquals("2010-12-01", value.toString());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void Date_Date2Day() {
		Date date = new Date(2010 - 1900, 12 - 1, 1, 20, 27, 30);
		Object value = c.convert(Date2Day.class, date);
		Assert.assertNotNull(value);
		Assert.assertEquals(Date2Day.class, value.getClass());
		Assert.assertEquals("2010-12-01", value.toString());
	}

	@Test(expected = CoreException.class)
	public void error01() {
		// 字符串长度不足则格式化错误
		c.convert(Date2Day.class, "2010-12");
	}

	@Test(expected = CoreException.class)
	public void error02() {
		// 字符串长度不足则格式化错误
		c.convert(Date2Day.class, "2010");
	}

}
