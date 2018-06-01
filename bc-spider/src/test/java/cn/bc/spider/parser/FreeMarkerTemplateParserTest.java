package cn.bc.spider.parser;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class FreeMarkerTemplateParserTest {
  @Test
  public void test() throws Exception {
    FreeMarkerTemplateParser c = new FreeMarkerTemplateParser("${name}");
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("name", "dragon");
    Assert.assertEquals("dragon", c.parse(params));
  }
}
