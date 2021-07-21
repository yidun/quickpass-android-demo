package nis.netease.com.quickpassdemo;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "QuickLogin";
    private static String BUSINESS_ID;
    private static String mSecretKey;
    private static String mSecretId;
    private static String mVerifyUrl, mOnePassUrl;
    private boolean isTest = false;

    private String mMobileNumber;
    private TextView tvMobileNumber;
    private EditText etMobileNumber;
    private Button btnVerify, btnOnePass;
    private QuickLogin login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initArgs();
        login = QuickLogin.getInstance(getApplicationContext(), BUSINESS_ID);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        etMobileNumber = findViewById(R.id.et_mobile_number);
        btnVerify = findViewById(R.id.btn_verify);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMobileNumber = etMobileNumber.getText().toString();
                mobileNumberVerify(mMobileNumber);
            }
        });
        btnOnePass = findViewById(R.id.btn_one_pass);
        getPreMobileNumber();

    }

    private void initArgs() {
        if (isTest) {
            BUSINESS_ID = "xxx"; // 一键登录
            mSecretKey = "xxx";
            mSecretId = "xxx";
            mVerifyUrl = "url";
            mOnePassUrl = "url";
        } else {
            BUSINESS_ID = "xxx"; // 一键登录
            mSecretKey = "xxx";
            mSecretId = "xxx";
            mVerifyUrl = "http://ye.dun.163yun.com/v1/check";
            mOnePassUrl = "http://ye.dun.163yun.com/v1/oneclick/check";
        }
    }

    private void mobileNumberVerify(String mobileNumber) {
        // 本机校验获取token
        login.getToken(mobileNumber, new QuickLoginTokenListener() {
            @Override
            public boolean onExtendMsg(JSONObject extendMsg) {
                Log.d(TAG, "获取的扩展字段内容为:" + extendMsg.toString());
                return super.onExtendMsg(extendMsg);
            }

            @Override
            public void onGetTokenSuccess(final String YDToken, final String accessCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "获取Token成功,yd toke is:" + YDToken + " 运营商token is:" + accessCode);
                        tokenValidate(YDToken, accessCode, false);
                    }
                });
            }

            @Override
            public void onGetTokenError(final String YDToken, final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "获取Token失败" + YDToken + msg, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void getPreMobileNumber() {
        // 预取号与一键登录
        JSONObject extData = new JSONObject();
        try {
            extData.put("parameter1", "param1");
            extData.put("parameter2", "param2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        login.setExtendData(extData);
        login.prefetchMobileNumber(new QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(String YDToken, final String mobileNumber) {
                Log.d(TAG, "[onGetMobileNumberSuccess]callback mobileNumber is:" + mobileNumber);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMobileNumber.setText(mobileNumber);
                        btnOnePass.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                login.onePass(new QuickLoginTokenListener() {
                                    @Override
                                    public void onGetTokenSuccess(final String YDToken, final String accessCode) {
                                        Log.d(TAG, String.format("yd token is:%s accessCode is:%s", YDToken, accessCode));
                                        tokenValidate(YDToken, accessCode, true);
                                    }

                                    @Override
                                    public void onGetTokenError(String YDToken, String msg) {
                                        Log.d(TAG, "获取运营商token失败:" + msg);
                                    }

                                    @Override
                                    public boolean onExtendMsg(JSONObject extendMsg) {
                                        return super.onExtendMsg(extendMsg);
                                    }
                                });

                            }
                        });

                    }
                });
            }

            @Override
            public void onGetMobileNumberError(String YDToken, final String msg) {
                Log.e(TAG, "[onGetMobileNumberError]callback error msg is:" + msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMobileNumber.setText(msg);
                    }
                });
            }

            @Override
            public boolean onExtendMsg(JSONObject extendMsg) {
                Log.d(TAG, "获取的扩展字段内容为:" + extendMsg.toString());
                // 如果接入者自定义了preCheck接口，可在该方法中通过返回true或false来进行控制是否快速降级
                return super.onExtendMsg(extendMsg);
            }
        });


    }

    // token校验，接入者应该将该操作放到自己服务端
    private void tokenValidate(String token, String accessCode, final boolean isOnePass) {

        String nonce = Utils.getRandomString(32);
        String timestamp = String.valueOf(System.currentTimeMillis());
        //生成签名信息
        final HashMap<String, String> map = new HashMap<>();
        map.put("accessToken", accessCode);
        map.put("businessId", BUSINESS_ID);
        map.put("token", token);
        map.put("nonce", nonce);
        map.put("timestamp", timestamp);
        map.put("version", "v1");
        map.put("secretId", mSecretId);
        if (!isOnePass) {
            map.put("phone", mMobileNumber);
        }
        String sign = Utils.generateSign(mSecretKey, map);

        StringBuffer sburl = new StringBuffer();
        if (isOnePass) {
            sburl.append(mOnePassUrl);
        } else {
            sburl.append(mVerifyUrl);
        }
        sburl.append("?accessToken=" + accessCode);
        sburl.append("&businessId=" + BUSINESS_ID);
        sburl.append("&token=" + token);
        sburl.append("&signature=" + sign);
        sburl.append("&nonce=" + nonce);
        sburl.append("&timestamp=" + timestamp);
        sburl.append("&version=" + "v1");
        sburl.append("&secretId=" + mSecretId);
        sburl.append("&phone=" + mMobileNumber);
        final String reqUrl = sburl.toString();
        HttpUtil.doGetRequest(reqUrl, new HttpUtil.ResponseCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e(QuickLogin.TAG, result);
                    JSONObject j = new JSONObject(result);
                    int retCode = j.getInt("code");
                    if (retCode == 200) {
                        if (isOnePass) {
                            String msg = j.getString("msg");
                            JSONObject data = j.getJSONObject("data");
                            String mobileNumber = data.getString("phone");
                            if (!TextUtils.isEmpty(mobileNumber)) {
                                Utils.showToast(LoginActivity.this, "一键登录通过");
                            } else {
                                Utils.showToast(LoginActivity.this, "一键登录不通过" + msg);
                            }
                        } else {
                            JSONObject data = j.getJSONObject("data");
                            int result2 = data.getInt("result");
                            if (result2 == 1) {
                                Utils.showToast(LoginActivity.this, "本机校验通过");
                            } else if (result2 == 2) {
                                Utils.showToast(LoginActivity.this, "本机校验不通过");
                            } else {
                                Utils.showToast(LoginActivity.this, "无法确认校验是否通过");
                            }
                        }

                    } else {
                        String tip = isOnePass ? "一键登录校验token失败：" : "本机校验token失败：";
                        Utils.showToast(LoginActivity.this, tip + j.toString());
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
