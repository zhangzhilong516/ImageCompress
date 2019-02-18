package zcdog.com.imagecompress;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_20190218_163524.jpg");

        Bitmap bitmap = NativeJpegUtil.decodeFile(file.getAbsolutePath());
        File nativeZipFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Native_"+file.getName());
        NativeJpegUtil.compressBitmap(bitmap,30,nativeZipFile.getAbsolutePath());
        Log.d("TAG","Native size ==" + nativeZipFile.length());

        File compressZipFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Compress_"+file.getName());
        ImageCompressUtil.compress(file.getAbsolutePath(),compressZipFile.getAbsolutePath(), new ImageCompressUtil.CompressListener() {
            @Override
            public void onSuccess(File file) {
                Log.i("TAG","length ==" + file.length());
            }
            @Override
            public void onError(IOException e) {
                e.printStackTrace();
            }
        });
    }
}
