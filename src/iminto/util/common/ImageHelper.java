package iminto.util.common;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageObserver;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * 图片文件辅助类
 */
public class ImageHelper {

    /**
     * 图片尺寸
     */
    public static class ImgSize {

        /**
         * 宽度
         */
        private int width = 0;
        /**
         * 高度
         */
        private int height = 0;

        /**
         * 图片尺寸
         * @param width 宽度
         * @param height    高度
         */
        public ImgSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        /**
         * 获取高度
         * @return
         */
        public int getHeight() {
            return height;
        }

        /**
         * 获取宽度
         * @return
         */
        public int getWidth() {
            return width;
        }

        /**
         * 设置高度
         * @param height    高度
         */
        public void setHeight(int height) {
            this.height = height;
        }

        /**
         * 设置宽度
         * @param width 宽度
         */
        public void setWidth(int width) {
            this.width = width;
        }

        /**
         * 对比尺寸是否相同
         * @param obj
         * @return
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            return this.hashCode() == obj.hashCode();
        }

        /**
         * 尺寸hash
         * @return
         */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 83 * hash + this.width;
            hash = 83 * hash + this.height;
            return hash;
        }

        @Override
        public String toString() {
            return "ImgSize{" + "width=" + width + " height=" + height + '}';
        }
    }

    /**
     * 获取适应尺寸
     * @param originBuf 源图形
     * @param targetSize      目标固定尺寸
     * @return  依照固定尺寸比例进行缩放的适应尺寸
     * @throws IOException
     */
    private static ImgSize suiteSize(BufferedImage originBuf, ImgSize targetSize) throws IOException {
        return suiteSize(new ImgSize(originBuf.getWidth(), originBuf.getHeight()), targetSize);
    }

    /**
     * 获取适应尺寸
     * @param originImg     源图形
     * @param targetSize  目标固定尺寸
     * @return  依照固定尺寸比例进行缩放的适应尺寸
     * @throws IOException
     */
    public static ImgSize suiteSize(File originImg, ImgSize targetSize) throws IOException {
        BufferedImage originBuf = ImageIO.read(originImg);
        return suiteSize(originBuf, targetSize);
    }

    /**
     * 获取适应尺寸
     * @param originSize  源尺寸
     * @param targetSize  目标固定尺寸
     * @return  依照固定尺寸比例进行缩放的适应尺寸
     */
    public static ImgSize suiteSize(ImgSize originSize, ImgSize targetSize) {
        int originWidth = originSize.getWidth();
        int originHeight = originSize.getHeight();
        // 不需要缩略
        if (originSize.equals(targetSize)) {
            return new ImgSize(originWidth, originHeight);
        }
        int width = targetSize.getWidth();
        int height = targetSize.getHeight();

        /**
         * 制取同比例宽高
         */
        float newWidth = 0F;
        float newHeight = 0F;
        float cop = width / (float) height; // 最优宽高比例
        float copOrigin = originWidth / (float) originHeight; // 当前宽高比例
        //
        float copChange = 0F;
        // 按照比例缩略
        if (copOrigin > cop) { // 当前图片较宽
            // 将宽度缩略到限制的最宽
            newWidth = width;
            copChange = newWidth / (float) originWidth;
            newHeight = originHeight * copChange;
        } else {
            newHeight = height;
            copChange = newHeight / (float) originHeight;
            newWidth = originWidth * copChange;
        }
        return new ImgSize((int) newWidth, (int) newHeight);
    }

    /**
     * <pre>
     * 图片缩放
     * 注意：输出尺寸与原尺寸相同时，目标文件不会被建立。
     * </pre>
     * @param originImg     源图片文件
     * @param resizedImg    输出图片文件
     * @param size     输出尺寸
     * @return  生成是否成功
     * @throws IOException
     */
    public static boolean resize(File originImg, File resizedImg, ImgSize size) throws IOException {
        return resize(originImg, resizedImg, size.getWidth(), size.getHeight());
    }

    /**
     * <pre>
     * 图片缩放
     * 注意：输出尺寸与原尺寸相同时，目标文件将不会被建立。
     * </pre>
     * @param originImg     源图片文件
     * @param resizedImg    输出图片文件
     * @param width     输出宽度
     * @param height    输出高度
     * @return  生成是否成功
     * @throws IOException
     */
    public static boolean resize(File originImg, File resizedImg, int width, int height) throws IOException {
        boolean isResized = false;
        FileLocker oflock = new FileLocker(originImg);
        FileLocker rflock = null;
        try {

            oflock.lockRead();

            BufferedImage originBuf = ImageIO.read(originImg);
            {
                if (originBuf.getWidth() == width //
                        && originBuf.getHeight() == height) {
                    return false;
                }
                FileHelper.tryCreate(resizedImg);
                rflock = new FileLocker(resizedImg);
                rflock.lockWrite();
                /**
                 * resize
                 */
                BufferedImage resizedBuf = new BufferedImage(width, height, Transparency.TRANSLUCENT);
                Graphics2D graphics = resizedBuf.createGraphics();
                // 抗锯齿
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.drawImage(originBuf, 0, 0, width, height, new ImageObserver() {

                    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                        return true;
                    }
                });
                graphics.dispose();
                {
                    ImageIO.write(resizedBuf, "png", resizedImg);
                }
                isResized = true;

            }
        } finally {
            oflock.releaseRead();
            if (rflock != null) {
                rflock.releaseWrite();
            }
        }

        return isResized;
    }

    /**
     * 为图片打水印
     * @param originImg     源图片文件
     * @param markedImg     输出图片文件
     * @param markImg       水印文件
     * @param x             水印位置，横坐标
     * @param y             水印位置，纵坐标
     * @param alpha         透明度 0.0f～1.0f
     * @return      生成是否成功
     * @throws IOException
     */
    public static boolean watermark(File originImg, File markedImg, File markImg, int x, int y, float alpha)
            throws IOException {
        FileHelper.tryCreate(markedImg);
        FileLocker oflock = new FileLocker(originImg);
        FileLocker mflock = new FileLocker(markedImg);
        boolean isMarked = false;
        try {
            oflock.lockRead();
            mflock.lockWrite();
            BufferedImage originBuf = ImageIO.read(originImg);
            int width = originBuf.getWidth();
            int height = originBuf.getHeight();
            {
                /**
                 * marked image
                 */
                BufferedImage markedBuf = new BufferedImage(width, height, Transparency.TRANSLUCENT);
                Graphics2D graphics = markedBuf.createGraphics();
                graphics.drawImage(originBuf, 0, 0, width, height, new ImageObserver() {

                    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                        return true;
                    }
                });
                /**
                 * draw mark image
                 */
                BufferedImage markBuf = ImageIO.read(markImg);
                // 抗锯齿
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 覆盖规则
                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                graphics.drawImage(markBuf, x, y, markBuf.getWidth(), markBuf.getHeight(), new ImageObserver() {

                    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                        return true;
                    }
                });

                graphics.dispose();
                {
                    ImageIO.write(markedBuf, "png", markedImg);
                }
                isMarked = true;
            }
        } finally {
            oflock.releaseRead();
            mflock.releaseWrite();
        }
        return isMarked;
    }

    /**
     * 在图片上写文字
     * @param originImg     源图片文件
     * @param drawedImg     输出图片文件
     * @param line          要写的文字
     * @param x             文字位置，横坐标
     * @param y             文字位置，纵坐标
     * @param alpha         透明度 0.0f～1.0f
     * @return      生成是否成功
     * @throws IOException
     */
    public static boolean drawLine(File originImg, File drawedImg, char[] line, int x, int y, float alpha) throws IOException {
        return drawLine(originImg, drawedImg, line, x, y, alpha, null);
    }

    /**
     * 在图片上写文字
     * @param originImg     源图片文件
     * @param drawedImg     输出图片文件（自动创建、且覆盖原有文件）
     * @param line          要写的文字
     * @param x             文字位置，横坐标
     * @param y             文字位置，纵坐标
     * @param alpha         透明度 0.0f～1.0f
     * @param handler       在写入字符前对Graphics对象进行操作。如：graphics.setColor(Color.RED)
     * @return      生成是否成功
     * @throws IOException JDK 1.6以下，PNG读取有时会抛出：javax.imageio.IIOException: Error reading PNG metadata
     */
    public static boolean drawLine(File originImg, File drawedImg, char[] line, int x, int y, float alpha, GraphicsHandlAble handler) throws IOException {
        FileHelper.tryCreate(drawedImg);
        FileLocker oflock = new FileLocker(originImg);
        FileLocker dflock = new FileLocker(drawedImg);
        boolean isMarked = false;
        try {
            oflock.lockRead();
            dflock.lockWrite();


            BufferedImage originBuf = ImageIO.read(originImg);
            int width = originBuf.getWidth();
            int height = originBuf.getHeight();
            {
                /**
                 * marked image
                 */
                BufferedImage drawedBuf = new BufferedImage(width, height, Transparency.TRANSLUCENT);
                Graphics2D graphics = drawedBuf.createGraphics();
                graphics.drawImage(originBuf, 0, 0, width, height, new ImageObserver() {

                    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                        return true;
                    }
                });
                /**
                 * write line
                 */
                // 抗锯齿
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 覆盖规则,透明度
                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                if (handler != null) {
                    handler.handle(graphics);
                }
                graphics.drawChars(line, 0, line.length, x, y);

                graphics.dispose();
                {
                    ImageIO.write(drawedBuf, "png", drawedImg);
                }
                isMarked = true;
            }
        } finally {
            oflock.releaseRead();
            dflock.releaseWrite();
        }
        return isMarked;
    }
    /**                                                                                                                                           
    Java's ImageIO can't process 4-component images                                                                                             
    and Java2D can't apply AffineTransformOp either,                                                                                            
    so convert raster data to RGB.                                                                                                              
    Technique due to MArk Stephens.                                                                                                             
    Free for any use.                                                                                                                           
  */
    private static BufferedImage createJPEG4(Raster raster) {
        int w = raster.getWidth();
        int h = raster.getHeight();
        byte[] rgb = new byte[w * h * 3];
      
        float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
        float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
        float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
        float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);

        for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3) {
            float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i],
                    cr = 255 - Cr[i];

            double val = y + 1.402 * (cr - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);

            val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);

            val = y + 1.772 * (cb - 128) - k;
            val = (val - 128) * .65f + 128;
            rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff
                    : (byte) (val + 0.5);
        }


        raster = Raster.createInterleavedRaster(new DataBufferByte(rgb, rgb.length), w, h, w * 3, 3, new int[]{0, 1, 2}, null);
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, (WritableRaster) raster, true, null);
    }
    /**
	 * 将内存中一个图片写入目标文件
	 * 
	 * @param im
	 *            图片对象
	 * @param targetFile
	 *            目标文件，根据其后缀，来决定写入何种图片格式
	 */
	public static void write(RenderedImage im, File targetFile) {
			try {
				ImageIO.write(im, FileHelper.getSuffixName(targetFile), targetFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
    /**
	 * 写入一个 JPG 图像
	 * 
	 * @param im
	 *            图像对象
	 * @param targetJpg
	 *            目标输出 JPG 图像文件
	 * @param quality
	 *            质量 0.1f ~ 1.0f
     * @throws IOException 
	 */
	public static void writeJpeg(RenderedImage im, File targetJpg, float quality) throws IOException {
			ImageWriter writer = ImageIO.getImageWritersBySuffix("jpg").next();
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
			ImageOutputStream os = ImageIO.createImageOutputStream(targetJpg);
			writer.setOutput(os);
			writer.write((IIOMetadata) null, new IIOImage(im, null, null), param);	
	}
	
	/**
	 * 自动缩放剪切一个图片，令其符合给定的尺寸
	 * <p>
	 * 如果图片太大，则将其缩小，如果图片太小，则将其放大，多余的部分被裁减
	 * 
	 * @param im
	 *            图像对象
	 * @param w
	 *            宽度
	 * @param h
	 *            高度
	 * @return 被转换后的图像
	 */
	public static BufferedImage clipScale(BufferedImage im, int w, int h) {
		// 获得尺寸
		int oW = im.getWidth();
		int oH = im.getHeight();
		float oR = (float) oW / (float) oH;
		float nR = (float) w / (float) h;

		int nW, nH, x, y;
		/*
		 * 裁减
		 */
		// 原图太宽，计算当原图与画布同高时，原图的等比宽度
		if (oR > nR) {
			nW = (h * oW) / oH;
			nH = h;
			x = (w - nW) / 2;
			y = 0;
		}
		// 原图太长
		else if (oR < nR) {
			nW = w;
			nH = (w * oH) / oW;
			x = 0;
			y = (h - nH) / 2;
		}
		// 比例相同
		else {
			nW = w;
			nH = h;
			x = 0;
			y = 0;
		}
		// 创建图像
		BufferedImage re = new BufferedImage(w, h, ColorSpace.TYPE_RGB);
		re.getGraphics().drawImage(im, x, y, nW, nH, Color.black, null);
		// 返回
		return re;
	}
	/**
	 * 自动等比缩放一个图片，并将其保存成目标图像文件<br />
	 * 多余的部分，用给定背景颜色补上<br />
	 * 如果参数中的宽度或高度为<b>-1</b>的话，着按照指定的高度或宽度对原图等比例缩放图片，不添加背景颜色
	 * <p>
	 * 图片格式支持 png | gif | jpg | bmp | wbmp
	 * 
	 * @param srcIm
	 *            源图像文件对象
	 * @param taIm
	 *            目标图像文件对象
	 * @param w
	 *            宽度
	 * @param h
	 *            高度
	 * @param bgColor
	 *            背景颜色
	 * 
	 * @return 被转换前的图像对象
	 * 
	 * @throws IOException
	 *             当读写文件失败时抛出
	 */
	public static BufferedImage zoomScale(Object srcIm, File taIm, int w, int h, Color bgColor)
			throws IOException {
		BufferedImage old = read(srcIm);
		BufferedImage im = zoomScale(old, w, h, bgColor);
		write(im, taIm);
		return old;
	}
	/**
	 * 自动等比缩放一个图片，并将其保存成目标图像文件<br />
	 * 多余的部分，用给定背景颜色补上<br />
	 * 如果参数中的宽度或高度为<b>-1</b>的话，着按照指定的高度或宽度对原图等比例缩放图片，不添加背景颜色
	 * <p>
	 * 图片格式支持 png | gif | jpg | bmp | wbmp
	 * 
	 * @param srcPath
	 *            源图像路径
	 * @param taPath
	 *            目标图像路径，如果不存在，则创建
	 * @param w
	 *            宽度
	 * @param h
	 *            高度
	 * @param bgColor
	 *            背景颜色
	 * 
	 * @return 被转换前的图像对象
	 * 
	 * @throws IOException
	 *             当读写文件失败时抛出
	 */
	public static BufferedImage zoomScale(String srcPath, String taPath, int w, int h, Color bgColor)
			throws IOException {
		File srcIm = FileHelper.findFile(srcPath);
		if (null == srcIm)
			System.out.printf("Fail to find image file '%s'!", srcPath);

		File taIm = FileHelper.createFileIfNoExists(taPath);
		return zoomScale(srcIm, taIm, w, h, bgColor);
	}
	/**
	 * 自动等比缩放一个图片
	 * 
	 * @param im
	 *            图像对象
	 * @param w
	 *            宽度
	 * @param h
	 *            高度
	 * 
	 * @return 被转换后的图像
	 */
	public static BufferedImage zoomScale(BufferedImage im, int w, int h) {
		// 获得尺寸
		int oW = im.getWidth();
		int oH = im.getHeight();

		int nW = w, nH = h;

		/*
		 * 缩放
		 */
		// 未指定图像高度，根据原图尺寸计算出高度
		if (h == -1) {
			nH = (int) ((float) w / oW * oH);
		}
		// 未指定图像宽度，根据原图尺寸计算出宽度
		else if (w == -1) {
			nW = (int) ((float) h / oH * oW);
		}

		// 创建图像
		BufferedImage re = new BufferedImage(nW, nH, ColorSpace.TYPE_RGB);
		re.getGraphics().drawImage(im, 0, 0, nW, nH, null);
		// 返回
		return re;
	}
	public static BufferedImage zoomScale(BufferedImage im, int w, int h, Color bgColor) {
		if (w == -1 || h == -1) {
			return zoomScale(im, w, h);
		}

		// 检查背景颜色
		bgColor = null == bgColor ? Color.black : bgColor;
		// 获得尺寸
		int oW = im.getWidth();
		int oH = im.getHeight();
		float oR = (float) oW / (float) oH;
		float nR = (float) w / (float) h;

		int nW, nH, x, y;
		/*
		 * 缩放
		 */
		// 原图太宽，计算当原图与画布同高时，原图的等比宽度
		if (oR > nR) {
			nW = w;
			nH = (int) (((float) w) / oR);
			x = 0;
			y = (h - nH) / 2;
		}
		// 原图太长
		else if (oR < nR) {
			nH = h;
			nW = (int) (((float) h) * oR);
			x = (w - nW) / 2;
			y = 0;
		}
		// 比例相同
		else {
			nW = w;
			nH = h;
			x = 0;
			y = 0;
		}

		// 创建图像
		BufferedImage re = new BufferedImage(w, h, ColorSpace.TYPE_RGB);
		// 得到一个绘制接口
		Graphics gc = re.getGraphics();
		gc.setColor(bgColor);
		gc.fillRect(0, 0, w, h);
		gc.drawImage(im, x, y, nW, nH, bgColor, null);
		// 返回
		return re;
	}
	/**
	 * 将一个图片文件读入内存
	 * 
	 * @param img
	 *            图片文件
	 * @return 图片对象
	 */
	public static BufferedImage read(Object img) {
		try {
			if (img instanceof File)
				return ImageIO.read((File) img);
			else if (img instanceof URL)
				img = ((URL) img).openStream();
			if (img instanceof InputStream) {
				File tmp = File.createTempFile("demos_img", ".jpg");
				FileHelper.write(tmp, (InputStream)img);
				tmp.deleteOnExit();
				return read(tmp);
			}
			return null;
		}
		catch (IOException e) {
			try {
					InputStream in = null;
					if (img instanceof File)
						in = new FileInputStream((File)img);
					else if (img instanceof URL)
						in = ((URL)img).openStream();
					else if (img instanceof InputStream)
						in = (InputStream)img;
					if (in != null)
						return readJpeg(in);
			} catch (IOException e2) {
				e2.fillInStackTrace();
			}
			return null;
			//throw Lang.wrapThrow(e);
		}
	}
	/**
	 * 尝试读取JPEG文件的高级方法,可读取32位的jpeg文件
	 * <p/>
	 * 来自: http://stackoverflow.com/questions/2408613/problem-reading-jpeg-image-using-imageio-readfile-file
	 * 
	 * */
	private static BufferedImage readJpeg(InputStream in) throws IOException {
		Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
	    ImageReader reader = null;
	    while(readers.hasNext()) {
	        reader = (ImageReader)readers.next();
	        if(reader.canReadRaster()) {
	            break;
	        }
	    }
	    ImageInputStream input = ImageIO.createImageInputStream(in);
	    reader.setInput(input);
	    //Read the image raster
	    Raster raster = reader.readRaster(0, null); 
	    BufferedImage image = createJPEG4(raster);
	    File tmp = File.createTempFile("nutz.img", "jpg"); //需要写到文件,然后重新解析哦
	    writeJpeg(image, tmp, 1);
	    return read(tmp);
	}
	@SuppressWarnings("unused")
	private static BufferedImage convertCMYK2RGB(BufferedImage image) throws IOException{
	    //Create a new RGB image
	    BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(),
	    BufferedImage.TYPE_3BYTE_BGR);
	    // then do a funky color convert
	    ColorConvertOp op = new ColorConvertOp(null);
	    op.filter(image, rgbImage);
	    return rgbImage;
	}
	/**
	 * 对一个图像进行旋转
	 * 
	 * @param srcIm
	 *            原图像文件
	 * @param taIm
	 *            转换后的图像文件
	 * @param degree
	 *            旋转角度, 90 为顺时针九十度， -90 为逆时针九十度
	 * @return 旋转后得图像对象
	 */
	public static BufferedImage rotate(Object srcIm, File taIm, int degree) {
		BufferedImage im = read(srcIm);
		BufferedImage im2 = rotate(im, degree);
		write(im2, taIm);
		return im2;
	}

	/**
	 * 对一个图像进行旋转
	 * 
	 * @param srcPath
	 *            原图像文件路径
	 * @param taPath
	 *            转换后的图像文件路径
	 * @param degree
	 *            旋转角度, 90 为顺时针九十度， -90 为逆时针九十度
	 * @return 旋转后得图像对象
	 */
	public static BufferedImage rotate(String srcPath, String taPath, int degree)
			throws IOException {
		File srcIm = FileHelper.findFile(srcPath);
		if (null == srcIm)
			System.out.printf("Fail to find image file '%s'!", srcPath);

		File taIm = FileHelper.createFileIfNoExists(taPath);
		return rotate(srcIm, taIm, degree);
	}

	/**
	 * 对一个图像进行旋转
	 * 
	 * @param image
	 *            图像
	 * @param degree
	 *            旋转角度, 90 为顺时针九十度， -90 为逆时针九十度
	 * @return 旋转后得图像对象
	 */
	public static BufferedImage rotate(BufferedImage image, int degree) {
		int iw = image.getWidth();// 原始图象的宽度
		int ih = image.getHeight();// 原始图象的高度
		int w = 0;
		int h = 0;
		int x = 0;
		int y = 0;
		degree = degree % 360;
		if (degree < 0)
			degree = 360 + degree;// 将角度转换到0-360度之间
		double ang = degree * 0.0174532925;// 将角度转为弧度

		/**
		 * 确定旋转后的图象的高度和宽度
		 */

		if (degree == 180 || degree == 0 || degree == 360) {
			w = iw;
			h = ih;
		} else if (degree == 90 || degree == 270) {
			w = ih;
			h = iw;
		} else {
			int d = iw + ih;
			w = (int) (d * Math.abs(Math.cos(ang)));
			h = (int) (d * Math.abs(Math.sin(ang)));
		}

		x = (w / 2) - (iw / 2);// 确定原点坐标
		y = (h / 2) - (ih / 2);
		BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
		Graphics gs = rotatedImage.getGraphics();
		gs.fillRect(0, 0, w, h);// 以给定颜色绘制旋转后图片的背景
		AffineTransform at = new AffineTransform();
		at.rotate(ang, w / 2, h / 2);// 旋转图象
		at.translate(x, y);
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		op.filter(image, rotatedImage);
		image = rotatedImage;
		return image;
	}
	/**
	 * 高斯模糊加了两个常用的功能,白菜 added 20120.12.01
	 * @param elems
	 * @param origin
	 * @return
	 */
	public BufferedImage convoleOp(float[] elems,BufferedImage origin) {
	    Kernel kernel = new Kernel(3, 3, elems);
	    ConvolveOp op = new ConvolveOp(kernel);
	    BufferedImage filteredImage = new BufferedImage(origin.getWidth(), origin.getHeight(), origin.getType());
	    op.filter(origin, filteredImage);
	    return filteredImage;
	  }
	/**
	 * 锐化
	 * @param origin
	 * @return
	 */
	public BufferedImage sharpen(BufferedImage origin){
	    float[] elements = { 0.0F, -1.0F, 0.0F, -1.0F, 5.0F, -1.0F, 0.0F, -1.0F, 0.0F };
	    return convoleOp(elements,origin);
	  }
	/**
	 * 模糊
	 * @param origin
	 * @return
	 */
	public BufferedImage blur(BufferedImage origin){
		float weight = 0.1111111F;
	    float[] elements = new float[9];
	    for (int i = 0; i < 9; i++) {
	      elements[i] = weight;
	    }
	    return convoleOp(elements,origin);
	  }

    /**
     *  Graphics操作接口
     */
    public static interface GraphicsHandlAble {

        /**
         * Graphics操作
         * @param graphics
         */
        public void handle(Graphics2D graphics);
    }
}
