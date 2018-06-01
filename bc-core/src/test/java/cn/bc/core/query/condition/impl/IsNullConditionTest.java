/**
 *
 */
package cn.bc.core.query.condition.impl;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author dragon
 */
public class IsNullConditionTest {
  @Test
  public void test() {
    IsNullCondition c = new IsNullCondition("key");
    Assert.assertEquals("key is null", c.getExpression());
    Assert.assertNull(c.getValues());
  }
}
