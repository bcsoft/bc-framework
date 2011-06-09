package cn.bc.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间日期处理帮助类
 * 
 * @author dragon
 */
public class DateUtils {
	/**
	 * 将参数中的时间格式化为：yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		if (null == date)
			return "";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}

	/**
	 * 将参数中的时间格式化为：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateTime(Date date) {
		return formatDateTime(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 将参数中的时间格式化为：yyyy-MM-dd HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateTime2Minute(Date date) {
		return formatDateTime(date, "yyyy-MM-dd HH:mm");
	}

	/**
	 * 将参数中的时间格式化为指定的格式
	 * 
	 * @param date
	 * @param format
	 *            格式
	 * @return
	 */
	public static String formatDateTime(Date date, String format) {
		if (null == date)
			return "";
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	/**
	 * 取得指定日期中的小时数
	 * 
	 * @param date
	 * @return
	 */
	public static int getHour(Date date) {
		if (null == date)
			return 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 取得指定日期中的分钟数
	 * 
	 * @param date
	 * @return
	 */
	public static int getMinute(Date date) {
		if (null == date)
			return 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MINUTE);
	}

	/**
	 * 指定的字符串时间转换成Date
	 * 
	 * @param dateTime
	 *            所要转换的时间
	 */
	public static Date getDate(String dateTime) {
		if (null == dateTime || dateTime.length() == 0)
			return null;
		DateFormat df = null;
		if (dateTime.length() == "yyyy-MM-dd HH:mm:ss".length()) {
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else if (dateTime.length() == "yyyy-MM-dd HH:mm".length()) {
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		} else if (dateTime.length() == "yyyy-MM-dd".length()) {
			df = new SimpleDateFormat("yyyy-MM-dd");
		} else {
			return null;
		}

		try {
			return df.parse(dateTime);
		} catch (ParseException pe) {
			return null;
		}
	}

	/**
	 * 获取指定日期的起始时间(类似yyyy-MM-dd 00:00:00)
	 * 
	 * @param date
	 *            指定日期
	 * @return 转换后的日期
	 */
	public static Date getStartDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取指定日期的结束时间(类似yyyy-MM-dd 23:59:59)
	 * 
	 * @param date
	 *            指定日期
	 * @return 转换后的日期
	 */
	public static Date getEndDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}
}