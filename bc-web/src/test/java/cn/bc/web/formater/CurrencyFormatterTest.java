package cn.bc.web.formater;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class CurrencyFormatterTest {
  @Test
  public void defaultSymbol() {
    Assert.assertNull(new CurrencyFormatter().format(null));
    Assert.assertEquals("", new CurrencyFormatter().format(""));
    Assert.assertEquals(" ", new CurrencyFormatter().format(" "));
    Assert.assertEquals("a", new CurrencyFormatter().format("a"));

    Assert.assertEquals("¥ 0.00", new CurrencyFormatter().format(0));
    Assert.assertEquals("¥ 0.01", new CurrencyFormatter().format(0.01));

    Assert.assertEquals("¥ 100.00", new CurrencyFormatter().format(100));
    Assert.assertEquals("¥ 100.00", new CurrencyFormatter().format(new BigDecimal(100)));
    Assert.assertEquals("¥ 100.00", new CurrencyFormatter().format(100.005));
    Assert.assertEquals("¥ -100.00", new CurrencyFormatter().format(-100));
    Assert.assertEquals("¥ -100.00", new CurrencyFormatter().format(-100.005));

    Assert.assertEquals("¥ 0.00", new CurrencyFormatter().format("0"));
    Assert.assertEquals("¥ 100.00", new CurrencyFormatter().format("100"));
    Assert.assertEquals("¥ 100.00", new CurrencyFormatter().format("100.005"));
    Assert.assertEquals("¥ -100.00", new CurrencyFormatter().format("-100"));
    Assert.assertEquals("¥ -100.00", new CurrencyFormatter().format("-100.005"));

    // export
    Assert.assertNull(new CurrencyFormatter().getExportText(null, null));
    Assert.assertEquals("", new CurrencyFormatter().getExportText(null, ""));
    Assert.assertEquals(" ", new CurrencyFormatter().getExportText(null, " "));
    Assert.assertEquals("a", new CurrencyFormatter().getExportText(null, "a"));
    Assert.assertEquals("100.00", new CurrencyFormatter().getExportText(null, 100));
    Assert.assertEquals("-100.00", new CurrencyFormatter().getExportText(null, -100));
  }

  @Test
  public void customSymbol() {
    Assert.assertNull(new CurrencyFormatter("$").format(null));
    Assert.assertEquals("", new CurrencyFormatter("$").format(""));
    Assert.assertEquals(" ", new CurrencyFormatter("$").format(" "));
    Assert.assertEquals("a", new CurrencyFormatter("$").format("a"));

    Assert.assertEquals("$ 0.00", new CurrencyFormatter("$").format(0));
    Assert.assertEquals("$ 0.01", new CurrencyFormatter("$").format(0.01));

    Assert.assertEquals("$ 100.00", new CurrencyFormatter("$").format(100));
    Assert.assertEquals("$ 100.00", new CurrencyFormatter("$").format(new BigDecimal(100)));
    Assert.assertEquals("$ 100.00", new CurrencyFormatter("$").format(100.005));
    Assert.assertEquals("$ -100.00", new CurrencyFormatter("$").format(-100));
    Assert.assertEquals("$ -100.00", new CurrencyFormatter("$").format(-100.005));

    Assert.assertEquals("$ 0.00", new CurrencyFormatter("$").format("0"));
    Assert.assertEquals("$ 100.00", new CurrencyFormatter("$").format("100"));
    Assert.assertEquals("$ 100.00", new CurrencyFormatter("$").format("100.005"));
    Assert.assertEquals("$ -100.00", new CurrencyFormatter("$").format("-100"));
    Assert.assertEquals("$ -100.00", new CurrencyFormatter("$").format("-100.005"));

    // export
    Assert.assertNull(new CurrencyFormatter("$").getExportText(null, null));
    Assert.assertEquals("", new CurrencyFormatter("$").getExportText(null, ""));
    Assert.assertEquals(" ", new CurrencyFormatter("$").getExportText(null, " "));
    Assert.assertEquals("a", new CurrencyFormatter("$").getExportText(null, "a"));
    Assert.assertEquals("100.00", new CurrencyFormatter("$").getExportText(null, 100));
    Assert.assertEquals("-100.00", new CurrencyFormatter("$").getExportText(null, -100));
  }

  @Test
  public void customPrecision() {
    Assert.assertNull(new CurrencyFormatter(1).format(null));
    Assert.assertEquals("", new CurrencyFormatter(3).format(""));
    Assert.assertEquals(" ", new CurrencyFormatter(3).format(" "));
    Assert.assertEquals("a", new CurrencyFormatter(3).format("a"));

    Assert.assertEquals("¥ 0.000", new CurrencyFormatter(3).format(0));
    Assert.assertEquals("¥ 0.010", new CurrencyFormatter(3).format(0.01));

    Assert.assertEquals("¥ 100.000", new CurrencyFormatter(3).format(100));
    Assert.assertEquals("¥ 100.000", new CurrencyFormatter(3).format(new BigDecimal(100)));
    Assert.assertEquals("¥ 100.005", new CurrencyFormatter(3).format(100.005));
    Assert.assertEquals("¥ -100.000", new CurrencyFormatter(3).format(-100));
    Assert.assertEquals("¥ -100.005", new CurrencyFormatter(3).format(-100.005));

    Assert.assertEquals("¥ 0.000", new CurrencyFormatter(3).format("0"));
    Assert.assertEquals("¥ 100.000", new CurrencyFormatter(3).format("100"));
    Assert.assertEquals("¥ 100.005", new CurrencyFormatter(3).format("100.005"));
    Assert.assertEquals("¥ -100.000", new CurrencyFormatter(3).format("-100"));
    Assert.assertEquals("¥ -100.005", new CurrencyFormatter(3).format("-100.005"));

    // export
    Assert.assertNull(new CurrencyFormatter(3).getExportText(null, null));
    Assert.assertEquals("", new CurrencyFormatter(3).getExportText(null, ""));
    Assert.assertEquals(" ", new CurrencyFormatter(3).getExportText(null, " "));
    Assert.assertEquals("a", new CurrencyFormatter(3).getExportText(null, "a"));
    Assert.assertEquals("100.000", new CurrencyFormatter(3).getExportText(null, 100));
    Assert.assertEquals("-100.000", new CurrencyFormatter(3).getExportText(null, -100));
  }

  @Test
  public void alignRight() {
    testAlign(CurrencyFormatter.Align.Right);
  }

  @Test
  public void alignLeft() {
    testAlign(CurrencyFormatter.Align.Left);
  }

  @Test
  public void alignCenter() {
    testAlign(CurrencyFormatter.Align.Center);
  }

  @Test
  public void alignNone() {
    testAlign(CurrencyFormatter.Align.None);
  }

  private void testAlign(CurrencyFormatter.Align align) {
    Assert.assertNull(new CurrencyFormatter().setAlign(align).format(null));
    Assert.assertEquals("", new CurrencyFormatter().setAlign(align).format(""));
    Assert.assertEquals(" ", new CurrencyFormatter().setAlign(align).format(" "));
    Assert.assertEquals("a", new CurrencyFormatter().setAlign(align).format("a"));

    Assert.assertEquals(createAlignText(align, "¥ 0.00"), new CurrencyFormatter().setAlign(align).format(0));
    Assert.assertEquals(createAlignText(align, "¥ 0.01"), new CurrencyFormatter().setAlign(align).format(0.01));

    Assert.assertEquals(createAlignText(align, "¥ 100.00"), new CurrencyFormatter().setAlign(align).format(100));
    Assert.assertEquals(createAlignText(align, "¥ 100.00"), new CurrencyFormatter().setAlign(align).format(new BigDecimal(100)));
    Assert.assertEquals(createAlignText(align, "¥ 100.00"), new CurrencyFormatter().setAlign(align).format(100.005));
    Assert.assertEquals(createAlignText(align, "¥ -100.00"), new CurrencyFormatter().setAlign(align).format(-100));
    Assert.assertEquals(createAlignText(align, "¥ -100.00"), new CurrencyFormatter().setAlign(align).format(-100.005));

    Assert.assertEquals(createAlignText(align, "¥ 0.00"), new CurrencyFormatter().setAlign(align).format("0"));
    Assert.assertEquals(createAlignText(align, "¥ 100.00"), new CurrencyFormatter().setAlign(align).format("100"));
    Assert.assertEquals(createAlignText(align, "¥ 100.00"), new CurrencyFormatter().setAlign(align).format("100.005"));
    Assert.assertEquals(createAlignText(align, "¥ -100.00"), new CurrencyFormatter().setAlign(align).format("-100"));
    Assert.assertEquals(createAlignText(align, "¥ -100.00"), new CurrencyFormatter().setAlign(align).format("-100.005"));

    // export
    Assert.assertNull(new CurrencyFormatter().getExportText(null, null));
    Assert.assertEquals("", new CurrencyFormatter().getExportText(null, ""));
    Assert.assertEquals(" ", new CurrencyFormatter().getExportText(null, " "));
    Assert.assertEquals("a", new CurrencyFormatter().getExportText(null, "a"));
    Assert.assertEquals("100.00", new CurrencyFormatter().getExportText(null, 100));
    Assert.assertEquals("-100.00", new CurrencyFormatter().getExportText(null, -100));
  }

  private String createAlignText(CurrencyFormatter.Align align, String value) {
    if (align != CurrencyFormatter.Align.None)
      return "<div style=\"text-align:" + align.value() + "\">" + value + "</div>";
    else
      return value;
  }
}