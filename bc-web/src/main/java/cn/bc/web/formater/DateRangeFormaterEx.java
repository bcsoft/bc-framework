/**
 *
 */
package cn.bc.web.formater;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期范围格式化的扩展
 * <p>
 * 如输入两个日期信息，默认显示类似“2011-01-01 12:10:10～2011-02-01 14:10:15”的格式
 * </p>
 * <p>
 * 如果两个日期的年月日相同，默认显示类似“2011-01-01 12:10:10～14:10:15”的格式
 * </p>
 *
 * @author dragon
 */
public abstract class DateRangeFormaterEx extends DateRangeFormater {
  private SimpleDateFormat dateFormat;
  private SimpleDateFormat timeFormat;
  private String connector = " ";// 日期和时间间的连接字符串

  public String getConnector() {
    return connector;
  }

  public DateRangeFormaterEx setConnector(String connector) {
    this.connector = connector;
    return this;
  }

  public DateRangeFormaterEx() {
    // 默认日期格式
    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    // 默认时间格式
    timeFormat = new SimpleDateFormat("HH:mm:ss");
  }

  /**
   * 使用指定的日期和时间格式
   *
   * @param datePattern 日期部分的格式，如yyy-MM-dd
   * @param timePattern 时间部分的格式，如HH:mm:ss
   */
  public DateRangeFormaterEx(String datePattern, String timePattern) {
    this.dateFormat = new SimpleDateFormat(datePattern);
    this.timeFormat = new SimpleDateFormat(timePattern);
  }

  /**
   * 使用指定的时间格式，日期部分的格式自动设置为yyyy-MM-dd
   *
   * @param timePattern 时间部分的格式，如HH:mm:ss
   */
  public DateRangeFormaterEx(String timePattern) {
    this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    this.timeFormat = new SimpleDateFormat(timePattern);
  }

  public String format(Object context, Object value) {
    Date _fromDate = getFromDate(context, value);
    Date _toDate = getToDate(context, value);
    Calendar fromDate = null;
    if (_fromDate != null) {
      fromDate = Calendar.getInstance();
      fromDate.setTime(_fromDate);
    }
    Calendar toDate = null;
    if (_toDate != null) {
      toDate = Calendar.getInstance();
      toDate.setTime(_toDate);
    }
    if (fromDate == null) {
      if (toDate == null) {
        return "";
      } else {
        return "～" + dateFormat.format(toDate.getTime())
          + this.connector + timeFormat.format(toDate.getTime());
      }
    } else {
      if (toDate == null) {
        return dateFormat.format(fromDate.getTime()) + this.connector
          + timeFormat.format(fromDate.getTime()) + "～";
      } else {
        if (fromDate.get(Calendar.YEAR) != toDate.get(Calendar.YEAR)
          || fromDate.get(Calendar.MONTH) != toDate
          .get(Calendar.MONTH)
          || fromDate.get(Calendar.DATE) != toDate
          .get(Calendar.DATE)) {// 年月日不相同
          return dateFormat.format(fromDate.getTime())
            + this.connector
            + timeFormat.format(fromDate.getTime()) + "～"
            + dateFormat.format(toDate.getTime())
            + this.connector
            + timeFormat.format(toDate.getTime());
        } else {// 年月日相同
          return dateFormat.format(fromDate.getTime())
            + this.connector
            + timeFormat.format(fromDate.getTime()) + "～"
            + timeFormat.format(toDate.getTime());
        }
      }
    }
  }
}
