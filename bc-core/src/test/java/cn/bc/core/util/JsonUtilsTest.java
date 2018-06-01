/**
 *
 */
package cn.bc.core.util;

import cn.bc.core.gson.GsonPerson;
import com.google.gson.JsonParseException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author dragon
 */
public class JsonUtilsTest {
  @Test
  public void toJson_Object() {
    GsonPerson obj = new GsonPerson("name1", 1);
    Assert.assertEquals("{\"name\":\"name1\",\"age\":1}",
      JsonUtils.toJson(obj));
  }

  @Test
  public void toJson_String() {
    Assert.assertEquals("\"name\"", JsonUtils.toJson("name"));
  }

  @Test(expected = JsonParseException.class)
  public void toMap_Exception_String() {
    JsonUtils.toMap("name");
  }

  @Test(expected = JsonParseException.class)
  public void toMap_Exception_Array() {
    JsonUtils.toMap("[\"name\"]");
  }

  @Test
  public void toMap() {
    String str = "{\"str\":\"str\",\"int\":1,\"bool\":true}";
    Map<String, Object> map = JsonUtils.toMap(str);
    Assert.assertEquals("str", map.get("str"));
    Assert.assertEquals(String.class, map.get("str").getClass());
    Assert.assertEquals(new Integer(1), map.get("int"));
    Assert.assertEquals(Integer.class, map.get("int").getClass());
    Assert.assertEquals(new Boolean(true), map.get("bool"));
    Assert.assertEquals(Boolean.class, map.get("bool").getClass());
  }

  @Test
  public void toMap_NestedMap() {
    String str = "{\"str\":\"str\",\"int\":1,\"bool\":true,\"sub\":{\"str\":\"str\",\"int\":1,\"bool\":true}}";
    Map<String, Object> map = JsonUtils.toMap(str);

    Assert.assertEquals("str", map.get("str"));
    Assert.assertEquals(String.class, map.get("str").getClass());
    Assert.assertEquals(new Integer(1), map.get("int"));
    Assert.assertEquals(Integer.class, map.get("int").getClass());
    Assert.assertEquals(new Boolean(true), map.get("bool"));
    Assert.assertEquals(Boolean.class, map.get("bool").getClass());

    Assert.assertTrue(map.get("sub") instanceof Map);
    @SuppressWarnings({"unchecked"})
    Map<String, Object> sub = (Map<String, Object>) map.get("sub");
    Assert.assertEquals("str", sub.get("str"));
    Assert.assertEquals(String.class, sub.get("str").getClass());
    Assert.assertEquals(new Integer(1), sub.get("int"));
    Assert.assertEquals(Integer.class, sub.get("int").getClass());
    Assert.assertEquals(new Boolean(true), sub.get("bool"));
    Assert.assertEquals(Boolean.class, sub.get("bool").getClass());
  }

  @Test
  public void toMap_Nested_Array_Simple() {
    String str = "{\"str\":\"str\",\"int\":1,\"bool\":true,\"array\":[\"a1\",1,true]}";
    Map<String, Object> map = JsonUtils.toMap(str);

    Assert.assertEquals("str", map.get("str"));
    Assert.assertEquals(String.class, map.get("str").getClass());
    Assert.assertEquals(new Integer(1), map.get("int"));
    Assert.assertEquals(Integer.class, map.get("int").getClass());
    Assert.assertEquals(new Boolean(true), map.get("bool"));
    Assert.assertEquals(Boolean.class, map.get("bool").getClass());

    Assert.assertEquals(Object[].class, map.get("array").getClass());
    Object[] array = (Object[]) map.get("array");
    Assert.assertEquals(String.class, array[0].getClass());
    Assert.assertEquals("a1", array[0]);
    Assert.assertEquals(Integer.class, array[1].getClass());
    Assert.assertEquals(1, array[1]);
    Assert.assertEquals(Boolean.class, array[2].getClass());
    Assert.assertEquals(new Boolean(true), array[2]);
  }

  @Test
  public void toArray() {
    String str = "[\"a1\",1,true]";
    Object[] array = JsonUtils.toArray(str);
    Assert.assertEquals(String.class, array[0].getClass());
    Assert.assertEquals("a1", array[0]);
    Assert.assertEquals(Integer.class, array[1].getClass());
    Assert.assertEquals(1, array[1]);
    Assert.assertEquals(Boolean.class, array[2].getClass());
    Assert.assertEquals(new Boolean(true), array[2]);
  }

  @Test
  public void toArray_NestedMap() {
    String str = "[{\"str\":\"str\",\"int\":1,\"bool\":true}]";
    Object[] array = JsonUtils.toArray(str);
    Assert.assertEquals(LinkedHashMap.class, array[0].getClass());
    @SuppressWarnings("unchecked")
    Map<String, Object> sub = (Map<String, Object>) array[0];
    Assert.assertEquals("str", sub.get("str"));
    Assert.assertEquals(String.class, sub.get("str").getClass());
    Assert.assertEquals(new Integer(1), sub.get("int"));
    Assert.assertEquals(Integer.class, sub.get("int").getClass());
    Assert.assertEquals(new Boolean(true), sub.get("bool"));
    Assert.assertEquals(Boolean.class, sub.get("bool").getClass());
  }

  @Test
  public void toCollection() {
    String str = "[\"a1\",1,true]";
    Collection<Object> col = JsonUtils.toCollection(str);
    // System.out.println(col.getClass());//java.util.LinkedList
    Object[] array = col.toArray();
    Assert.assertEquals(String.class, array[0].getClass());
    Assert.assertEquals("a1", array[0]);
    Assert.assertEquals(Integer.class, array[1].getClass());
    Assert.assertEquals(1, array[1]);
    Assert.assertEquals(Boolean.class, array[2].getClass());
    Assert.assertEquals(new Boolean(true), array[2]);
  }

  @Test
  public void stripComment() throws Exception {
    //String source = FileCopyUtils.copyToString(new FileReader("d:\\test.json"));
    String source = "// 中文\n  \t// \ttest\n" +
      "{\n" +
      "  // comment11\n" +
      "  k11: \"v11\",\n" +
      "  k12: \"v12\",  \t// comment12\n" +
      "  //comment13  \t\n" +
      "  k13: \"v13\",  \t// \tcomment13  \t\n" +
      "\n" +
      "  /* comment21 */\n" +
      "  k21: \"v21\",\n" +
      "  k22: \"v22\",  \t/* comment22 */\n" +
      "  /*comment23*/  \t\n" +
      "  k23: \"v23\",  \t/* \tcomment23*/  \t\n" +
      "\n" +
      "  /* line311\n" +
      "   * line312\n" +
      "   */\n" +
      "  k31: \"v31\",\n" +
      "  /** line321\n" +
      "   * line322\n" +
      "   */\n" +
      "  k32: \"v32\",\n" +
      "  /**\n" +
      "   * line331\n" +
      "   * line332\n" +
      "   */\n" +
      "  k33: \"v33\",\n" +
      "  ke:0\n" +
      "}";
    System.out.println("source=" + source);
    String result = JsonUtils.stripComment(source);
    System.out.println("result=" + result);
    JSONObject json = new JSONObject(result);
    //JSONArray json = new JSONArray(result);
    System.out.println("json=" + json);
  }
}