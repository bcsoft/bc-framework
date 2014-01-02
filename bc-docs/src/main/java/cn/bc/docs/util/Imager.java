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

    /**
     * @param imageStream 图片的文件流
     * @param top         上间隙
     * @param right       右间隙
     * @param bottom      下间隙
     * @param left        左间隙
     * @throws IOException
     */
    public Imager(InputStream imageStream, int top, int right, int bottom, int left) throws IOException {
        this(ImageIO.read(imageStream), top, right, bottom, left);
    }

    /**
     * @param imageStream 图片的文件流
     * @param topBottom   上下间隙
     * @param leftRight   左右间隙
     * @throws IOException
     */
    public Imager(InputStream imageStream, int topBottom, int leftRight) throws IOException {
        this(ImageIO.read(imageStream), topBottom, leftRight);
    }

    /**
     * @param imageStream 图片的文件流
     * @param all         周边间隙
     * @throws IOException
     */
    public Imager(InputStream imageStream, int all) throws IOException {
        this(ImageIO.read(imageStream), all);
    }

    /**
     * @param imageStream 图片的文件流
     * @throws IOException
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
     * @throws IOException
     */
    public Imager(BufferedImage image, int top, int right, int bottom, int left) {
        this.image = image;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    /**
     * @param image     图片
     * @param topBottom 上下间隙
     * @param leftRight 左右间隙
     * @throws IOException
     */
    public Imager(BufferedImage image, int topBottom, int leftRight) {
        this(image, topBottom, leftRight, topBottom, leftRight);
    }

    /**
     * @param image 图片
     * @param all   周边间隙
     * @throws IOException
     */
    public Imager(BufferedImage image, int all) {
        this(image, all, all, all, all);
    }

    public Imager(BufferedImage image) {
        this(image, 0, 0, 0, 0);
    }

    /**
     * 获取包含间隙的宽度
     *
     * @return
     */
    public int getWidth() {
        return this.getRealWidth() + this.getLeft() + this.getRight();
    }

    /**
     * 获取图片的实际像素宽度（不含间隙）
     *
     * @return
     */
    public int getRealWidth() {
        return image == null ? 0 : image.getWidth();
    }

    /**
     * 获取包含间隙的高度
     *
     * @return
     */
    public int getHeight() {
        return this.getRealHeight() + this.getTop() + this.getBottom();
    }

    /**
     * 获取图片的实际像素高度（不含间隙）
     *
     * @return
     */
    public int getRealHeight() {
        return image == null ? 0 : image.getHeight();
    }

    /**
     * 图片
     */
    public BufferedImage getImage() {
        return image;
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
}