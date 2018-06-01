package cn.bc.spider.callable;

import cn.bc.spider.Result;
import cn.bc.spider.http.TaskExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class StreamCallableTest {
  private static Log logger = LogFactory.getLog(StreamCallableTest.class);

  @Test
  public void test() throws Exception {
    StreamCallable c = new StreamCallable();
    c.setMethod("get");
    c.setUrl("http://www.baidu.com/img/shouye_b5486898c692066bd2cbaeda86d74448.gif");

    Result<InputStream> result = TaskExecutor.get(c);
    Assert.assertTrue(result.isSuccess());
    if (logger.isDebugEnabled())
      logger.debug(result.getData());

    String file = "d:\\t\\baidu.gif";
    c.export(new FileOutputStream(file));
    Assert.assertTrue(new File(file).exists());
  }
}
