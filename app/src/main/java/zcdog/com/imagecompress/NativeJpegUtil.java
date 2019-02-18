package zcdog.com.imagecompress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author: zhangzhilong
 * @date: 2019/2/18
 * @des:
 */
public class NativeJpegUtil {
    static {
        System.loadLibrary("jpeg");
        System.loadLibrary("compressimg");
    }

    /**
     * NDK方法加载图片
     * @param bitmap 图片bitmap
     * @param quality 压缩的质量
     * @param fileName 压缩后的路径
     * @return
     */
    public native static int compressBitmap(Bitmap bitmap, int quality,
                                            String fileName);


    public static Bitmap decodeFile(String path) {
        int finalWidth = 800;

        // 先获取宽度
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 不加载图片到内存只拿宽高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        int bitmapWidth = options.outWidth;

        int inSampleSize = 1;

        if(bitmapWidth>finalWidth){
            inSampleSize = bitmapWidth/finalWidth;
        }

        options.inSampleSize = computeSize(options);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path,options);
    }

    // 开源项目 鲁班Luban 的算法
    private static int computeSize(BitmapFactory.Options options) {
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }
}
