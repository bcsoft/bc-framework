package cn.bc.spider;

import org.springframework.util.FileCopyUtils;

import java.io.*;

/**
 * 验证码的获取
 *
 * @author dragon
 */
public class CaptchaBaseCallable extends StreamCallable<String> {
  private String root;// 验证码保存到的跟路径
  private String key;// 验证码保存到的文件
  private String captcha; // 用户输入的验证码值

  private int repeat = 24;// 默认24次，约2分钟内
  private long sleep = 5000;// 默认每次等待5秒

  public CaptchaBaseCallable(String key) {
    super();
    this.key = key;
  }

  @Override
  protected void parseStream(InputStream stream) throws Exception {
    // 复制流到指定的文件
    String dir = this.root + "/" + this.getGroup();
    File file = new File(dir);
    if (!file.exists()) {
      file.mkdirs();
    }
    file = new File(dir + "/" + key + ".jpg");
    FileOutputStream output = new FileOutputStream(file);
    FileCopyUtils.copy(stream, output);

    // 创建保存验证码答案的文件
    file = new File(dir + "/" + key + ".txt");
    output = new FileOutputStream(file);
    output.write("".getBytes());
    output.flush();
    output.close();

    // 循环读取直至等待用户输入结果后再向下执行
    repeatGetUserInput(file);
  }

  /**
   * 循环读取文件的内容直至文件内容不为空或超过指定的时限
   *
   * @param file
   * @throws FileNotFoundException
   * @throws IOException
   * @throws InterruptedException
   */
  protected void repeatGetUserInput(File file) throws FileNotFoundException,
    IOException, InterruptedException {
    FileReader in;
    int c = 0;
    while (c < this.repeat) {// 2分钟内
      in = new FileReader(file);
      this.captcha = FileCopyUtils.copyToString(in);
      System.out.println("time=" + c * sleep / 1000 + ",captcha="
        + captcha);
      if (!this.captcha.isEmpty()) {
        break;
      } else {
        Thread.sleep(sleep);// 等待5秒
        c++;
      }
    }
  }

  @Override
  protected String parseData() {
    // 返回用户输入的验证码
    return this.getCaptcha();
  }

  public String getRoot() {
    return root;
  }

  public void setRoot(String root) {
    this.root = root;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getCaptcha() {
    return captcha;
  }

  public void setCaptcha(String captcha) {
    this.captcha = captcha;
  }

  public int getRepeat() {
    return repeat;
  }

  public void setRepeat(int repeat) {
    this.repeat = repeat;
  }

  public long getSleep() {
    return sleep;
  }

  public void setSleep(long sleep) {
    this.sleep = sleep;
  }
}