package cn.bc.remoting.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.FileCopyUtils;

import cn.bc.core.util.DateUtils;
import cn.bc.remoting.msoffice.WordSaveFormat;
import cn.bc.remoting.service.WordService;

public class ClientMain {
	private static DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSSS");

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		WordSaveFormat a = WordSaveFormat.DOC;
		Date now = new Date();
		System.out.println("starting ..." + (a == WordSaveFormat.DOC));

		// 初始化 Spring
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"cn/bc/remoting/client/spring.xml");

		// 获得RMI服务
		WordService service = context.getBean(WordService.class);
		System.out.println("service=" + service);

		// 调用RMI服务
		boolean result = service.test("testToken");
		System.out.println("result=" + result);

		// 字节测试;
		InputStream source = new FileInputStream("/t/test01.docx");
		System.out.println("source=" + source);
		byte[] bs = service.convertFormat("test",
				FileCopyUtils.copyToByteArray(source), WordSaveFormat.DOCX,
				WordSaveFormat.PDF);

		// 转换后的文件保存
		File toFile = new File("/t/convert/" + df.format(now) + ".pdf");
		if (!toFile.getParentFile().exists())
			toFile.getParentFile().mkdirs();
		FileCopyUtils.copy(bs, new FileOutputStream(toFile));
		System.out.println("--finished1--" + DateUtils.getWasteTime(now));

		// 文件测试
		result = service.convertFormat("test", "test01.docx", "test01.html");
		System.out.println("result2=" + result);

		System.out.println("--finished2--" + DateUtils.getWasteTime(now));
	}
}
