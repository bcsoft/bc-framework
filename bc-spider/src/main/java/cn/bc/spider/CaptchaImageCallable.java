package cn.bc.spider;

import cn.bc.docs.domain.Attach;
import cn.bc.spider.callable.TextCallable;
import org.apache.tools.ant.util.DateUtils;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * 验证码图片的获取
 *
 * @author dragon
 */
public class CaptchaImageCallable extends TextCallable {
  private String root = Attach.DATA_REAL_PATH;// 验证码保存到的根路径
  private String subpath = "temp/captcha";// 验证码保存到的先对子路径
  private String name;// 验证码保存到的文件名称（不含路径和扩展名的部分）
  private String ext = "jpg";// 验证码的扩展名（不含符号"."）

  private String path;// 验证码保存到的文件全路径

  public CaptchaImageCallable() {
    super();

    // 默认 get
    this.setMethod("get");

    // 创建一个未分类的默认 group
    this.setGroup("none");

    // 时间搓作为默认文件名
    this.name = DateUtils.format(new Date(), "yyyyMMddHHmmssSSSS");
  }

  public CaptchaImageCallable(String name) {
    this();
    this.name = name;
  }

  @Override
  public String parseResponse() throws Exception {
    // 复制流到指定的文件
    String dir = this.subpath + "/" + this.getGroup();
    File file = new File(this.root + "/" + dir);
    if (!file.exists()) {
      file.mkdirs();
    }
    this.path = dir + "/" + name + "." + ext;
    file = new File(this.root + "/" + this.path);
    FileCopyUtils.copy(this.responseEntity.getContent(), new FileOutputStream(file));

    // 返回验证码图片保存到的全路径
    return this.path;
  }

  public String getSubpath() {
    return subpath;
  }

  public void setSubpath(String root) {
    this.subpath = root;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public String getRoot() {
    return root;
  }

  public void setRoot(String root) {
    this.root = root;
  }
}