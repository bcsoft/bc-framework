package cn.bc.spider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

import cn.bc.spider.http.TaskExecutor;

public class SimpleLoginCallableTest {
	ExecutorService executorService = Executors.newCachedThreadPool();

	@Test
	public void test() throws Exception {
		// 模拟登录出租车应用管理系统
		SimpleLoginCallable callable = new SimpleLoginCallable();
		callable.setGroup("gis");
		callable.setUrl("http://gis.gci-china.com:8083/exec.aspx");
		callable.addFormData("Sys_FuncID", "110");
		callable.addFormData("pUserID", "baochenglh_admin");
		callable.addFormData("pPWD", "baocheng-gs");
		callable.setSuccessExpression("new Integer(document.select(\"#__ROWCOUNT\").val()) > 0");

		Result<Boolean> result = TaskExecutor.get(callable);
		Assert.assertTrue(result.isSuccess());
	}
}
