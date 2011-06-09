/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.core.query.condition.impl.QlCondition;

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
