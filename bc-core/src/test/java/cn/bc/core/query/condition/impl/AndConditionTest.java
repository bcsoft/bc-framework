/**
 * 
 */
package cn.bc.core.query.condition.impl;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;

/**
 * 
 * @author dragon
 * 
 */
public class AndConditionTest {
	@Test
	public void construct1() {
		AndCondition and = new AndCondition(new EqualsCondition("key1",
				"value1"));
		Assert.assertEquals("key1 = ?", and.getExpression());
		Assert.assertNotNull(and.getValues());
		Assert.assertTrue(and.getValues().size() == 1);
		Assert.assertEquals("value1", and.getValues().get(0));
	}

	@Test
	public void construct2() {
		AndCondition and = new AndCondition(new EqualsCondition("key1",
				"value1"), new EqualsCondition("key2", "value2"));
		Assert.assertEquals("key1 = ? and key2 = ?", and.getExpression());
		Assert.assertNotNull(and.getValues());
		Assert.assertTrue(and.getValues().size() == 2);
		Assert.assertEquals("value1", and.getValues().get(0));
		Assert.assertEquals("value2", and.getValues().get(1));
	}

	@Test
	public void add() {
		AndCondition and = new AndCondition();
		and.add(new EqualsCondition("key1", "value1")).add(
				new EqualsCondition("key2", "value2"));
		Assert.assertEquals("key1 = ? and key2 = ?", and.getExpression());
		Assert.assertNotNull(and.getValues());
		Assert.assertTrue(and.getValues().size() == 2);
		Assert.assertEquals("value1", and.getValues().get(0));
		Assert.assertEquals("value2", and.getValues().get(1));
	}
}
