package cn.bc.remoting.client;

import cn.bc.core.util.DateUtils;
import cn.bc.remoting.msoffice.WordSaveFormat;
import cn.bc.remoting.service.WordService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.Date;

public class ClientMain {
  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    System.out.println("starting ...");

    // 初始化 Spring
    ApplicationContext context = new ClassPathXmlApplicationContext(
      "cn/bc/remoting/client/spring.xml");

    // 获得RMI服务
    WordService service = context.getBean(WordService.class);
    System.out.println("service=" + service);

    // 调用test服务
    boolean result = service.test("testToken");
    System.out.println("result:test=" + result);

    // 字节测试
    testBytes(service);

    // 文件测试
    testFile(service);
  }

  /**
   * @param service
   * @throws IOException
   */
  private static void testFile(WordService service) throws IOException {
    Date now = new Date();
    boolean result = service.convertFormat("test", "test.docx",
      "test_file.pdf");
    System.out.println("testFile:result=" + result);
    System.out.println("--finished:testFile--"
      + DateUtils.getWasteTime(now));
  }

  /**
   * @param now
   * @param service
   * @throws FileNotFoundException
   * @throws IOException
   */
  protected static void testBytes(WordService service) throws Exception {
    Date now = new Date();
    // 字节测试;
    InputStream source = new FileInputStream("/t/data_rmi/source/test.docx");
    System.out.println("testBytes:source=" + source);
    byte[] bs = service.convertFormat("testBytes",
      FileCopyUtils.copyToByteArray(source),
      WordSaveFormat.DOCX.getKey(), WordSaveFormat.PDF.getKey());

    // 转换后的文件保存
    File toFile = new File("/t/data_rmi/convert/test_bytes.pdf");
    if (!toFile.getParentFile().exists())
      toFile.getParentFile().mkdirs();
    FileCopyUtils.copy(bs, new FileOutputStream(toFile));
    System.out.println("--finished:testBytes--"
      + DateUtils.getWasteTime(now));
  }
}
