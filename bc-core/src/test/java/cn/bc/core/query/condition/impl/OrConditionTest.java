/**
 * 
 */
package cn.bc.core.query.condition.impl;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrCondition;


/**
 * 
 * @author dragon
 * 
 */
public class OrConditionTest {
	@Test
	public void construct() {
		OrCondition and = new OrCondition(new EqualsCondition("key1",
				"value1"), new EqualsCondition("key2", "value2"));
		Assert.assertEquals("key1 = ? or key2 = ?", and.getExpression());
		Assert.assertNotNull(and.getValues());
		Assert.assertTrue(and.getValues().size() == 2);
		Assert.assertEquals("value1", and.getValues().get(0));
		Assert.assertEquals("value2", and.getValues().get(1));
	}

	@Test
	public void add() {
		OrCondition or = new OrCondition();
		or.add(new EqualsCondition("key1", "value1"))
			.add(new EqualsCondition("key2", "value2"));
		Assert.assertEquals("key1 = ? or key2 = ?", or.getExpression());
		Assert.assertNotNull(or.getValues());
		Assert.assertTrue(or.getValues().size() == 2);
		Assert.assertEquals("value1", or.getValues().get(0));
		Assert.assertEquals("value2", or.getValues().get(1));
	}

	@Test
	public void addLike() {
		OrCondition or = new OrCondition();
		or.add(new LikeCondition("key1", "value1"));
		or.add(new LikeCondition("key2", "value2"));
		or.add(new LikeCondition("key3", "value3"));
		or.add(new LikeCondition("key4", "value4"));
		Assert.assertEquals("key1 like ? or key2 like ? or key3 like ? or key4 like ?", or.getExpression());
		Assert.assertNotNull(or.getValues());
		Assert.assertTrue(or.getValues().size() == 4);
		Assert.assertEquals("%value1%", or.getValues().get(0));
		Assert.assertEquals("%value2%", or.getValues().get(1));
		Assert.assertEquals("%value3%", or.getValues().get(2));
		Assert.assertEquals("%value4%", or.getValues().get(3));
	}
}
