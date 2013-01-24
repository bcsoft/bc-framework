package cn.bc.core.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {
	SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Test
	public void testGetDateEx() throws ParseException {
		//-- yyyy-MM-dd HH:mm:ss
		Assert.assertEquals("2013-01-21 01:02:03",
				formater.format(DateUtils.getDateEx("2013-01-21 01:02:03")));
		Assert.assertEquals("2013-01-21 01:02:03",
				formater.format(DateUtils.getDateEx("2013-1-21 1:2:3")));
		
		// yyyy-MM-dd HH:mm
		Assert.assertEquals("2013-01-21 01:02:00",
				formater.format(DateUtils.getDateEx("2013-01-21 01:02")));
		Assert.assertEquals("2013-01-02 01:02:00",
				formater.format(DateUtils.getDateEx("2013-1-2 1:2")));
		
		// yyyy-MM-dd
		Assert.assertEquals("2013-01-21 00:00:00",
				formater.format(DateUtils.getDateEx("2013-01-21")));
		Assert.assertEquals("2013-01-02 00:00:00",
				formater.format(DateUtils.getDateEx("2013-1-2")));
		
		//-- yyyy/MM/dd HH:mm:ss
		Assert.assertEquals("2013-01-21 01:02:03",
				formater.format(DateUtils.getDateEx("2013/01/21 01:02:03")));
		Assert.assertEquals("2013-01-21 01:02:03",
				formater.format(DateUtils.getDateEx("2013/1/21 1:2:3")));
		
		// yyyy/MM/dd HH:mm
		Assert.assertEquals("2013-01-21 01:02:00",
				formater.format(DateUtils.getDateEx("2013/01/21 01:02")));
		Assert.assertEquals("2013-01-02 01:02:00",
				formater.format(DateUtils.getDateEx("2013/1/2 1:2")));
		
		// yyyy/MM/dd
		Assert.assertEquals("2013-01-21 00:00:00",
				formater.format(DateUtils.getDateEx("2013/01/21")));
		Assert.assertEquals("2013-01-02 00:00:00",
				formater.format(DateUtils.getDateEx("2013/1/2")));
		
		//-- yyyy.MM.dd HH:mm:ss
		Assert.assertEquals("2013-01-21 01:02:03",
				formater.format(DateUtils.getDateEx("2013.01.21 01:02:03")));
		Assert.assertEquals("2013-01-21 01:02:03",
				formater.format(DateUtils.getDateEx("2013.1.21 1:2:3")));
		
		// yyyy.MM.dd HH:mm
		Assert.assertEquals("2013-01-21 01:02:00",
				formater.format(DateUtils.getDateEx("2013.01.21 01:02")));
		Assert.assertEquals("2013-01-02 01:02:00",
				formater.format(DateUtils.getDateEx("2013.1.2 1:2")));
		
		// yyyy.MM.dd
		Assert.assertEquals("2013-01-21 00:00:00",
				formater.format(DateUtils.getDateEx("2013.01.21")));
		Assert.assertEquals("2013-01-02 00:00:00",
				formater.format(DateUtils.getDateEx("2013.1.2")));
		
		//- yyyy年MM月dd日
		Assert.assertEquals("2013-01-21 00:00:00",
				formater.format(DateUtils.getDateEx("2013年01月21日")));
		Assert.assertEquals("2013-01-02 00:00:00",
				formater.format(DateUtils.getDateEx("2013年1月2日")));
	}

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

	@Test
	public void testGetAge() throws ParseException {
		DecimalFormat f = new DecimalFormat("0.00");
		Calendar startDate = DateUtils.getCalendar("2010-11-01");
		Calendar endDate = DateUtils.getCalendar("2011-11-01");
		Assert.assertEquals("1.00",
				f.format(DateUtils.getAge(startDate, endDate)));

		startDate = DateUtils.getCalendar("2010-10-01");
		endDate = DateUtils.getCalendar("2011-12-01");
		Assert.assertEquals("1.17",
				f.format(DateUtils.getAge(startDate, endDate)));
	}

	@Test
	public void testGetAgeDetail() throws ParseException {
		Calendar startDate = DateUtils.getCalendar("2010-01-01");
		Calendar endDate = DateUtils.getCalendar("2011-01-01");
		int[] detail = DateUtils.getAgeDetail(startDate, endDate);
		Assert.assertEquals("1,0,1", detail[0] + "," + detail[1] + ","
				+ detail[2]);

		startDate = DateUtils.getCalendar("2010-01-01");
		endDate = DateUtils.getCalendar("2011-01-01");
		detail = DateUtils.getAgeDetail(startDate, endDate);
		Assert.assertEquals("1,0,1", detail[0] + "," + detail[1] + ","
				+ detail[2]);

		startDate = DateUtils.getCalendar("2010-02-01");
		endDate = DateUtils.getCalendar("2011-01-01");
		detail = DateUtils.getAgeDetail(startDate, endDate);
		Assert.assertEquals("0,11,1", detail[0] + "," + detail[1] + ","
				+ detail[2]);

		startDate = DateUtils.getCalendar("2010-01-01");
		endDate = DateUtils.getCalendar("2011-01-10");
		detail = DateUtils.getAgeDetail(startDate, endDate);
		Assert.assertEquals("1,0,10", detail[0] + "," + detail[1] + ","
				+ detail[2]);
	}
}
