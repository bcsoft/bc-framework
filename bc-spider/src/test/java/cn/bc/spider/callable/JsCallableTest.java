package cn.bc.spider.callable;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import cn.bc.spider.Result;
import cn.bc.spider.http.TaskExecutor;

public class JsCallableTest {
	private static Log logger = LogFactory.getLog(JsCallableTest.class);

	@Test
	public void test() throws Exception {
		TextCallable c = new TextCallable();
		c.setMethod("get");
		c.setGroup("jquery");
		c.setUrl("http://code.jquery.com/jquery-1.9.1.min.js");

		Result<String> result = TaskExecutor.get(c);
		Assert.assertTrue(result.isSuccess());
		if (logger.isDebugEnabled())
			logger.debug(result.getData());

		String file = "d:\\t\\jquery-1.9.1.min.js";
		c.export(new FileOutputStream(file));
		Assert.assertTrue(new File(file).exists());
	}
}
