package cn.bc.photo.util;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author dragon
 */
public class QRCodeUtilsTest {
  @Test
  public void encodeToPNGFile() throws Exception {
    File file = new File("e:\\t\\zxing\\qrcode.png");
    String content = "http://detail.tmall.com/item.htm?spm=a1z10.1.w6129549-4101809663.2.n4kSGW&id=23289408383&rn=cd665709a25a50ce8bad23b01868a212";
    QRCodeUtils.encode(content, file);
  }

  @Test
  public void encodeToJPGFile() throws Exception {
    File file = new File("e:\\t\\zxing\\qrcode.jpg");
    String content = "http://192.168.0.222/bctest";
    QRCodeUtils.encode(content, file);
  }

  @Test
  public void encodeToStream() throws Exception {
    String format = "png";
    File file = new File("e:\\t\\zxing\\qrcode." + format);
    if (!file.exists())
      file.mkdirs();
    OutputStream stream = new FileOutputStream(file);
    String content = "http://192.168.0.222/bctest";
    QRCodeUtils.encode(content, stream, format);
  }

  @Test
  public void decodePNGFile() throws Exception {
    File file = new File("e:\\t\\zxing\\qrcode.png");
    String content = "http://192.168.0.222/bctest";
    QRCodeUtils.encode(content, file);
    Assert.assertEquals(content, QRCodeUtils.decode(file));
  }

  @Test
  public void decodeJPGFile() throws Exception {
    File file = new File("e:\\t\\zxing\\qrcode.jpg");
    String content = "http://192.168.0.222/bctest";
    QRCodeUtils.encode(content, file);
    Assert.assertEquals(content, QRCodeUtils.decode(file));
  }
}