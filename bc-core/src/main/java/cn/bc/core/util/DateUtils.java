package cn.bc.core.util;

import cn.bc.core.exception.CoreException;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * 时间日期处理帮助类
 *
 * @author dragon
 */
public class DateUtils {
  private final static Logger logger = LoggerFactory.getLogger(DateUtils.class);

  /**
   * 将参数中的时间格式化为：yyyy-MM-dd
   */
  public static String formatDate(Date date) {
    if (null == date)
      return "";
    FastDateFormat df = FastDateFormat.getInstance("yyyy-MM-dd");
    return df.format(date);
  }

  /**
   * 将参数中的时间格式化为：yyyy-MM-dd
   */
  public static String formatCalendar2Day(Calendar calendar) {
    if (null == calendar)
      return "";
    FastDateFormat df = FastDateFormat.getInstance("yyyy-MM-dd");
    return df.format(calendar.getTime());
  }

  /**
   * 将参数中的时间格式化为：yyyy-MM-dd HH:mm:ss
   */
  public static String formatDateTime(Date date) {
    return formatDateTime(date, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * 将参数中的时间格式化为：yyyy-MM-dd HH:mm:ss
   */
  public static String formatCalendar2Second(Calendar calendar) {
    return formatDateTime(calendar != null ? calendar.getTime() : null, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * 将参数中的时间格式化为：yyyy-MM-dd HH:mm
   */
  public static String formatDateTime2Minute(Date date) {
    return formatDateTime(date, "yyyy-MM-dd HH:mm");
  }

  /**
   * 将参数中的时间格式化为：yyyy-MM-dd HH:mm
   */
  public static String formatCalendar2Minute(Calendar calendar) {
    return formatDateTime(calendar != null ? calendar.getTime() : null, "yyyy-MM-dd HH:mm");
  }

  /**
   * 将参数中的时间格式化为指定的格式
   *
   * @param date   日期
   * @param format 格式
   */
  public static String formatDateTime(Date date, String format) {
    if (null == date)
      return "";
    FastDateFormat df = FastDateFormat.getInstance(format);
    return df.format(date);
  }

  /**
   * 将参数中的时间格式化为指定的格式
   */
  public static String formatCalendar(Calendar calendar, String format) {
    if (null == calendar)
      return "";
    FastDateFormat df = FastDateFormat.getInstance(format);
    return df.format(calendar.getTime());
  }

  /**
   * 取得指定日期中的小时数
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
   * @param dateTime 所要转换的时间
   */
  public static Calendar getCalendar(String dateTime) {
    Date date = getDate(dateTime);
    Calendar to = Calendar.getInstance();
    if (date != null) {
      to.setTime(date);
      return to;
    } else {
      return null;
    }
  }

  /**
   * 指定的字符串时间转换成Date
   *
   * @param dateTime 所要转换的时间
   */
  public static Date getDate(String dateTime) {
    if (null == dateTime || dateTime.length() == 0)
      return null;
    FastDateFormat df;
    if (dateTime.length() == "yyyy-MM-dd HH:mm:ss".length()) {
      df = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    } else if (dateTime.length() == "yyyy-MM-dd HH:mm".length()) {
      df = FastDateFormat.getInstance("yyyy-MM-dd HH:mm");
    } else if (dateTime.length() == "yyyy-MM-dd".length()) {
      df = FastDateFormat.getInstance("yyyy-MM-dd");
    } else if (dateTime.length() == "yyyy-M-d".length()) {
      df = FastDateFormat.getInstance("yyyy-MM-dd");
    } else if (dateTime.length() == "yyyy-MM-d".length()) {
      df = FastDateFormat.getInstance("yyyy-MM-dd");
    } else if (dateTime.length() == "yyyy-M-dd".length()) {
      df = FastDateFormat.getInstance("yyyy-MM-dd");
    } else {
      return null;
    }

    try {
      return df.parse(dateTime);
    } catch (ParseException pe) {
      logger.error("Unparseable date: dateTime=" + dateTime + ",error="
        + pe.getMessage());
      throw new CoreException("Unparseable date: " + dateTime, pe);
    }
  }

  public static Map<String, String> dateFormats;

  static {
    dateFormats = new LinkedHashMap<>();
    dateFormats.put("yyyy-MM-dd HH:mm:ss",
      "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$");
    dateFormats.put("yyyy-MM-dd HH:mm",
      "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$");
    dateFormats.put("yyyy-MM-dd", "^\\d{4}-\\d{1,2}-\\d{1,2}$");

    dateFormats.put("yyyy/MM/dd HH:mm:ss",
      "^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$");
    dateFormats.put("yyyy/MM/dd HH:mm",
      "^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}$");
    dateFormats.put("yyyy/MM/dd", "^\\d{4}/\\d{1,2}/\\d{1,2}$");

    dateFormats.put("yyyy.MM.dd HH:mm:ss",
      "^\\d{4}.\\d{1,2}.\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$");
    dateFormats.put("yyyy.MM.dd HH:mm",
      "^\\d{4}.\\d{1,2}.\\d{1,2} \\d{1,2}:\\d{1,2}$");
    dateFormats.put("yyyy.MM.dd", "^\\d{4}.\\d{1,2}.\\d{1,2}$");

    dateFormats.put("yyyy年MM月dd日", "^\\d{4}年\\d{1,2}月\\d{1,2}日$");
  }

  /**
   * 指定的字符串时间转换成Date
   * <p>
   * yyyy-MM-dd HH:mm:ss|yyyy.MM.dd HH:mm:ss|yyyy年MM月dd日
   * </p>
   *
   * @param dateTime 所要转换的时间
   */
  public static Date getDateEx(String dateTime) {
    if (dateTime == null)
      return null;
    dateTime = dateTime.trim();
    if (dateTime.isEmpty())
      return null;

    for (Entry<String, String> e : dateFormats.entrySet()) {
      if (Pattern.matches(e.getValue(), dateTime)) {
        FastDateFormat df4ex = FastDateFormat.getInstance(e.getKey());
        try {
          return df4ex.parse(dateTime);
        } catch (ParseException e1) {
          logger.warn("Unparseable date: dateTime=" + dateTime
            + ",error=" + e1.getMessage());
          throw new CoreException("Unparseable date: " + dateTime, e1);
        }
      }
    }
    logger.warn("can't parse this string to date: " + dateTime);
    throw new CoreException("can't parse this string to Date: " + dateTime);
  }

  /**
   * 指定的字符串时间转换成Calendar
   * <p>
   * yyyy-MM-dd HH:mm:ss|yyyy.MM.dd HH:mm:ss|yyyy年MM月dd日
   * </p>
   *
   * @param dateTime 所要转换的时间
   */
  public static Calendar getCalendarEx(String dateTime) {
    Date date = getDateEx(dateTime);
    Calendar to = Calendar.getInstance();
    to.setTime(date);
    return to;
  }

  /**
   * 获取月份的第一天(类似yyyy-MM-01 00:00:00)
   *
   * @param calendar 要处理的日期
   */
  public static Calendar getFirstDayOfMonth(Calendar calendar) {
    if (calendar == null)
      return null;
    Calendar to = Calendar.getInstance();
    to.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
    to.set(Calendar.MILLISECOND, 0);
    return to;
  }

  /**
   * 获取月份的最后一天(类似yyyy-MM-dd 23:59:59)
   *
   * @param calendar 要处理的日期
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
   * @param calendar 要处理的日期
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
   * @param calendar 要处理的日期
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
   * @param calendar 要处理的日期
   */
  public static void setToZeroTime(Calendar calendar) {
    if (calendar == null)
      return;
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
  }

  /**
   * 将日期时分秒设为0(类似yyyy-MM-dd 00:00:00)
   *
   * @param date 要处理的日期
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
   * @param calendar 要处理的日期
   */
  public static void setToMaxTime(Calendar calendar) {
    if (calendar == null)
      return;
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
  }

  /**
   * 将日期时分秒设为最大值(类似yyyy-MM-dd 23:59:59)
   *
   * @param date 要处理的日期
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
   * @param date 指定的日期
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
   * @param date 指定日期
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
   * @param fromDate 开始时间
   */
  public static String getWasteTime(Date fromDate) {
    return getWasteTime(fromDate, new Date());
  }

  public static String getWasteTimeCN(Date fromDate) {
    return getWasteTimeCN(fromDate, new Date());
  }

  /**
   * 计算指定时间范围内的耗时描述信息
   *
   * @param startDate 开始时间
   * @param endDate   结束时间
   */
  public static String getWasteTime(Date startDate, Date endDate) {
    long wt = endDate.getTime() - startDate.getTime();
    return getWasteTime(wt);
  }

  public static String getWasteTimeCN(Date startDate, Date endDate) {
    long wt = endDate.getTime() - startDate.getTime();
    return getWasteTimeCN(wt);
  }

  /**
   * 计算指定时间范围内的耗时描述信息
   *
   * @param wt 毫秒值
   */
  public static String getWasteTime(long wt) {
    if (wt < 1000) {// 小于1秒
      return wt + "ms";
    } else if (wt < 60000) {// 小于1分钟
      long ms = wt % 1000;
      return ((wt - ms) / 1000) + "s " + ms + "ms";
    } else {// 大于1分钟
      if (wt < 3600000) {// 小于1小时
        long ms = wt % 1000;
        long s = ((wt - ms) / 1000 % 60);
        return ((wt - ms - s * 1000) / 60000) + "m " + s + "s";
      } else if (wt < 86400000) {// 小于1天
        long m = (wt / 60000 % 60);
        return (wt / 3600000) + "h " + m + "m";
      } else {// 大于1天
        long m = (wt / 60000 % 60);
        long h = (wt / 3600000) % 24;
        return (wt / 86400000) + "d " + h + "h " + m + "m";
      }
    }
  }

  public static String getWasteTimeCN(long wt) {
    if (wt < 1000) {// 小于1秒
      return wt + "毫秒";
    } else if (wt < 60000) {// 小于1分钟
      long ms = wt % 1000;
      return ((wt - ms) / 1000) + "秒 " + ms + "毫秒";
    } else {// 大于1分钟
      if (wt < 3600000) {// 小于1小时
        long ms = wt % 1000;
        long s = ((wt - ms) / 1000 % 60);
        return ((wt - ms - s * 1000) / 60000) + "分钟 " + s + "秒";
      } else if (wt < 86400000) {// 小于1天
        long m = (wt / 60000 % 60);
        return (wt / 3600000) + "小时 " + m + "分钟";
      } else {// 大于1天
        long m = (wt / 60000 % 60);
        long h = (wt / 3600000) % 24;
        return (wt / 86400000) + "天 " + h + "小时 " + m + "分钟";
      }
    }
  }

  /**
   * 计算两个日期之间的年龄:以实际足月年龄计算
   *
   * @param startDate 开始日期
   * @param endDate   结束日期
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
   * @param startDate 开始日期
   * @param endDate   结束日期
   */
  public static float getAge(Calendar startDate, Calendar endDate) {
    int startYear = startDate.get(Calendar.YEAR);// 获取日期的年份
    int startMonth = startDate.get(Calendar.MONTH);
    int endYear = endDate.get(Calendar.YEAR);
    int endMonth = endDate.get(Calendar.MONTH);

    // 月份的天数相加
    int day = getLastDayOfMonth(startDate).get(Calendar.DATE)
      - startDate.get(Calendar.DATE) + 1 + endDate.get(Calendar.DATE);

    return (endYear - startYear - 1) + (11 - startMonth + endMonth + (float) day / 30) / 12;
  }

  /**
   * 计算指定出生日期的当前年龄:以实际足月年龄计算
   *
   * @param birthDate 出生日期
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
   * @param startDate 开始日期
   * @param endDate   结束日期
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

  /**
   * 计算指定时间为星期几
   */
  public static String getWeekCN(Calendar date) {
    if (date == null)
      date = Calendar.getInstance();

    int week = date.get(Calendar.DAY_OF_WEEK);

    if (week == Calendar.MONDAY) {
      return "星期一";
    } else if (week == Calendar.TUESDAY) {
      return "星期二";
    } else if (week == Calendar.WEDNESDAY) {
      return "星期三";
    } else if (week == Calendar.THURSDAY) {
      return "星期四";
    } else if (week == Calendar.FRIDAY) {
      return "星期五";
    } else if (week == Calendar.SATURDAY) {
      return "星期六";
    } else if (week == Calendar.SUNDAY) {
      return "星期日";
    }

    return null;
  }

  /**
   * 获取下个月份的第一天(类似yyyy-MM-01 00:00:00)
   *
   * @param calendar 要处理的日期
   */
  public static Calendar getFirstDayOfNextMonth(Calendar calendar) {
    if (calendar == null)
      return null;
    Calendar to = getFirstDayOfMonth(calendar);//本月第一天
    to.add(Calendar.MONTH, 1);//下月第一天
    return to;
  }

  /**
   * 获取下个月份的最后一天(类似yyyy-MM-dd 23:59:59)
   *
   * @param calendar 要处理的日期
   */
  public static Calendar getLastDayOfNextMonth(Calendar calendar) {
    if (calendar == null)
      return null;
    Calendar to = getFirstDayOfNextMonth(calendar);// 下月第一天
    to.add(Calendar.MONTH, 1);// 下下月第一天
    to.add(Calendar.SECOND, -1);// 缩减1秒变为上月最后一天
    return to;
  }

  /**
   * 获取根据幅度增加后的日期
   *
   * @param calendar    要处理的日期
   * @param rangeConfig 幅度 ：	1h 表示增加1小时
   *                    1d 表示增加1天
   *                    1m 表示增加1个月
   *                    1y 表示增加1年
   */
  public static Calendar getDate4Range(Calendar calendar, String rangeConfig) {
    if (calendar == null)
      return null;
    Calendar to = Calendar.getInstance();
    to.set(calendar.get(Calendar.YEAR)
      , calendar.get(Calendar.MONTH)
      , calendar.get(Calendar.DAY_OF_MONTH)
      , calendar.get(Calendar.HOUR_OF_DAY)
      , calendar.get(Calendar.MINUTE)
      , calendar.get(Calendar.SECOND));

    if (rangeConfig.matches("\\b\\d*h")) {//1h
      to.add(Calendar.HOUR_OF_DAY, Integer.valueOf(rangeConfig.replace("h", "")));
    } else if (rangeConfig.matches("\\b\\d*d")) {//1d
      to.add(Calendar.DAY_OF_MONTH, Integer.valueOf(rangeConfig.replace("d", "")));
    } else if (rangeConfig.matches("\\b\\d*m")) {
      to.add(Calendar.MONTH, Integer.valueOf(rangeConfig.replace("m", "")));
    } else if (rangeConfig.matches("\\b\\d*y")) {
      to.add(Calendar.YEAR, Integer.valueOf(rangeConfig.replace("y", "")));
    }

    return to;
  }

  /**
   * 判断日期是否为月末
   *
   * @return true 是月末  false非月末
   */
  public static boolean isMonthEnd(Calendar calendar) {
    if (calendar == null)
      return false;
    Calendar date = Calendar.getInstance();
    date.set(calendar.get(Calendar.YEAR)
      , calendar.get(Calendar.MONTH)
      , calendar.get(Calendar.DAY_OF_MONTH)
      , calendar.get(Calendar.HOUR_OF_DAY)
      , calendar.get(Calendar.MINUTE)
      , calendar.get(Calendar.SECOND));
    int month = date.get(Calendar.MONTH);
    //加1日
    date.add(Calendar.DAY_OF_MONTH, 1);
    int _month = date.get(Calendar.MONTH);
    return month != _month;
  }
}