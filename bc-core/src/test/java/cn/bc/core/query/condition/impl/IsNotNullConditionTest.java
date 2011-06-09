/**
 * 
 */
package cn.bc.core.query.condition.impl;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.core.query.condition.impl.IsNotNullCondition;


/**
 * 
 * @author dragon
 * 
 */
public class IsNotNullConditionTest {
	@Test
	public void test() {
		IsNotNullCondition c = new IsNotNullCondition("key");
		Assert.assertEquals("key is not null", c.getExpression());
		Assert.assertNull(c.getValues());
	}
}
