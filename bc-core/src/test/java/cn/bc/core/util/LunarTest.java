package cn.bc.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

public class LunarTest {
	SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年M月d日");

	@Test
	public void testToday() throws ParseException {
		Calendar date = Calendar.getInstance();
		Lunar lunar = new Lunar(date);
		System.out.println("今天是新历：" + chineseDateFormat.format(date.getTime()));
		System.out.println("农历：" + lunar.toString() + "，生肖："
				+ lunar.animalsYear() + "，干支：" + lunar.cyclical());
	}

	@Test
	public void testMain() throws ParseException {
		Calendar date = Calendar.getInstance();
		date.setTime(chineseDateFormat.parse("2011年1月1日"));
		Lunar lunar = new Lunar(date);

		Assert.assertEquals("2011年1月1日",
				chineseDateFormat.format(date.getTime()));
		Assert.assertEquals("2010年十一月廿七", lunar.toString());
	}
}
