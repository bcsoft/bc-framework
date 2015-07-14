package cn.bc.core;

		import org.junit.Test;
		import org.slf4j.Logger;

public class Sl4jTest {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(Sl4jTest.class);

	@Test
	public void test01() throws Exception{
		// 输出格式化的日志 http://caichaowei.iteye.com/blog/1684520
		logger.debug("参数：arg0={}, arg1={}", 0, "中文");
	}

	@Test
	public void test02() throws Exception{
		System.out.println("rolesAction".replaceAll("Action|sAction|ViewAction|FormAction", ""));
	}
}