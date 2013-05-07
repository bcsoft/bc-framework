package cn.bc.spider.callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import cn.bc.spider.Result;
import cn.bc.spider.service.TaskExecutor;

public class TextCallableTest {
	private static Log logger = LogFactory.getLog(TextCallableTest.class);

	@Test
	public void test() throws Exception {
		TextCallable c = new TextCallable();
		c.setMethod("get");
		c.setGroup("baidu");
		c.setUrl("http://www.baidu.com");
		c.setSuccessExpression("length() > 0");

		Result<String> result = TaskExecutor.get(c);
		Assert.assertTrue(result.isSuccess());
		if (logger.isDebugEnabled())
			logger.debug(result.getData());
	}
}
