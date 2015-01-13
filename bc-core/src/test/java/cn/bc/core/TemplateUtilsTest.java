package cn.bc.core;

import cn.bc.core.util.TemplateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test.xml")
public class TemplateUtilsTest {
	@Test
	public void getContent() throws Exception{
		//String code = "BC-EMAIL-SYSTEMAUTOFORWARD:1";// 指定编码和版本号
		String code = "BC-EMAIL-SYSTEMAUTOFORWARD";// 最新版本
		String s = TemplateUtils.getContent(code);
		//System.out.println(s);
		Assert.assertNotNull(s);
	}
}