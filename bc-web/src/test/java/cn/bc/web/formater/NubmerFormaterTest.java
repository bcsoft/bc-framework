package cn.bc.web.formater;

import org.junit.Assert;
import org.junit.Test;

public class NubmerFormaterTest {

  @Test
  public void testFloat() {
    NumberFormater f = new NumberFormater("0.0");
    Assert.assertEquals("1.0", f.format(1));
    Assert.assertEquals("1.0", f.format(1.0));
    Assert.assertEquals("1.0", f.format(1.01));
    Assert.assertEquals("1.2", f.format(1.234));
  }

  @Test
  public void testInt() {
    NumberFormater f = new NumberFormater("#.#");
    Assert.assertEquals("1", f.format(1));
    Assert.assertEquals("1", f.format(1.0));
    Assert.assertEquals("1", f.format(1.01));
    Assert.assertEquals("1.2", f.format(1.234));
  }

  @Test
  public void test21() {
    NumberFormater f = new NumberFormater("#.##");
    Assert.assertEquals("1", f.format(1));
    Assert.assertEquals("1", f.format(1.0));
    Assert.assertEquals("1.01", f.format(1.01));
    Assert.assertEquals("1.23", f.format(1.234));
    Assert.assertEquals("1.24", f.format(1.235));
    Assert.assertEquals("321.23", f.format(321.234));
  }

  @Test
  public void test22() {
    NumberFormater f = new NumberFormater("￥#0.00");
    Assert.assertEquals("￥0", f.format(12225.00));
    Assert.assertEquals("￥1", f.format(1));
    Assert.assertEquals("￥1", f.format(1.0));
    Assert.assertEquals("￥1.01", f.format(1.01));
    Assert.assertEquals("￥1.23", f.format(1.234));
    Assert.assertEquals("￥1.24", f.format(1.235));
    Assert.assertEquals("￥987654321.23", f.format(987654321.234));
  }

  @Test
  public void testMY() {
    NumberFormater f = new NumberFormater("$0.0");
    Assert.assertEquals("$1.0", f.format(1));
    Assert.assertEquals("$1.0", f.format(1.0));
    Assert.assertEquals("$1.0", f.format(1.01));
    Assert.assertEquals("$1.2", f.format(1.234));
  }
}
