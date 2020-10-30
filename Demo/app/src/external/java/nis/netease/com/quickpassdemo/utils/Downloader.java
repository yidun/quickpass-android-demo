package nis.netease.com.quickpassdemo.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hzhuqi on 2020/1/2
 */
public class Downloader {
    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static void download(String url, final String dstPath, final DownloadListener listener) {
        final Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailed(-1, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    listener.onFailed(response.code(), response.toString());
                    return;
                }
                InputStream is = response.body().byteStream();
                FileOutputStream fos = new FileOutputStream(dstPath);
                byte[] buffer = new byte[1024 * 4];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                listener.onSuccess();
            }
        });
    }

    interface DownloadListener {
        void onSuccess();

        void onFailed(int code, String msg);
    }
}
