/**
 * 
 */
package cn.bc.core.query.condition.impl;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.core.query.condition.impl.IsNullCondition;


/**
 * 
 * @author dragon
 * 
 */
public class IsNullConditionTest {
	@Test
	public void test() {
		IsNullCondition c = new IsNullCondition("key");
		Assert.assertEquals("key is null", c.getExpression());
		Assert.assertNull(c.getValues());
	}
}
