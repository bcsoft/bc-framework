package cn.bc.spider;

import cn.bc.spider.http.TaskExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.util.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class CaptchaImageCallableTest {
  private static Log logger = LogFactory
    .getLog(CaptchaImageCallableTest.class);

  @Test
  public void test() throws Exception {
    // 获取验证码
    String key = DateUtils.format(new Date(), "yyyyMMddHHmmssSSSS");
    logger.info("key=" + key);

    CaptchaImageCallable c = new CaptchaImageCallable(key);
    c.setUrl("https://passport.baidu.com/cgi-bin/genimage?0013679162730156F5D6E41E0F7926A17D8F5AD729523395FB0C0838CB9DADC12B612FB5A773765553D3416201012AA9978BAE573C17E24EF4C5010984D0049B33E314946C1E624AFFA82FB4DFEE0831DC0F103023580F57962BB41FE099C1FE7EAFB3BFE512239007014224979AB21C66FD9D7F26E9645B0F98D40E9E43A332D516E277AC7657B6FCC92D431DC65E1D62F398B16AFECD46F8663A284596CBC111F518B069D553A1");
    Result<String> r = TaskExecutor.get(c);
    Assert.assertTrue(r.isSuccess());

    // 打印响应的html内容
    logger.info("captcha file=" + r.getData());
  }
}