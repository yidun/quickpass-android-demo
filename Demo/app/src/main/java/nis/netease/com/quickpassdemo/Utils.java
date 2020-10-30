package nis.netease.com.quickpassdemo;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import org.apache.commons.mycodec.digest.DigestUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by hzhuqi on 2019/1/29.
 */
public class Utils {

    /**
     * @param secretKey
     * @param params
     * @return
     */
    public static String generateSign(String secretKey, Map<String, String> params) {
        String signature = "";
        if (TextUtils.isEmpty(secretKey) || params.size() == 0) {
            return signature;
        }
        try {
            // 1. 参数名按照ASCII码表升序排序
            String[] keys = params.keySet().toArray(new String[0]);
            Arrays.sort(keys);
            // 2. 按照排序拼接参数名与参数值
            StringBuilder paramBuffer = new StringBuilder();
            for (String key : keys) {
                paramBuffer.append(key).append(params.get(key) == null ? "" : params.get(key));
            }
            // 3. 将secretKey拼接到最后
            paramBuffer.append(secretKey);
            // 4. MD5是128位长度的摘要算法，用16进制表示，一个十六进制的字符能表示4个位，所以签名后的字符串长度固定为32个十六进制字符。
            signature = DigestUtils.md5Hex(paramBuffer.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    public static void showToast(final Activity act, final String tip) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act.getApplicationContext(), tip, Toast.LENGTH_LONG).show();
            }
        });

    }

    public static String getRandomString(int length) {
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }

    /**
     * 将dp值转换为px值
     *
     * @param dipValue dp值
     * @return px
     */
    public static int dip2px(Context context, float dipValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        } catch (Exception e) {
            return (int) dipValue;
        }
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    public static int getScreenPxWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenDpWidth(Context context) {
        int pxWidth = getScreenPxWidth(context);
        return (int) px2dip(context, pxWidth);
    }

    public static int getScreenPxHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getScreenDpHeight(Context context) {
        int pxHeight = getScreenPxHeight(context);
        return (int) px2dip(context, pxHeight);
    }
}
