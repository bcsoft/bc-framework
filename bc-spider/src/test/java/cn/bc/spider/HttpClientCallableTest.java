package cn.bc.spider;

import org.junit.Assert;
import org.junit.Test;

import cn.bc.spider.http.TaskExecutor;

public class HttpClientCallableTest {
	@Test
	public void testGet() throws Exception {
		// 模拟获取百度主页
		HttpClientCallable<Boolean> callable = new HttpClientCallable<Boolean>();
		callable.setMethod("get");
		callable.setUrl("http://www.baidu.com");
		callable.setSuccessExpression("!document.select(\"input[type=text]\").isEmpty()");
		Result<Boolean> result1 = TaskExecutor.get(callable);

		Assert.assertTrue(result1.isSuccess());
	}
}
