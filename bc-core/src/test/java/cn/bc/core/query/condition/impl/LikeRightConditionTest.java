/**
 * 
 */
package cn.bc.core.query.condition.impl;

import junit.framework.Assert;

import org.junit.Test;


/**
 * 
 * @author dragon
 * 
 */
public class LikeRightConditionTest {
	@Test
	public void test() {
		LikeRightCondition c = new LikeRightCondition("key","value");
		Assert.assertEquals("key like ?", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 1);
		Assert.assertEquals("%value",c.getValues().get(0));
	}
}
