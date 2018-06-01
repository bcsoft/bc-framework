package cn.bc.core.util;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

public class AgeUtilsTest {
  @Test
  public void testGetFullAge4LocalDateDetails() {
    int year = 2010;

    // 0 周岁
    for (int m = 1; m <= 12; m++) {
      Assert.assertEquals(0, AgeUtils.getFullAge(LocalDate.of(year, 1, 1), LocalDate.of(year, m, 1)));
    }

    // 1 周岁：下一年相同月或此月之后不论哪个月、不论哪一天
    LocalDate toDate;
    int lastDay;
    for (int m = 1; m <= 12; m++) {
      toDate = LocalDate.of(year + 1, m, 1);
      lastDay = toDate.plusMonths(1).minusDays(1).getDayOfMonth();
      for (int d = 1; d <= lastDay; d++) {
        Assert.assertEquals(1, AgeUtils.getFullAge(LocalDate.of(year, 1, 1), LocalDate.of(year + 1, m, d)));
      }
    }

    // 2 周岁
    Assert.assertEquals(2, AgeUtils.getFullAge(LocalDate.of(year, 1, 1), LocalDate.of(year + 2, 1, 1)));
    Assert.assertEquals(2, AgeUtils.getFullAge(LocalDate.of(year, 2, 10), LocalDate.of(year + 2, 2, 10)));
    Assert.assertEquals(2, AgeUtils.getFullAge(LocalDate.of(year, 2, 10), LocalDate.of(year + 2, 12, 31)));
    Assert.assertEquals(1, AgeUtils.getFullAge(LocalDate.of(year, 2, 10), LocalDate.of(year + 2, 2, 9)));
  }

  @Test
  public void testGetFullAge4LocalDate() {
    int currentYear = LocalDate.now().getYear();
    int birthYear = 2010;
    LocalDate birthDate = LocalDate.of(birthYear, 1, 1);
    Assert.assertEquals(currentYear - birthYear, AgeUtils.getFullAge(birthDate));
  }

  @Test
  public void tesFullAge4Calendar() {
    Calendar birthDate = Calendar.getInstance();
    int currentYear = birthDate.get(Calendar.YEAR);
    int birthYear = 2010;
    birthDate.set(Calendar.YEAR, birthYear);
    birthDate.set(Calendar.MONTH, 0);
    birthDate.set(Calendar.DATE, 1);
    Assert.assertEquals(currentYear - birthYear, AgeUtils.getFullAge(birthDate));
  }

  @Test
  public void tesFullAge4Date() {
    Calendar birthDate = Calendar.getInstance();
    int currentYear = birthDate.get(Calendar.YEAR);
    int birthYear = 2010;
    birthDate.set(Calendar.YEAR, birthYear);
    birthDate.set(Calendar.MONTH, 0);
    birthDate.set(Calendar.DATE, 1);
    Assert.assertEquals(currentYear - birthYear, AgeUtils.getFullAge(birthDate.getTime()));
  }

  @Test
  public void testGetAge() {
    LocalDate now = LocalDate.now();
    int currentYear = now.getYear();
    LocalDate birthDate = LocalDate.of(2010, 2, 1);
    Period age = AgeUtils.getAge(birthDate); // P6Y1M21D
    Assert.assertEquals(currentYear - birthDate.getYear(), age.getYears());
    Assert.assertEquals(now.getMonthValue() - birthDate.getMonthValue(), age.getMonths());
    Assert.assertEquals(now.getDayOfMonth() - birthDate.getDayOfMonth(), age.getDays());
  }

  @Test
  public void testGetAgeDetails() {
    LocalDate to = LocalDate.of(2011, 1, 10);

    LocalDate from = LocalDate.of(2010, 1, 10);
    Period age = AgeUtils.getAge(from, to);
    Assert.assertEquals(1, age.getYears());
    Assert.assertEquals(0, age.getMonths());
    Assert.assertEquals(0, age.getDays());

    from = LocalDate.of(2010, 1, 11);
    age = AgeUtils.getAge(from, to);
    Assert.assertEquals(0, age.getYears());
    Assert.assertEquals(11, age.getMonths());
    Assert.assertEquals(30, age.getDays());

    from = LocalDate.of(2010, 5, 11);
    age = AgeUtils.getAge(from, to);
    Assert.assertEquals(0, age.getYears());
    Assert.assertEquals(7, age.getMonths());
    Assert.assertEquals(30, age.getDays());
  }
}