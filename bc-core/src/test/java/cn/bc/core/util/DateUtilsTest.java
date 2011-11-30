package cn.bc.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {
	SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Test
	public void testSetToZeroTime() throws ParseException {
		Calendar date = DateUtils.getCalendar("2011-11-01 10:20:30");
		DateUtils.setToZeroTime(date);
		Assert.assertEquals("2011-11-01 00:00:00",
				formater.format(date.getTime()));
	}

	@Test
	public void testSetToMaxTime() throws ParseException {
		Calendar date = DateUtils.getCalendar("2011-11-01 10:20:30");
		DateUtils.setToMaxTime(date);
		Assert.assertEquals("2011-11-01 23:59:59",
				formater.format(date.getTime()));
	}

	@Test
	public void testGetFirstDayOfMonth() throws ParseException {
		Calendar date = DateUtils.getCalendar("2011-11-01 10:20:30");
		date = DateUtils.getFirstDayOfMonth(date);
		Assert.assertEquals("2011-11-01 00:00:00",
				formater.format(date.getTime()));
	}
	
	@Test
	public void testGetLastDayOfMonth() throws ParseException {
		Calendar date = DateUtils.getCalendar("2011-11-01 10:20:30");
		date = DateUtils.getLastDayOfMonth(date);
		Assert.assertEquals("2011-11-30 23:59:59",
				formater.format(date.getTime()));
	}

	@Test
	public void testSetToFirstDayOfMonth() throws ParseException {
		Calendar date = DateUtils.getCalendar("2011-11-01 10:20:30");
		DateUtils.setToFirstDayOfMonth(date);
		Assert.assertEquals("2011-11-01 00:00:00",
				formater.format(date.getTime()));
	}

	@Test
	public void testSetToLastDayOfMonth() throws ParseException {
		Calendar date = DateUtils.getCalendar("2011-11-01 10:20:30");
		DateUtils.setToLastDayOfMonth(date);
		Assert.assertEquals("2011-11-30 23:59:59",
				formater.format(date.getTime()));
	}
}
