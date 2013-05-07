package cn.bc.spider.callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import cn.bc.spider.Result;
import cn.bc.spider.service.TaskExecutor;

public class DomCallableTest {
	private static Log logger = LogFactory.getLog(DomCallableTest.class);

	@Test
	public void test() throws Exception {
		DomCallable c = new DomCallable();
		c.setMethod("get");
		c.setGroup("baidu");
		c.setUrl("http://www.baidu.com");
		c.setSuccessExpression("select(\"#kw\").size() > 0");
		//c.setResultExpression("#root");
		Result<Document> result = TaskExecutor.get(c);
		Assert.assertTrue(result.isSuccess());
		if (logger.isDebugEnabled())
			logger.debug(result.getData());
	}
}
