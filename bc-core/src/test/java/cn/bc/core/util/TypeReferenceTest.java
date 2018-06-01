package cn.bc.core.util;

import cn.bc.core.RichEntity;
import cn.bc.core.RichEntityImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TypeReferenceTest {
  @Test
  public void testAbstractClass() {
    TypeReference<RichEntityImpl> tr = new TypeReference<RichEntityImpl>() {
    };
    Assert.assertSame(RichEntityImpl.class, tr.getRawType());
    Assert.assertSame(RichEntityImpl.class, tr.getType());
  }

  @Test
  public void testInterface() {
    TypeReference<RichEntity<Long>> tr = new TypeReference<RichEntity<Long>>() {
    };
    Assert.assertSame(RichEntity.class, tr.getRawType());
    Assert.assertNotSame(RichEntity.class, tr.getType());
  }

  @Test
  public void testMap() {
    TypeReference<Map<String, RichEntityImpl>> tr = new TypeReference<Map<String, RichEntityImpl>>() {
    };
    Assert.assertSame(Map.class, tr.getRawType());
    Assert.assertNotSame(Map.class, tr.getType());

//		//或者
//		TypeToken<Map<String, Example>> tt = new TypeToken<Map<String, Example>>() {};
//		Assert.assertEquals("java.util.Map<java.lang.String, cn.bc.test.Example>", tt.getType().toString());
//		Assert.assertEquals("java.util.Map", tt.getRawType().getName());
  }
}
