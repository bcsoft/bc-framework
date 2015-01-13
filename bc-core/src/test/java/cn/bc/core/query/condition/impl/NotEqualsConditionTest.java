/**
 * 
 */
package cn.bc.core.query.condition.impl;

import org.junit.Assert;
import org.junit.Test;


/**
 * 
 * @author dragon
 * 
 */
public class NotEqualsConditionTest {
	@Test
	public void test() {
		NotEqualsCondition c = new NotEqualsCondition("key","value");
		Assert.assertEquals("key != ?", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 1);
		Assert.assertEquals("value",c.getValues().get(0));
	}
}
