package cn.bc.core.util;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

  @Test
  public void testA() throws ParseException {
    String n = "叶燕明[252315]";
    Assert.assertEquals("叶燕明", removeCert4DriverName(n));
  }

  /**
   * 将类似“叶燕明[252315]”的格式转换为“叶燕明”
   *
   * @param DRIVER_NAME
   * @return
   */
  protected String removeCert4DriverName(Object DRIVER_NAME) {
    if (DRIVER_NAME == null)
      return null;
    String n = DRIVER_NAME.toString().trim();
    if (n.indexOf("[") != -1) {
      return n.substring(0, n.indexOf("["));
    } else {
      return n;
    }
  }
}
