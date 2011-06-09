/**
 * 
 */
package cn.bc.core.query.condition.impl;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.core.query.condition.impl.RangeCondition;
import cn.bc.core.query.condition.impl.RangeCondition.RangeType;


/**
 * 
 * @author dragon
 * 
 */
public class RangeConditionTest {
	@Test
	public void GreaterThan_LessThanOrEqual() {
		RangeCondition c = new RangeCondition("key", new Long(1), new Long(5),
				RangeType.GreaterThan_LessThanOrEqual);
		Assert.assertEquals("(key > ? and key <= ?)", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 2);
		Assert.assertEquals(new Long(1), c.getValues().get(0));
		Assert.assertEquals(new Long(5), c.getValues().get(1));
	}
	
	@Test
	public void GreaterThan_LessThan() {
		RangeCondition c = new RangeCondition("key", new Long(1), new Long(5),
				RangeType.GreaterThan_LessThan);
		Assert.assertEquals("(key > ? and key < ?)", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 2);
		Assert.assertEquals(new Long(1), c.getValues().get(0));
		Assert.assertEquals(new Long(5), c.getValues().get(1));
	}
	
	@Test
	public void GreaterThanOrEqual_LessThanOrEqual() {
		RangeCondition c = new RangeCondition("key", new Long(1), new Long(5),
				RangeType.GreaterThanOrEqual_LessThanOrEqual);
		Assert.assertEquals("(key >= ? and key <= ?)", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 2);
		Assert.assertEquals(new Long(1), c.getValues().get(0));
		Assert.assertEquals(new Long(5), c.getValues().get(1));
	}
	
	@Test
	public void GreaterThanOrEqual_LessThan() {
		RangeCondition c = new RangeCondition("key", new Long(1), new Long(5),
				RangeType.GreaterThanOrEqual_LessThan);
		Assert.assertEquals("(key >= ? and key < ?)", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 2);
		Assert.assertEquals(new Long(1), c.getValues().get(0));
		Assert.assertEquals(new Long(5), c.getValues().get(1));
	}
}
