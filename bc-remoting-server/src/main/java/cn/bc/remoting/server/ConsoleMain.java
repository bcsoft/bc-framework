package cn.bc.remoting.server;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import cn.bc.remoting.service.WordService;

public class ConsoleMain {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("starting ...");

		// 初始化 Spring
		// ClassPathXmlApplicationContext
		Resource sresource = new ClassPathResource(
				"cn/bc/remoting/spring.xml");
		BeanFactory sfactory = new XmlBeanFactory(sresource);

		// 注册 RMI
		WordService convertService = sfactory.getBean(WordService.class);
		System.out.println("convertService=" + convertService);

		//Thread.sleep(10 * 60 * 1000);
		System.out.println("finished");
	}
}
