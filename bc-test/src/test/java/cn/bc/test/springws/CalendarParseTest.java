package cn.bc.test.springws;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class CalendarParseTest {

	@Before
	public void setUp() throws Exception {
	}

	@SuppressWarnings("deprecation")
	@Test
	public void test() throws Exception {
		String source = "2011-08-09T10:52:04.0000000+08:00".replace("T", " ").replace("+08:00", "");
		Assert.assertEquals("2011-08-09 10:52:04.0000000",source);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date t = df.parse(source);
		Assert.assertEquals(2011 - 1900, t.getYear());
		Assert.assertEquals(7, t.getMonth());
		Assert.assertEquals(9, t.getDate());
		Assert.assertEquals(10, t.getHours());
		Assert.assertEquals(52, t.getMinutes());
		Assert.assertEquals(4, t.getSeconds());
	}
}
