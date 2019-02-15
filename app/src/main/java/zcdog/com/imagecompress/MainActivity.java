package zcdog.com.imagecompress;

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
        ;
        ImageCompressUtil.compress(Environment.getExternalStorageDirectory() + "/IMG_20190215_185635.jpg", new ImageCompressUtil.CompressListener() {
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
