/**
 *
 */
package cn.bc.docs.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

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

    public static BufferedImage combineHorizontal(InputStream[] imageStreams) throws IOException {
        return combineHorizontal(imageStreams, 0);
    }

    public static BufferedImage combineHorizontal(InputStream[] imageStreams, int gap) throws IOException {
        BufferedImage[] images = new BufferedImage[imageStreams.length];
        for (int i = 0; i < imageStreams.length; i++) {
            images[i] = ImageIO.read(imageStreams[i]);
        }
        return combineHorizontal(images, gap);
    }

    public static BufferedImage combineHorizontal(BufferedImage[] images) {
        return combineHorizontal(images, 0);
    }

    /**
     * 将图片沿水平方向合并在一起
     *
     * @param images 要合并的图像
     * @param gap    合并图像之间的间隙
     * @return
     * @throws IOException
     */
    public static BufferedImage combineHorizontal(BufferedImage[] images, int gap) {
        int width = 0, height = 0;
        boolean opaque = false;// 要合并的图片中是否存在不透明的
        int i = 0;
        for (BufferedImage image : images) {
            width += image.getWidth();
            height = Math.max(height, image.getHeight());
            if (image.getTransparency() == Transparency.OPAQUE) {
                opaque = true;
            }

            // 考虑间隙
            if (i > 0) width = width + gap;
            i++;
        }

        // 创建空白图像
        BufferedImage imageNew = new BufferedImage(width, height, opaque ?
                BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);

        // 如果有一张图不支持透明，就设置全局背景为白色（否则保存为jpg时，空白区为黑色）
        if (opaque) {
            Graphics2D g = (Graphics2D) imageNew.getGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, width, height);
        }

        int startX = 0;
        int[] ImageArrayOne;
        i = 0;
        for (BufferedImage image1 : images) {
            width = image1.getWidth();
            height = image1.getHeight();

            ImageArrayOne = new int[width * height];
            ImageArrayOne = image1.getRGB(0, 0, width, height, ImageArrayOne,
                    0, width);

            // 添加间隙
            if (i > 0) startX = startX + gap;
            i++;

            imageNew.setRGB(startX, 0, width, height, ImageArrayOne, 0, width);
            startX += width;
        }

        return imageNew;
    }

    public static BufferedImage combineVertical(InputStream[] imageStreams) throws IOException {
        return combineVertical(imageStreams, 0);
    }

    public static BufferedImage combineVertical(InputStream[] imageStreams, int gap) throws IOException {
        BufferedImage[] images = new BufferedImage[imageStreams.length];
        for (int i = 0; i < imageStreams.length; i++) {
            images[i] = ImageIO.read(imageStreams[i]);
        }
        return combineVertical(images, gap);
    }

    public static BufferedImage combineVertical(BufferedImage[] images) {
        return combineVertical(images, 0);
    }

    /**
     * 将图片沿垂直方向合并在一起
     *
     * @param images 要合并的图像
     * @param gap    合并图像之间的间隙
     * @return
     */
    public static BufferedImage combineVertical(BufferedImage[] images, int gap) {
        int width = 0, height = 0;
        boolean opaque = false;// 要合并的图片中是否存在不透明的
        int i = 0;
        for (BufferedImage image : images) {
            width = Math.max(width, image.getWidth());
            height += image.getHeight();
            if (image.getTransparency() == Transparency.OPAQUE) {
                opaque = true;
            }

            // 考虑间隙
            if (i > 0) height = height + gap;
            i++;
        }

        // 创建空白图像
        BufferedImage imageNew = new BufferedImage(width, height, opaque ?
                BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);

        // 如果有一张图不支持透明，就设置全局背景为白色（否则保存为jpg时，空白区为黑色）
        if (opaque) {
            Graphics2D g = (Graphics2D) imageNew.getGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, width, height);
        }

        int startY = 0;
        int[] ImageArrayOne;
        i = 0;
        for (BufferedImage image1 : images) {
            width = image1.getWidth();
            height = image1.getHeight();
            ImageArrayOne = new int[width * height];
            ImageArrayOne = image1.getRGB(0, 0, width, height, ImageArrayOne,
                    0, width);

            // 添加间隙
            if (i > 0) startY = startY + gap;
            i++;

            imageNew.setRGB(0, startY, width, height, ImageArrayOne, 0, width);
            startY += height;
        }

        return imageNew;
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
     * @param imageStreams
     * @param mixConfig    如"1,1,1;1,1"表示前三张水平合并，后2张水平合并，之后再垂直合并
     * @return
     * @throws IOException
     */
    public static BufferedImage combineMix(BufferedImage[] images,
                                           String mixConfig) {
        String[] vertical = mixConfig.split(";");
        BufferedImage[] vimages = new BufferedImage[vertical.length];
        String[][] all = new String[vertical.length][];

        BufferedImage[] himages;
        int c = 0;
        for (int i = 0; i < vertical.length; i++) {
            all[i] = vertical[i].split(",");
            himages = new BufferedImage[all[i].length];
            for (int j = 0; j < himages.length; j++) {
                himages[j] = images[c + j];
            }
            c += himages.length;
            vimages[i] = combineHorizontal(himages);
        }
        return combineVertical(vimages);
    }
}