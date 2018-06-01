package cn.bc.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * 年龄计算工具类
 *
 * @author dragon 2016-02-22
 */
public class AgeUtils {
  private final static Logger logger = LoggerFactory.getLogger(AgeUtils.class);

  /**
   * 按公历计算周岁年龄
   *
   * @param birthDate 出生日期
   * @return 按公历计算的周岁年龄
   */
  public static int getFullAge(Date birthDate) {
    return getFullAge(birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
  }

  /**
   * 按公历计算周岁年龄
   *
   * @param birthDate 出生日期
   * @return 按公历计算的周岁年龄
   */
  public static int getFullAge(Calendar birthDate) {
    return getFullAge(birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
  }

  /**
   * 按公历计算周岁年龄
   *
   * @param birthDate 出生日期
   * @return 按公历计算的周岁年龄
   */
  public static int getFullAge(LocalDate birthDate) {
    return getFullAge(birthDate, LocalDate.now());
  }

  /**
   * 按公历计算两个日期之间的周岁年龄值
   *
   * @param fromDate 开始日期
   * @param toDate   结束日期
   * @return 按公历计算的周岁年龄
   */
  public static int getFullAge(LocalDate fromDate, LocalDate toDate) {
    if (fromDate.isAfter(toDate)) {
      logger.warn("fromDate greater then toDate, just return 0. fromDate={}, toDate={}", fromDate, toDate);
      //throw new IllegalArgumentException("fromDate can not after toDate");
      return 0;
    }
    int y = toDate.getYear() - fromDate.getYear();
    if (y == 0) {               // 同年：不足 1 周岁即 0 周岁
      return 0;
    } else {                    // 跨年
      int m = toDate.getMonthValue() - fromDate.getMonthValue();
      if (m < 0) {            //-- 按月计不足 12 个月
        return y - 1;
      } else if (m > 0) {
        return y;
      } else {
        int d = toDate.getDayOfMonth() - fromDate.getDayOfMonth();
        if (d < 0) {        //-- 按日计不足 12 个月
          return y - 1;
        } else {
          return y;
        }
      }
    }
  }

  /**
   * 按公历计算周岁年龄
   *
   * @param birthDate 出生日期
   * @return 按公历计算的周岁年龄
   */
  public static Period getAge(LocalDate birthDate) {
    return getAge(birthDate, LocalDate.now());
  }

  /**
   * 按公历计算确切年龄
   *
   * @param fromDate 开始日期
   * @param toDate   结束日期
   * @return 按公历计算的确切年龄: 如 1年3个月零4日
   */
  public static Period getAge(LocalDate fromDate, LocalDate toDate) {
    if (fromDate.isAfter(toDate)) throw new IllegalArgumentException("fromDate can not after toDate");
    return Period.between(fromDate, toDate);
  }
}