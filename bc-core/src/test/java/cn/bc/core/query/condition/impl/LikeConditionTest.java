/**
 * 
 */
package cn.bc.core.query.condition.impl;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.core.query.condition.impl.LikeCondition;


/**
 * 
 * @author dragon
 * 
 */
public class LikeConditionTest {
	@Test
	public void test() {
		LikeCondition c = new LikeCondition("key","value");
		Assert.assertEquals("key like ?", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 1);
		Assert.assertEquals("%value%",c.getValues().get(0));
	}
}
