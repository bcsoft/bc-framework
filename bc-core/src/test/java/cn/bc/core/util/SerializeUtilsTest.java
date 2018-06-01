package cn.bc.core.util;

import org.junit.Assert;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dragon on 2014/12/25.
 */
public class SerializeUtilsTest {
  @Test
  public void serializeString() {
    Object src = "test";
    byte[] bytes = SerializeUtils.serialize(src);
    Object dest = SerializeUtils.deserialize(bytes);
    Assert.assertEquals(src, dest);
  }

  @Test
  public void serializeMap() {
    Map<String, Object> src = new HashMap<String, Object>();
    src.put("k1", 1);
    src.put("k2", "v2");
    byte[] bytes = SerializeUtils.serialize(src);
    Object dest = SerializeUtils.deserialize(bytes);
    Assert.assertEquals(src, dest);
  }

  @Test(expected = RuntimeException.class)
  public void serializeJson() {
    JsonObject src = Json.createObjectBuilder()
      .add("k1", "a")
      .add("k2", 1)
      .add("k3", false)
      .addNull("k")
      .build();
    // throw java.io.NotSerializableException
    // because org.glassfish.json.JsonObjectBuilderImpl$JsonObjectImpl is not serializable
    SerializeUtils.serialize(src);
  }
}