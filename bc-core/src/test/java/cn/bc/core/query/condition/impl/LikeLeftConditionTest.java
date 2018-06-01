/**
 *
 */
package cn.bc.core.query.condition.impl;

import org.junit.Assert;
import org.junit.Test;

;


/**
 * @author dragon
 */
public class LikeLeftConditionTest {
  @Test
  public void test() {
    LikeLeftCondition c = new LikeLeftCondition("key", "value");
    Assert.assertEquals("key like ?", c.getExpression());
    Assert.assertNotNull(c.getValues());
    Assert.assertTrue(c.getValues().size() == 1);
    Assert.assertEquals("value%", c.getValues().get(0));
  }
}
