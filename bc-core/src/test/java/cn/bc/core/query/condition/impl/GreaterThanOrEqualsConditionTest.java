/**
 * 
 */
package cn.bc.core.query.condition.impl;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.core.query.condition.impl.GreaterThanOrEqualsCondition;


/**
 * 
 * @author dragon
 * 
 */
public class GreaterThanOrEqualsConditionTest {
	@Test
	public void test() {
		GreaterThanOrEqualsCondition c = new GreaterThanOrEqualsCondition("key",new Long(1));
		Assert.assertEquals("key >= ?", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 1);
		Assert.assertEquals(new Long(1),c.getValues().get(0));
	}
}
