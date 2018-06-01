package cn.bc.web.formater;

import org.junit.Assert;
import org.junit.Test;

public class IconTextFormaterTest {
  @Test
  public void testOnlyText() {
    IconTextFormater f = new IconTextFormater();
    Assert.assertEquals("TEST", f.format("TEST"));
  }

  @Test
  public void testAddIconBeforeText() {
    IconTextFormater f = new IconTextFormater();
    f.addIconBeforeText(new IconTextFormater.Icon("ui-icon-me", null, null));
    Assert.assertEquals("<ul class=\"inputIcons incell\"><li class=\"inputIcon ui-icon-me\"></li></ul>TEST", f.format("TEST"));
  }

  @Test
  public void testAddIconAfterText() {
    IconTextFormater f = new IconTextFormater();
    f.addIconAfterText(new IconTextFormater.Icon("ui-icon-me", null, null));
    Assert.assertEquals("TEST<ul class=\"inputIcons incell\"><li class=\"inputIcon ui-icon-me\"></li></ul>", f.format("TEST"));
  }

  @Test
  public void testAddIcons() {
    IconTextFormater f = new IconTextFormater();
    f.addIconAfterText(new IconTextFormater.Icon("ui-icon-me", null, null));
    f.addIconBeforeText(new IconTextFormater.Icon("ui-icon-me", null, null));
    Assert.assertEquals("<ul class=\"inputIcons incell\"><li class=\"inputIcon ui-icon-me\"></li></ul>" +
      "TEST<ul class=\"inputIcons incell\"><li class=\"inputIcon ui-icon-me\"></li></ul>", f.format("TEST"));
  }


  @Test
  public void testAddIconBeforeTextWithClick() {
    IconTextFormater f = new IconTextFormater();
    f.addIconBeforeText(new IconTextFormater.Icon("ui-icon-me", "bc.a", null));
    Assert.assertEquals("<ul class=\"inputIcons incell\"><li class=\"inputIcon ui-icon-me\" data-click=\"bc.a\"></li></ul>TEST"
      , f.format("TEST"));
  }

  @Test
  public void testAddIconBeforeTextWithClickAndTitle() {
    IconTextFormater f = new IconTextFormater();
    f.addIconBeforeText(new IconTextFormater.Icon("ui-icon-me", "bc.a", "t"));
    Assert.assertEquals("<ul class=\"inputIcons incell\"><li class=\"inputIcon ui-icon-me\" data-click=\"bc.a\" title=\"t\"></li></ul>TEST"
      , f.format("TEST"));
  }

  @Test
  public void testAddIconBeforeTextWithClickAndTitleAndId() {
    IconTextFormater f = new IconTextFormater();
    f.addIconBeforeText(new IconTextFormater.Icon("ui-icon-me", "bc.a", "t", "me"));
    Assert.assertEquals("<ul class=\"inputIcons incell\"><li class=\"inputIcon ui-icon-me\"" +
        " id=\"me\" data-click=\"bc.a\" title=\"t\"></li></ul>TEST"
      , f.format("TEST"));
  }

  @Test
  public void testExportText() {
    IconTextFormater f = new IconTextFormater() {
      @Override
      public String getExportText(Object context, Object value) {
        return "TT";
      }
    };
    System.out.println(f.getClass());
    Assert.assertTrue(f instanceof ExportText);
  }
}
