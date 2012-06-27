package cn.bc.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 时间日期处理帮助类
 * 
 * @author dragon
 */
public class DateUtils {
	private final static Log logger = LogFactory.getLog(DateUtils.class);

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
	 * 将参数中的时间格式化为：yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String formatCalendar2Day(Calendar calendar) {
		if (null == calendar)
			return "";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(calendar.getTime());
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
	 * 将参数中的时间格式化为：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String formatCalendar2Second(Calendar calendar) {
		return formatDateTime(calendar != null ? calendar.getTime() : null,
				"yyyy-MM-dd HH:mm:ss");
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
	 * 将参数中的时间格式化为：yyyy-MM-dd HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String formatCalendar2Minute(Calendar calendar) {
		return formatDateTime(calendar != null ? calendar.getTime() : null,
				"yyyy-MM-dd HH:mm");
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
	 * 将参数中的时间格式化为指定的格式
	 * 
	 * @param date
	 * @param format
	 *            格式
	 * @return
	 */
	public static String formatCalendar(Calendar calendar, String format) {
		if (null == calendar)
			return "";
		DateFormat df = new SimpleDateFormat(format);
		return df.format(calendar.getTime());
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
	 * 指定的字符串时间转换成Calendar
	 * 
	 * @param dateTime
	 *            所要转换的时间
	 */
	public static Calendar getCalendar(String dateTime) {
		Date date = getDate(dateTime);
		Calendar to = Calendar.getInstance();
		to.setTime(date);
		return to;
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
			logger.error("can't parse to date,return null:dateTime=" + dateTime
					+ ",error=" + pe.getMessage());
			return null;
		}
	}

	/**
	 * 获取月份的第一天(类似yyyy-MM-01 00:00:00)
	 * 
	 * @param calendar
	 *            要处理的日期
	 */
	public static Calendar getFirstDayOfMonth(Calendar calendar) {
		if (calendar == null)
			return null;
		Calendar to = Calendar.getInstance();
		to.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0,
				0, 0);
		to.set(Calendar.MILLISECOND, 0);
		return to;
	}

	/**
	 * 获取月份的最后一天(类似yyyy-MM-dd 23:59:59)
	 * 
	 * @param calendar
	 *            要处理的日期
	 */
	public static Calendar getLastDayOfMonth(Calendar calendar) {
		if (calendar == null)
			return null;
		Calendar to = getFirstDayOfMonth(calendar);// 本月第一天
		to.add(Calendar.MONTH, 1);// 下月第一天
		to.add(Calendar.SECOND, -1);// 缩减1秒变为上月最后一天
		return to;
	}

	/**
	 * 将日期设为其所在月份的第一天(类似yyyy-MM-01 00:00:00)
	 * 
	 * @param calendar
	 *            要处理的日期
	 */
	public static void setToFirstDayOfMonth(Calendar calendar) {
		if (calendar == null)
			return;
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				1, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * 将日期设为其所在月份的最后一天(类似yyyy-MM-dd 23:59:59)
	 * 
	 * @param calendar
	 *            要处理的日期
	 */
	public static void setToLastDayOfMonth(Calendar calendar) {
		if (calendar == null)
			return;
		setToFirstDayOfMonth(calendar);
		calendar.add(Calendar.MONTH, 1);// 下月第一天
		calendar.add(Calendar.SECOND, -1);// 缩减1秒变为上月最后一天
	}

	/**
	 * 将日期时分秒设为0(类似yyyy-MM-dd 00:00:00)
	 * 
	 * @param calendar
	 *            要处理的日期
	 */
	public static void setToZeroTime(Calendar calendar) {
		if (calendar == null)
			return;
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
	}

	/**
	 * 将日期时分秒设为0(类似yyyy-MM-dd 00:00:00)
	 * 
	 * @param date
	 *            要处理的日期
	 */
	@SuppressWarnings("deprecation")
	public static void setToZeroTime(Date date) {
		if (date == null)
			return;
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
	}

	/**
	 * 将日期时分秒设为最大值(类似yyyy-MM-dd 23:59:59)
	 * 
	 * @param calendar
	 *            要处理的日期
	 */
	public static void setToMaxTime(Calendar calendar) {
		if (calendar == null)
			return;
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
	}

	/**
	 * 将日期时分秒设为最大值(类似yyyy-MM-dd 23:59:59)
	 * 
	 * @param date
	 *            要处理的日期
	 */
	@SuppressWarnings("deprecation")
	public static void setToMaxTime(Date date) {
		if (date == null)
			return;
		date.setHours(23);
		date.setMinutes(59);
		date.setSeconds(59);
	}

	/**
	 * 获取指定日期的起始时间(类似yyyy-MM-dd 00:00:00)
	 * 
	 * @param date
	 *            指定的日期
	 * @return 转换后的日期
	 */
	public static Date getZeroTimeDate(Date date) {
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
	public static Date getMaxTimeDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 计算指定时间到当前时间之间的耗时描述信息
	 * 
	 * @param fromDate
	 *            开始时间
	 * @return
	 */
	public static String getWasteTime(Date fromDate) {
		return getWasteTime(fromDate, new Date());
	}

	/**
	 * 计算指定时间范围内的耗时描述信息
	 * 
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @return
	 */
	public static String getWasteTime(Date startDate, Date endDate) {
		long wt = endDate.getTime() - startDate.getTime();
		return getWasteTime(wt);
	}

	/**
	 * 计算指定时间范围内的耗时描述信息
	 * 
	 * @param wt
	 *            毫秒值
	 * @return
	 */
	public static String getWasteTime(long wt) {
		if (wt < 1000) {
			return wt + "ms";
		} else if (wt < 60000) {
			long ms = wt % 1000;
			return ((wt - ms) / 1000) + "s " + ms + "ms";
		} else {
			long ms = wt % 1000;
			long s = (wt - ms) % 60;
			return ((wt - s - ms) / 60000) + "m " + s + "s " + ms + "ms";
		}
	}

	/**
	 * 计算两个日期之间的年龄:以实际足月年龄计算
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	public static float getAge(Date startDate, Date endDate) {
		Calendar start = Calendar.getInstance();// 创建新日期
		start.setTime(startDate);// 将日期设置为需要的日期
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		return getAge(start, end);
	}

	/**
	 * 计算两个日期之间的年龄:以实际足月年龄计算
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	public static float getAge(Calendar startDate, Calendar endDate) {
		int startYeay = startDate.get(Calendar.YEAR);// 获取日期的年份
		int startMonth = startDate.get(Calendar.MONTH);
		int endYeay = endDate.get(Calendar.YEAR);
		int endMonth = endDate.get(Calendar.MONTH);

		// 月份的天数相加
		int dday = getLastDayOfMonth(startDate).get(Calendar.DATE)
				- startDate.get(Calendar.DATE) + 1 + endDate.get(Calendar.DATE);

		return (endYeay - startYeay - 1)
				+ ((float) (11 - startMonth + endMonth + (float) dday / 30))
				/ 12;
	}

	/**
	 * 计算指定出生日期的当前年龄:以实际足月年龄计算
	 * 
	 * @param birthDate
	 *            出生日期
	 * @return
	 */
	public static float getAge(Calendar birthDate) {
		return getAge(birthDate, Calendar.getInstance());
	}

	/**
	 * 计算两个日期之间的年龄细节
	 * <p>
	 * 如1年零两个月加10天
	 * </p>
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 0-实际流逝的年数,1-不足一年的月数,2-不足一月的天数
	 */
	public static int[] getAgeDetail(Calendar startDate, Calendar endDate) {
		int startYeay = startDate.get(Calendar.YEAR);// 获取日期的年份
		int startMonth = startDate.get(Calendar.MONTH);
		int endYeay = endDate.get(Calendar.YEAR);
		int endMonth = endDate.get(Calendar.MONTH);

		int[] detail = new int[3];
		detail[0] = endYeay - startYeay - 1;
		detail[1] = 11 - startMonth + endMonth;
		int maxDay = getLastDayOfMonth(startDate).get(Calendar.DATE);
		detail[2] = maxDay - startDate.get(Calendar.DATE) + 1
				+ endDate.get(Calendar.DATE);

		if (detail[2] >= maxDay) {
			detail[1] += 1;
			detail[2] -= maxDay;
		}
		if (detail[1] >= 12) {
			detail[0] += 1;
			detail[1] -= 12;
		}
		return detail;
	}
}