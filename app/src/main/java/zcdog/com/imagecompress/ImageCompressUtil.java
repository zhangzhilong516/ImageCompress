package zcdog.com.imagecompress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: zhangzhilong
 * @date: 2019/2/15
 * @des:
 */
public class ImageCompressUtil {


    public static void compress(final String filePath, final CompressListener listener) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.onSuccess(compress(filePath,filePath));
                } catch (IOException e) {
                    listener.onError(e);
                }
            }
        });
    }

    public static void compress(final String filePath, final String desPath, final CompressListener listener) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.onSuccess(compress(filePath,desPath));
                } catch (IOException e) {
                    listener.onError(e);
                }
            }
        });
    }

    public static File compress(String filePath,String desPath) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //为True时，不会真正加载图片，而是得到图片的宽高信息
        options.inSampleSize = 1;

        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = computeSize(options);
        options.inJustDecodeBounds = false;

        Bitmap tagBitmap = BitmapFactory.decodeFile(filePath, options);

        rotatingImage(tagBitmap, readPictureDegree(filePath));  // 旋转图片

        //字节数组输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        tagBitmap.compress(isJPG(filePath) ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG, 60, outputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(desPath);
        fileOutputStream.write(outputStream.toByteArray());
        fileOutputStream.flush();
        fileOutputStream.close();
        outputStream.close();
        return new File(filePath);
    }

    public static boolean isJPG(String filePath) {
        return filePath.endsWith(".jpg") || filePath.endsWith(".jpeg");
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


    private static Bitmap rotatingImage(Bitmap bitmap, int degree) {
        if (degree == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 读取图片属性：旋转的角度
     */

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    interface CompressListener {
        void onSuccess(File file);

        void onError(IOException e);
    }
}
