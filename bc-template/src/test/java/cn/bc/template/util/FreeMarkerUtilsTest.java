/**
 *
 */
package cn.bc.template.util;

import cn.bc.core.util.DateUtils;
import cn.bc.core.util.FreeMarkerUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dragon
 */
public class FreeMarkerUtilsTest {
  @Test
  public void testFormatNumber() {
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("n1", 1234567890);
    args.put("n", null);
    Assert.assertEquals("1,234,567,890", FreeMarkerUtils.format("${n1}", args));
    Assert.assertEquals("1234567890", FreeMarkerUtils.format("${n1?c}", args));
    Assert.assertEquals("null", FreeMarkerUtils.format("${n?cn}", args));
    Assert.assertEquals("null", FreeMarkerUtils.format("${notExists?cn}", args));
    Assert.assertEquals("1,234,567,890.00", FreeMarkerUtils.format("${n1?string(',##0.00')}", args));
    Assert.assertEquals("￥1,234,567,890.00", FreeMarkerUtils.format("${n1?string.currency}", args));
  }

  @Test
  public void testFormatByMapParams() {
    String tpl = "${k1}-${k2}";
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("k1", "v1");
    args.put("k2", 2);
    Assert.assertEquals("v1-2", FreeMarkerUtils.format(tpl, args));
  }

  // 日期格式化
  @Test
  public void testFormatDate() {
    String tpl = "${d?string('yyyy-MM-dd HH:mm:ss')}";

    // Date类型,不支持Calendar类型
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("d", DateUtils.getDate("2012-01-01 12:10:05"));
    Assert.assertEquals("2012-01-01 12:10:05",
      FreeMarkerUtils.format(tpl, args));
  }

  // 日期格式化
  @Test
  public void testFormatDateUnExists() {
    String tpl = "a${(d?string('yyyy-MM-dd HH:mm:ss'))!}";

    // Date类型,不支持Calendar类型
    Map<String, Object> args = new HashMap<String, Object>();
    //args.put("t", "");
    //args.put("d", DateUtils.getDate("2012-01-01 12:10:05"));
    Assert.assertEquals("a", FreeMarkerUtils.format(tpl, null));
  }

  // 不存在的值
  @Test
  public void testUnExists() {
    String tpl = "${a!}-${a!'1'}-${a!\"2\"}";

    // Date类型,不支持Calendar类型
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("t", "");
    Assert.assertEquals("-1-2", FreeMarkerUtils.format(tpl, args));
  }

  //
  @Test
  public void testFormat() {
    String tpl = "${bsType?string('承包□合作□挂靠√','123')}";
    // String tpl = "${bsType == 3 ? \"承包□合作□挂靠√\":\"123\"}";

    // Date类型,不支持Calendar类型
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("bsType", true);
    Assert.assertEquals("承包□合作□挂靠√", FreeMarkerUtils.format(tpl, args));
  }

  //
  @Test
  public void testFormatByIfElse() {
    String tpl = "<#if logoutReason == \"转蓝\">√<#else>□</#if>";
    // String tpl =
    // "&lt;#if vs.logoutReason == \"转蓝\"&gt;√&lt;#else&gt;□&lt;/#if&gt;";
    // String tpl =
    // "&lt;#if logoutOwner??&gt;${logoutOwner}&lt;#else&gt;123&lt;/#if&gt;";
    // String tpl = "${bsType == 3 ? \"承包□合作□挂靠√\":\"123\"}";

    // Date类型,不支持Calendar类型
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("logoutReason", "转蓝");
    args.put("logoutOwner", "公司");
    Assert.assertEquals("√", FreeMarkerUtils.format(tpl, args));
  }

  @Test
  public void testFormatByMethod() {
    String tpl = "${a2}-${a1}";
    Map<String, Object> args = new HashMap<String, Object>();
    T a1 = new T();
    a1.setA1("name");
    args.put("a1", a1);
    args.put("a2", "a2");
    Assert.assertEquals("name", FreeMarkerUtils.format(tpl, args));
  }

  class T {
    private String a1;

    public String doIt() {
      return "ok";
    }

    public String getA1() {
      return a1;
    }

    public void setA1(String name) {
      this.a1 = name;
    }
  }

  @Test
  public void teTst() {
    String tpl = "<#assign key='f2'><#assign i=1>1:${f1}-2:${map['f2']}-3:${map[key]}-4:${map['f' + '2']}-5:${.vars['f' + i]}";
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("f1", "f1v");

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("f2", "f2v");
    args.put("map", map);
    System.out.println(FreeMarkerUtils.format(tpl, args));
    ;
  }

  @Test
  public void testEvalJson() {
    String tpl = "<#assign jsonObjectStr='{\"a\":\"a\", \"b\":\"2\"}'>\n" + 
        "<#assign m=jsonObjectStr?eval_json>\n" + 
        "<#list m as k, v>  ${k}=${v}<#sep>\n</#list>";
    System.out.println("tpl:\n" + tpl);
    Map<String, Object> args = new HashMap<String, Object>();
    // args.put("f1", "f1v");
    System.out.println("result:\n" + FreeMarkerUtils.format(tpl, args));
    
    tpl = "<#assign jsonObjectStr='[{\"a\":\"a\", \"b\":\"2\"}]'>\n" + 
        "<#assign array=jsonObjectStr?eval_json>\n" + 
        "<#list array as m>\n" + 
        "  ${m?index}:<#list m as k, v>${k}=${v}<#sep>, </#list>\n" + 
        "</#list>中文";
    System.out.println("tpl:\n" + tpl);
    System.out.println("result:\n" + FreeMarkerUtils.format(tpl, args));
  }

  @Test
  public void testDynamicVar() {
    String tpl = "<#assign my_variable = \"my_value\">${\"my_variable\"?eval}";
    System.out.println("tpl:\n" + tpl);
    Map<String, Object> args = new HashMap<String, Object>();
    // args.put("f1", "f1v");
    System.out.println("result:\n" + FreeMarkerUtils.format(tpl, args));
  }
}
