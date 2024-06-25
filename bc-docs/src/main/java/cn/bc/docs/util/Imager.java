package cn.bc.docs.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * 要合并图片的数据封装
 */
public class Imager {
  private BufferedImage image;// 原图
  private int left;// 左间隙
  private int right;// 右间隙
  private int top;// 上间隙
  private int bottom;// 下间隙
  private double scale = 1d;// 缩放比例

  /**
   * @param imageStream 图片的文件流
   * @param top         上间隙
   * @param right       右间隙
   * @param bottom      下间隙
   * @param left        左间隙
   */
  public Imager(InputStream imageStream, int top, int right, int bottom, int left) throws IOException {
    this(ImageIO.read(imageStream), top, right, bottom, left, 1);
  }

  /**
   * @param imageStream 图片的文件流
   * @param topBottom   上下间隙
   * @param leftRight   左右间隙
   */
  public Imager(InputStream imageStream, int topBottom, int leftRight) throws IOException {
    this(ImageIO.read(imageStream), topBottom, leftRight);
  }

  /**
   * @param imageStream 图片的文件流
   * @param all         周边间隙
   */
  public Imager(InputStream imageStream, int all) throws IOException {
    this(ImageIO.read(imageStream), all);
  }

  /**
   * @param imageStream 图片的文件流
   */
  public Imager(InputStream imageStream) throws IOException {
    this(ImageIO.read(imageStream));
  }

  /**
   * @param image  图片
   * @param top    上间隙
   * @param right  右间隙
   * @param bottom 下间隙
   * @param left   左间隙
   * @param scale  缩放比例
   */
  public Imager(BufferedImage image, int top, int right, int bottom, int left, double scale) {
    this.image = image;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
    this.scale = scale;
  }

  /**
   * @param image  图片
   * @param top    上间隙
   * @param right  右间隙
   * @param bottom 下间隙
   * @param left   左间隙
   */
  public Imager(BufferedImage image, int top, int right, int bottom, int left) {
    this(image, top, right, bottom, left, 1);
  }

  /**
   * @param image     图片
   * @param topBottom 上下间隙
   * @param leftRight 左右间隙
   */
  public Imager(BufferedImage image, int topBottom, int leftRight) {
    this(image, topBottom, leftRight, topBottom, leftRight, 1);
  }

  /**
   * @param image 图片
   * @param all   周边间隙
   */
  public Imager(BufferedImage image, int all) {
    this(image, all, all, all, all, 1);
  }

  public Imager(BufferedImage image) {
    this(image, 0, 0, 0, 0, 1);
  }

  /**
   * 获取包含间隙的宽度
   */
  public int getWidth() {
    return this.getRealWidth() + this.getLeft() + this.getRight();
  }

  /**
   * 获取缩放后的宽度（含间隙）
   */
  public int getScaleWidth() {
    return (int) (this.getRealWidth() * this.scale) + this.getLeft() + this.getRight();
  }

  /**
   * 获取图片的实际像素宽度（不含间隙）
   */
  public int getRealWidth() {
    return image == null ? 0 : image.getWidth();
  }

  /**
   * 获取图片缩放后的实际像素宽度（不含间隙）
   */
  public int getScaleRealWidth() {
    return image == null ? 0 : (int) (image.getWidth() * this.scale);
  }

  /**
   * 获取包含间隙的高度
   */
  public int getHeight() {
    return this.getRealHeight() + this.getTop() + this.getBottom();
  }

  /**
   * 获取缩放后的高度（含间隙）
   */
  public int getScaleHeight() {
    return (int) (this.getRealHeight() * this.scale) + this.getTop() + this.getBottom();
  }

  /**
   * 获取图片的实际像素高度（不含间隙）
   */
  public int getRealHeight() {
    return image == null ? 0 : image.getHeight();
  }

  /**
   * 获取图片缩放后的实际像素高度（不含间隙）
   */
  public int getScaleRealHeight() {
    return image == null ? 0 : (int) (image.getHeight() * this.scale);
  }

  /**
   * 原始图片
   */
  public BufferedImage getImage() {
    return image;
  }

  /**
   * 缩放后的图片
   */
  public BufferedImage getScaleImage() {
    if (this.scale == 1d) return this.image;
    else {
      try {
        return ImageUtils.zoom(this.image, this.scale, "png");
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }

  /**
   * 下间隙
   */
  public int getBottom() {
    return bottom;
  }

  /**
   * 上间隙
   */
  public int getTop() {
    return top;
  }

  /**
   * 右间隙
   */
  public int getRight() {
    return right;
  }

  /**
   * 左间隙
   */
  public int getLeft() {
    return left;
  }

  /**
   * 缩放比例
   */
  public double getScale() {
    return scale;
  }
}