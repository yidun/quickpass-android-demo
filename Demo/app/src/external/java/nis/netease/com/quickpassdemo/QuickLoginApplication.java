package nis.netease.com.quickpassdemo;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.netease.nis.quicklogin.QuickLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by hzhuqi on 2019/10/24
 */
public class QuickLoginApplication extends Application {
    public static final String TAG = "QuickLoginDemo";
    private boolean isTest = false;
    public QuickLogin quickLogin;
    public QuickLogin trialQuickLogin;
    /**
     * 一键登录业务id
     */
    private static String onePassId;
    /**
     * 本机校验业务id
     */
    public static String mobileVerifyId;
    /**
     * 试用一键登录业务id
     */
    private static String trialOnePassId;
    /**
     * 试用本机校验id
     */
    public static String trialMobileVerifyId;
    private static String secretKey;
    private static String secretId;
    private static String verifyUrl, onePassUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        initArgs();
        initOnePass();
        initTrialQuickLogin();
    }

    private void initArgs() {
        if (isTest) {
            mobileVerifyId = "xxx";
            onePassId = "xxx";
            secretKey = "xxx";
            secretId = "xxx";
            verifyUrl = "url";
            onePassUrl = "url";
        } else {
            trialMobileVerifyId = "xxx";
            trialOnePassId = "xxx";

            mobileVerifyId = "xxx";
            onePassId = "xxx";
            secretKey = "xxx";
            secretId = "xxx";
            verifyUrl = "http://ye.dun.163yun.com/v1/check";
            onePassUrl = "http://ye.dun.163yun.com/v1/oneclick/check";
        }
    }

    private void initOnePass() {
        quickLogin = QuickLogin.getInstance(getApplicationContext(), onePassId);
        JSONObject extData = new JSONObject();
        try {
            extData.put("parameter1", "param1");
            extData.put("parameter2", "param2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        quickLogin.setUnifyUiConfig(QuickLoginUiConfig.getUiConfig(getApplicationContext()));
        quickLogin.setExtendData(extData);
        quickLogin.setDebugMode(true);
    }

    private void initTrialQuickLogin() {
        trialQuickLogin = QuickLogin.getInstance(getApplicationContext(), trialOnePassId);
        trialQuickLogin.setUnifyUiConfig(QuickLoginUiConfig.getUiConfig(getApplicationContext()));
        trialQuickLogin.setDebugMode(true);
    }

    // 本机校验与一键登录check校验，接入者应该将该操作放到自己服务端，Demo为了完整流程直接写在客户端
    public void tokenValidate(String token, String accessCode, final String mobileNumber, final Activity activity, final boolean isTrialId) {
        boolean isOnePass = true;
        if (!TextUtils.isEmpty(mobileNumber)) {
            isOnePass = false; // 本机校验
        }
        String nonce = Utils.getRandomString(32);
        String timestamp = String.valueOf(System.currentTimeMillis());
        //生成签名信息
        final HashMap<String, String> map = new HashMap<>();
        map.put("accessToken", accessCode);
        if (isOnePass) {
            if (isTrialId) {
                map.put("businessId", trialOnePassId);
            } else {
                map.put("businessId", onePassId);
            }
        } else {
            if (isTrialId) {
                map.put("businessId", trialMobileVerifyId);
            } else {
                map.put("businessId", mobileVerifyId);
            }
        }
        map.put("token", token);
        map.put("nonce", nonce);
        map.put("timestamp", timestamp);
        map.put("version", "v1");
        map.put("secretId", secretId);
        if (!isOnePass) {
            map.put("phone", mobileNumber);
        }
        String sign = Utils.generateSign(secretKey, map);

        StringBuffer sburl = new StringBuffer();
        if (isOnePass) {
            sburl.append(onePassUrl);
        } else {
            sburl.append(verifyUrl);
        }
        sburl.append("?accessToken=" + accessCode);
        if (isOnePass) {
            if (isTrialId) {
                sburl.append("&businessId=" + trialOnePassId);
            } else {
                sburl.append("&businessId=" + onePassId);
            }
        } else {
            if (isTrialId) {
                sburl.append("&businessId=" + trialMobileVerifyId);
            } else {
                sburl.append("&businessId=" + mobileVerifyId);
            }

        }
        sburl.append("&token=" + token);
        sburl.append("&signature=" + sign);
        sburl.append("&nonce=" + nonce);
        sburl.append("&timestamp=" + timestamp);
        sburl.append("&version=" + "v1");
        sburl.append("&secretId=" + secretId);
        sburl.append("&phone=" + mobileNumber);
        final String reqUrl = sburl.toString();
        Log.d(QuickLogin.TAG, "request url: " + reqUrl);
        final boolean finalIsOnePass = isOnePass;
        HttpUtil.doGetRequest(reqUrl, new HttpUtil.ResponseCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e(QuickLogin.TAG, result);
                    JSONObject j = new JSONObject(result);
                    int retCode = j.getInt("code");
                    if (retCode == 200) {
                        if (finalIsOnePass) {
                            String msg = j.getString("msg");
                            JSONObject data = j.getJSONObject("data");
                            String mobileNumber = data.getString("phone");
                            if (!TextUtils.isEmpty(mobileNumber)) {
                                Utils.showToast(activity, "一键登录通过");
                            } else {
                                Utils.showToast(activity, "一键登录不通过");
                            }
                        } else {
                            JSONObject data = j.getJSONObject("data");
                            int result2 = data.getInt("result");
                            if (result2 == 1) {
                                Utils.showToast(activity, "本机校验通过");
                            } else if (result2 == 2) {
                                Utils.showToast(activity, "本机校验不通过");
                            } else {
                                Utils.showToast(activity, "无法确认校验是否通过");
                            }
                        }

                    } else {
                        String tip = finalIsOnePass ? "一键登录校验token失败：" : "本机校验token失败：";
                        Utils.showToast(activity, tip + j.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(QuickLogin.TAG, "error:" + e.toString());
                }
            }

            @Override
            public void onError(String errorCode, String msg) {
                Log.e(QuickLogin.TAG, "校验token出现错误" + msg);
            }
        });
    }
}
