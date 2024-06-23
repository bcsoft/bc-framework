/**
 *
 */
package cn.bc.docs.util;

import cn.bc.core.exception.CoreException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片缩放、裁剪工具
 *
 * @author dragon
 */
public class ImageUtils {

  /**
   * 将图片写入文件
   *
   * @param image     要写入的图片
   * @param fileName  要写入到的文件路径名
   * @param extension 图片格式，即文件扩展名
   * @throws IOException
   */
  public static void write(RenderedImage image, String fileName,
                           String extension) throws IOException {
    ImageIO.write(image, extension, new File(fileName));
  }

  /**
   * 将图片写入文件
   *
   * @param image     要写入的图片
   * @param file      要写入到的文件
   * @param extension 图片格式，即文件扩展名
   * @throws IOException
   */
  public static void write(RenderedImage image, File file, String extension)
    throws IOException {
    ImageIO.write(image, extension, file);
  }

  /**
   * 裁剪图片 (png图片不要使用这个方法)
   *
   * @param inputStream 图片文件输入流
   * @param x           起始点（左上角）x坐标
   * @param y           起始点（左上角）y坐标
   * @param width       区域的宽度
   * @param height      区域的高度
   * @throws IOException IO异常
   */
  public static BufferedImage crop(InputStream inputStream, int x, int y,
                                   int width, int height) throws IOException {
    return crop(inputStream, x, y, width, height, null);
  }

  /**
   * 裁剪图片
   *
   * @param inputStream 图片文件输入流
   * @param x           起始点（左上角）x坐标
   * @param y           起始点（左上角）y坐标
   * @param width       区域的宽度
   * @param height      区域的高度
   * @return 处理后的图片BufferedImage，不能为null
   * @throws IOException IO异常
   */
  public static BufferedImage crop(InputStream inputStream, int x, int y,
                                   int width, int height, String extention) throws IOException {
    BufferedImage image = ImageIO.read(inputStream);

    // 如果裁剪区超出原图范围则使用原图
    if (x + width > image.getWidth()) {
      x = 0;
      width = image.getWidth();
    }
    if (y + height > image.getHeight()) {
      y = 0;
      height = image.getHeight();
    }
    return image.getSubimage(x, y, width, height);
  }

  /**
   * 缩放图像 (png图片不要使用这个方法)
   *
   * @param inputStream 图片文件输入流
   * @param zoomRatio   缩放比例
   * @param extention   图片类型，如png，为空则使用原图getType返回的值
   * @return 处理后的图片BufferedImage
   * @throws IOException IO异常
   */
  public static BufferedImage zoom(InputStream inputStream, double zoomRatio)
    throws IOException {
    return zoom(inputStream, zoomRatio, null);
  }

  /**
   * 缩放图像
   *
   * @param inputStream 图片文件输入流
   * @param zoomRatio   缩放比例
   * @param extention   图片类型，如png，为空则使用原图getType返回的值
   * @return 处理后的图片BufferedImage
   * @throws IOException IO异常
   */
  public static BufferedImage zoom(InputStream inputStream, double zoomRatio,
                                   String extention) throws IOException {
    if (zoomRatio <= 0)
      zoomRatio = 1;

    BufferedImage image = ImageIO.read(inputStream);

    int width = new Double(image.getWidth() * zoomRatio).intValue();
    int height = new Double(image.getHeight() * zoomRatio).intValue();

    BufferedImage bufferedImage = new BufferedImage(width, height,
      getTargetImgType(image, extention));
    Graphics graphics = bufferedImage.createGraphics();
    graphics.drawImage(
      image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,
      0, null);

    return bufferedImage;
  }

  /**
   * 先缩放原图后裁剪图片
   *
   * @param inputStream 图片文件输入流
   * @param zoomRatio   缩放比例
   * @param x           起始点（左上角）x坐标
   * @param y           起始点（左上角）y坐标
   * @param width       区域的宽度
   * @param height      区域的高度
   * @param extention   图片类型，如png，为空则使用原图getType返回的值
   * @return 处理后的图片BufferedImage，不能为null
   * @throws IOException IO异常
   */
  public static BufferedImage zoomAndCrop(InputStream inputStream,
                                          double zoomRatio, int x, int y, int width, int height,
                                          String extention) throws IOException {
    if (zoomRatio <= 0)
      zoomRatio = 1;

    BufferedImage image = ImageIO.read(inputStream);

    int newWidth = new Double(image.getWidth() * zoomRatio).intValue();
    int newHeight = new Double(image.getHeight() * zoomRatio).intValue();

    BufferedImage bufferedImage = new BufferedImage(newWidth, newHeight,
      getTargetImgType(image, extention));
    Graphics graphics = bufferedImage.createGraphics();
    graphics.drawImage(image.getScaledInstance(newWidth, newHeight,
      Image.SCALE_SMOOTH), 0, 0, null);

    // 如果裁剪区超出原图范围则使用原图
    if (x + width > newWidth) {
      x = 0;
      width = newWidth;
    }
    if (y + height > newHeight) {
      y = 0;
      height = newHeight;
    }

    return bufferedImage.getSubimage(x, y, width, height);
  }

  /**
   * 先裁剪图片后缩放到指定的尺寸
   *
   * @param inputStream 图片文件输入流
   * @param zoomRatio   缩放比例
   * @param x           裁剪的起始点（左上角）x坐标
   * @param y           裁剪的起始点（左上角）y坐标
   * @param width       裁剪区域的宽度
   * @param height      裁剪区域的高度
   * @param newWidth    裁剪后缩放到的宽度
   * @param newHeight   裁剪后缩放到的高度
   * @param extention   图片类型，如png，为空则使用原图getType返回的值
   * @return 处理后的图片BufferedImage，不能为null
   * @throws IOException IO异常
   */
  public static BufferedImage cropAndZoom(InputStream inputStream, int x,
                                          int y, int width, int height, int newWidth, int newHeight,
                                          String extention) throws IOException {
    // 裁剪图片
    BufferedImage cropImg = crop(inputStream, x, y, width, height,
      extention);

    // c处理缩放后的参数
    if (newWidth <= 0) {
      newWidth = cropImg.getWidth();
    }
    if (newHeight <= 0) {
      newHeight = cropImg.getHeight();
    }

    // 缩放图片
    if (cropImg.getWidth() != newWidth || cropImg.getHeight() != newHeight) {
      BufferedImage zoomImg = new BufferedImage(newWidth, newHeight,
        getTargetImgType(cropImg, extention));
      Graphics graphics = zoomImg.createGraphics();
      graphics.drawImage(cropImg.getScaledInstance(newWidth, newHeight,
        Image.SCALE_SMOOTH), 0, 0, null);
      return zoomImg;
    } else {
      return cropImg;
    }
  }

  private static int getTargetImgType(BufferedImage image, String extension) {
    int type = image.getType();
    if (type == BufferedImage.TYPE_CUSTOM) {
      if ("png".equals(extension)) {// BufferedImage.TYPE_中没有对应png类型的定义
        return BufferedImage.TYPE_INT_ARGB;// 使用这个可以保证图片依然保持透明度
      }
    }
    return type;
  }

  /**
   * 将图片沿水平方向合并在一起
   *
   * @param imagers 要合并的图像配置（包含间隙的定义）
   * @return
   * @throws java.io.IOException
   */
  public static BufferedImage combineHorizontal(Imager[] imagers) {
    int width = 0, height = 0;
    boolean opaque = false;// 要合并的图片中是否存在不透明的

    // 计算合并后图片的宽度和高度
    for (Imager imager : imagers) {
      width += imager.getWidth();
      height = Math.max(height, imager.getHeight());
      if (imager.getImage().getTransparency() == Transparency.OPAQUE) {
        opaque = true;
      }
    }

    // 创建新的空白图像
    BufferedImage imageNew = new BufferedImage(width, height, opaque ?
      BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);

    // 如果有一张图不支持透明，就设置全局背景为白色（否则保存为jpg时，空白区为黑色）
    if (opaque) {
      Graphics2D g = (Graphics2D) imageNew.getGraphics();
      g.setBackground(Color.WHITE);
      g.clearRect(0, 0, width, height);
    }

    // 循环每张图片进行合并
    int startX = 0, startY = 0;
    int[] ImageArrayOne;
    int i = 0;
    for (Imager imager : imagers) {
      width = imager.getRealWidth();// 原始图像宽度
      height = imager.getRealHeight();// 原始图像高度
      startX += imager.getLeft();// 左间隙
      startY = imager.getTop();// 上间隙

      // 原图的数据
      ImageArrayOne = new int[width * height];
      ImageArrayOne = imager.getImage().getRGB(0, 0, width, height, ImageArrayOne, 0, width);

      // 将原图合并到新图
      imageNew.setRGB(startX, startY, width, height, ImageArrayOne, 0, width);

      // 下一起点
      startX += width + imager.getRight();
    }

    // 返回合并后的结果
    return imageNew;
  }

  /**
   * 将图片沿垂直方向合并在一起
   *
   * @param imagers 要合并的图像配置（包含间隙的定义）
   * @return
   * @throws java.io.IOException
   */
  public static BufferedImage combineVertical(Imager[] imagers) {
    int width = 0, height = 0;
    boolean opaque = false;// 要合并的图片中是否存在不透明的

    // 计算合并后图片的宽度和高度
    for (Imager imager : imagers) {
      height += imager.getHeight();
      width = Math.max(width, imager.getWidth());
      if (imager.getImage().getTransparency() == Transparency.OPAQUE) {
        opaque = true;
      }
    }

    // 创建新的空白图像
    BufferedImage imageNew = new BufferedImage(width, height, opaque ?
      BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);

    // 如果有一张图不支持透明，就设置全局背景为白色（否则保存为jpg时，空白区为黑色）
    if (opaque) {
      Graphics2D g = (Graphics2D) imageNew.getGraphics();
      g.setBackground(Color.WHITE);
      g.clearRect(0, 0, width, height);
    }

    // 循环每张图片进行合并
    int startX = 0, startY = 0;
    int[] ImageArrayOne;
    int i = 0;
    for (Imager imager : imagers) {
      width = imager.getRealWidth();// 原始图像宽度
      height = imager.getRealHeight();// 原始图像高度
      startX = imager.getLeft();// 左间隙
      startY += imager.getTop();// 上间隙

      // 原图的数据
      ImageArrayOne = new int[width * height];
      ImageArrayOne = imager.getImage().getRGB(0, 0, width, height, ImageArrayOne, 0, width);

      // 将原图合并到新图
      imageNew.setRGB(startX, startY, width, height, ImageArrayOne, 0, width);

      // 下一起点
      startY += height + imager.getBottom();
    }

    // 返回合并后的结果
    return imageNew;
  }

  public static BufferedImage combineHorizontal(InputStream[] imageStreams) throws IOException {
    return combineHorizontal(imageStreams, 0);
  }

  public static BufferedImage combineHorizontal(InputStream[] imageStreams, int gap) throws IOException {
    Imager[] imagers = new Imager[imageStreams.length];
    for (int i = 0; i < imageStreams.length; i++) {
      imagers[i] = new Imager(imageStreams[i], 0, 0, 0, i > 0 ? gap : 0);// 上、右、下间隙统一为0
    }
    return combineHorizontal(imagers);
  }

  public static BufferedImage combineHorizontal(BufferedImage[] images) {
    return combineHorizontal(images, 0);
  }

  /**
   * 将图片沿水平方向合并在一起
   *
   * @param images 要合并的图像
   * @param gap    合并图像之间的水平间隙
   * @return
   * @throws IOException
   */
  public static BufferedImage combineHorizontal(BufferedImage[] images, int gap) {
    Imager[] imagers = new Imager[images.length];
    for (int i = 0; i < images.length; i++) {
      imagers[i] = new Imager(images[i], 0, 0, 0, i > 0 ? gap : 0);// 上、右、下间隙统一为0
    }
    return combineHorizontal(imagers);
  }

  public static BufferedImage combineVertical(InputStream[] imageStreams) throws IOException {
    return combineVertical(imageStreams, 0);
  }

  public static BufferedImage combineVertical(InputStream[] imageStreams, int gap) throws IOException {
    Imager[] imagers = new Imager[imageStreams.length];
    for (int i = 0; i < imageStreams.length; i++) {
      imagers[i] = new Imager(imageStreams[i], 0, 0, 0, i > 0 ? gap : 0);// 上、右、下间隙统一为0
    }
    return combineVertical(imagers);
  }

  public static BufferedImage combineVertical(BufferedImage[] images) {
    return combineVertical(images, 0);
  }

  /**
   * 将图片沿垂直方向合并在一起
   *
   * @param images 要合并的图像
   * @param gap    合并图像之间的垂直间隙
   * @return
   */
  public static BufferedImage combineVertical(BufferedImage[] images, int gap) {
    Imager[] imagers = new Imager[images.length];
    for (int i = 0; i < images.length; i++) {
      imagers[i] = new Imager(images[i], i > 0 ? gap : 0, 0, 0, 0);// 右、下、左间隙统一为0
    }
    return combineVertical(imagers);
  }

  public static BufferedImage combineMix(InputStream[] imageStreams,
                                         String mixConfig) throws IOException {
    BufferedImage[] images = new BufferedImage[imageStreams.length];
    for (int i = 0; i < imageStreams.length; i++) {
      images[i] = ImageIO.read(imageStreams[i]);
    }
    return combineMix(images, mixConfig);
  }

  /**
   * 按指定的配置沿水平、垂直方向混合合并图片
   *
   * @param images    要合并的图片列表
   * @param mixConfig 合并方式，如"1,1,1;1,1"表示前三张水平合并，后2张水平合并，之后再垂直合并；
   *                  如果要控制合并的间隙，如"1:5 6 7 8,1;1,1"，表示第一张图片合并时上、右、下、左的间隙为5、6、7、8个像素；
   *                  "1:5 6,1;1,1"，表示第一张图片合并时上下、左右的间隙为5、6个像素；
   *                  "h:5"代表连续水平合并，每个图片之间的合并间隙为5个像素，"h"等同于"h:0"；
   *                  "v:5"代表连续垂直合并，每个图片之间的合并间隙为5个像素，"v"等同于"v:0"；
   * @return
   * @throws IOException
   */
  public static BufferedImage combineMix(BufferedImage[] images, String mixConfig) {
    // 处理"h:5"、"v:5"格式，将其转换为标准格式
    mixConfig = rebuildSpecialMixConfig(mixConfig, images.length);

    String[] v_cfgs = mixConfig.split(";");
    Imager[] v_imagers = new Imager[v_cfgs.length];// 要垂直合并的图

    String[] h_cfgs, _gaps;
    int[] gaps;
    Imager[] h_imagers;// 要水平合并的图
    int c = 0, left, right, top, bottom, k;
    for (int i = 0; i < v_cfgs.length; i++) {
      h_cfgs = v_cfgs[i].split(",");
      h_imagers = new Imager[h_cfgs.length];
      for (int j = 0; j < h_imagers.length; j++) {
        // 从配置中计算出间隙
        k = h_cfgs[j].indexOf(":");
        if (k != -1) {
          _gaps = h_cfgs[j].split(":")[1].split(" ");
        } else {
          _gaps = null;
        }
        gaps = new int[]{0, 0, 0, 0};// 上 右 下 左
        if (_gaps != null) {
          if (_gaps.length == 1) {// 统一间隙
            gaps[0] = Integer.parseInt(_gaps[0]);
            gaps[1] = gaps[0];
            gaps[2] = gaps[0];
            gaps[3] = gaps[0];
          } else if (_gaps.length == 2) {// 上下、左右间隙
            gaps[0] = Integer.parseInt(_gaps[0]);
            gaps[1] = Integer.parseInt(_gaps[1]);
            gaps[2] = gaps[0];
            gaps[3] = gaps[1];
          } else if (_gaps.length == 4) {// 上、右、下、左间隙
            gaps[0] = Integer.parseInt(_gaps[0]);
            gaps[1] = Integer.parseInt(_gaps[1]);
            gaps[2] = Integer.parseInt(_gaps[2]);
            gaps[3] = Integer.parseInt(_gaps[3]);
          } else {
            throw new CoreException("不支持的间隙配置：" + h_cfgs[j].split(":")[1]);
          }
        }

        // 构建图片合并对象
        h_imagers[j] = new Imager(images[c + j], gaps[0], gaps[1], gaps[2], gaps[3]);
      }
      c += h_imagers.length;
      v_imagers[i] = new Imager(combineHorizontal(h_imagers));
    }
    return combineVertical(v_imagers);
  }

  // 处理"h:5"、"v:5"格式，将其转换为标准格式
  private static String rebuildSpecialMixConfig(String mixConfig, int count) {
    if (mixConfig != null && (mixConfig.startsWith("h") || mixConfig.startsWith("v"))) {
      int gap = 0;// 合并间隙
      int i = mixConfig.indexOf(":");
      if (i != -1) {
        gap = Integer.parseInt(mixConfig.substring(i + 1));
      }
      String newConfig = "";
      if (mixConfig.startsWith("h")) {// 连续水平合并 h:n
        for (i = 0; i < count; i++) {
          newConfig += (i == 0 ? "1" : ",1:0 0 0 " + gap);
        }
      } else if (mixConfig.startsWith("v")) {// 连续垂直合并 v:n
        for (i = 0; i < count; i++) {
          newConfig += (i == 0 ? "1" : ";1:" + gap + " 0 0 0");
        }
      }
      return newConfig;
    } else {
      return mixConfig;
    }
  }
}