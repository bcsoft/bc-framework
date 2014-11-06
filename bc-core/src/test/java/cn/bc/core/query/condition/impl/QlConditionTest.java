/**
 * 
 */
package cn.bc.core.query.condition.impl;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author dragon
 * 
 */
public class QlConditionTest {
	@Test
	public void test() {
		List<Object> values = new ArrayList<Object>();
		values.add("value");
		QlCondition c = new QlCondition("sql", values);
		Assert.assertEquals("sql", c.getExpression());
		Assert.assertNotNull(c.getValues());
		Assert.assertTrue(c.getValues().size() == 1);
		Assert.assertEquals("value", c.getValues().get(0));
	}
}
