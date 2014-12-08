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
public class EqualsConditionTest {
	static final String ALIAS = "alias_";
	
	@Test
	public void test() {
		EqualsCondition c = new EqualsCondition("key", "value");
		Assert.assertEquals("key = ?", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 1);
		Assert.assertEquals("value", c.getValues().get(0));
	}
	
	@Test
	public void testWithAlias() {
		EqualsCondition c = new EqualsCondition("key", "value");
		Assert.assertEquals("alias_.key = ?", c.getExpression("alias_"));
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 1);
		Assert.assertEquals("value", c.getValues().get(0));
	}
}
