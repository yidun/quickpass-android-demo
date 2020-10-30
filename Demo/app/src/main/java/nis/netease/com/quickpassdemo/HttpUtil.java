package nis.netease.com.quickpassdemo;

import android.net.Network;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by hzhuqi on 2018/4/9.
 */
public class HttpUtil {
    private static final int CONNECT_TIMEOUT_TIME = 10000;
    private static final int READ_TIMEOUT_TIME = 10000;

    private static HttpURLConnection createUrlConnection(String url, String requestType, Network network) {
        try {
            HttpURLConnection httpConn = null;
            final URL serverUrl = new URL(url);
            if (network != null) {
                if (Build.VERSION.SDK_INT > 21) {
                    httpConn = (HttpURLConnection) network.openConnection(serverUrl);
                }
            } else {
                httpConn = (HttpURLConnection) serverUrl.openConnection();
            }
            httpConn.setConnectTimeout(CONNECT_TIMEOUT_TIME);
            httpConn.setReadTimeout(READ_TIMEOUT_TIME);
            httpConn.setRequestMethod(requestType);
            httpConn.setDoInput(true);
            if (requestType.equals("POST")) {
                httpConn.setDoOutput(true);
            } else if (requestType.equals("GET")) {
                httpConn.setDoOutput(false);
            }
            httpConn.setUseCaches(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("connection", "Keep-Alive");
            return httpConn;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void doGetRequest(final String url, final ResponseCallBack responseCallBack) {
        new Thread() {
            @Override
            public void run() {
                //Log.d(QuickPass.TAG, "request url:" + url);
                doGetRequest2(url, responseCallBack, null);
            }
        }.start();
    }


    private static void doGetRequest2(String url, ResponseCallBack responseCallBack, Network network) {
        HttpURLConnection httpConn = createUrlConnection(url, "GET", network);
        if (httpConn != null) {
            try {
                httpConn.connect();
                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    String line = null;
                    StringBuffer stringBuffer = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        line = new String(line.getBytes("UTF-8"));
                        stringBuffer.append(line);
                    }
                    String result = stringBuffer.toString();
                    bufferedReader.close();
                    responseCallBack.onSuccess(result);
                } else {
                    responseCallBack.onError("10000", "与服务端通信失败");
                }
                httpConn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                responseCallBack.onError("10001", "网络请求出现异常" + e.toString());
            }
        } else {
            responseCallBack.onError("10002", "与服务端网络建立连接失败");

        }
    }

    /**
     * 进行网络请求的回调
     */
    public interface ResponseCallBack {
        void onSuccess(String result);

        void onError(String errorCode, String msg);
    }
}
