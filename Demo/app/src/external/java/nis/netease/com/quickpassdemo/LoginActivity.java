package nis.netease.com.quickpassdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.helper.UnifyUiConfig;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import org.json.JSONObject;

import nis.netease.com.quickpassdemo.utils.JsonConfigParser;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "QuickLogin";
    private QuickLoginApplication app;
    private QuickLogin loginHelper;
    private TextView tvMobileNumber;
    private EditText etMobileNumber;
    private Button btnVerify, btnPrefetchNumber, btnOnePass;
    private Switch swDialogMode, swPullUiConfig, swOnTrialKey;
    private boolean isHadPrefetchNumber = false;
    /**
     * 是否试用业务id，对客户端无影响，仅供QA测试后端试用业务分支代码
     */
    private boolean isOnTrialId = false;
    /**
     * 从服务端动态拉取UI配置，仅供QA测试时自动化操作
     */
    private boolean isOpenDynamicPullUiConfig = false;
    private JsonConfigParser configParser = new JsonConfigParser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initData();
    }


    private void initViews() {
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        etMobileNumber = findViewById(R.id.et_mobile_number);
        btnVerify = findViewById(R.id.btn_verify);
        btnPrefetchNumber = findViewById(R.id.btn_prefetch_number);
        btnOnePass = findViewById(R.id.btn_one_pass);
        swDialogMode = findViewById(R.id.switch_dialog);
        swPullUiConfig = findViewById(R.id.switch_dynamic_pull_config);
        swOnTrialKey = findViewById(R.id.switch_on_trial_key);
    }

    private void initData() {
        app = (QuickLoginApplication) getApplication();
        loginHelper = app.quickLogin;
        // 本机校验
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNumberVerify(etMobileNumber.getText().toString());
            }
        });
        // 一键登登录
        btnPrefetchNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefetchNumber();
                isHadPrefetchNumber = true;
            }
        });
        btnOnePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isHadPrefetchNumber) {
                    Utils.showToast(LoginActivity.this, "一键登录前请先调用预取号接口");
                } else {
                    doOnePass();
                    isHadPrefetchNumber = false;
                }
            }
        });
        swDialogMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginHelper.setUnifyUiConfig(QuickLoginUiConfig.getDialogUiConfig(LoginActivity.this));
                } else {
                    loginHelper.setUnifyUiConfig(QuickLoginUiConfig.getUiConfig(getApplicationContext()));
                }
            }
        });
        swPullUiConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isOpenDynamicPullUiConfig = true;
                } else {
                    isOpenDynamicPullUiConfig = false;
                }
            }
        });
        swOnTrialKey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isOnTrialId = true;
                    loginHelper = app.trialQuickLogin;
                } else {
                    isOnTrialId = false;
                    loginHelper = app.quickLogin;
                }
            }
        });
    }

    // ------------------------------------------本机校验------------------------------------------
    private void mobileNumberVerify(final String mobileNumber) {
        QuickLogin.getInstance(getApplicationContext(), QuickLoginApplication.mobileVerifyId)
                .getToken(mobileNumber, new QuickLoginTokenListener() {
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
                                app.tokenValidate(YDToken, accessCode, mobileNumber, LoginActivity.this, isOnTrialId);
                            }
                        });
                    }

                    @Override
                    public void onGetTokenError(final String YDToken, final String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "获取Token失败,yd toke is:" + YDToken + " msg is:" + msg);
                                Toast.makeText(getApplicationContext(), "获取Token失败,yd toke is:" + YDToken + " msg is:" + msg, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
    }

    // ------------------------------------------一键登录------------------------------------------
    private void prefetchNumber() {
        // 预取号与一键登录
        loginHelper.prefetchMobileNumber(new QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(String YDToken, final String mobileNumber) {
                Log.d(TAG, "[onGetMobileNumberSuccess]callback mobileNumber is:" + mobileNumber);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(LoginActivity.this, "预取号成功,可一键登录!");
                        tvMobileNumber.setText(mobileNumber);
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
        });
    }

    private void doOnePass() {
        loginHelper.onePass(new QuickLoginTokenListener() {
            @Override
            public void onGetTokenSuccess(final String YDToken, final String accessCode) {
                Log.d(TAG, String.format("yd token is:%s accessCode is:%s", YDToken, accessCode));
                app.tokenValidate(YDToken, accessCode, null, LoginActivity.this, isOnTrialId);
            }

            @Override
            public void onGetTokenError(String YDToken, String msg) {
                Utils.showToast(LoginActivity.this, "获取运营商token失败:" + msg);
                Log.d(TAG, "获取运营商token失败:" + msg);
            }

            @Override
            public boolean onExtendMsg(JSONObject extendMsg) {
                return super.onExtendMsg(extendMsg);
            }

            @Override
            public void onCancelGetToken() {
                Log.d(TAG, "用户取消登录");
                Utils.showToast(LoginActivity.this, "用户取消登录");
            }
        });
    }

    public void freshUiConfig(View view) {
        if (isOpenDynamicPullUiConfig) {
            UnifyUiConfig uiConfig = configParser.getUiConfig(this.getApplicationContext());
            loginHelper.setUnifyUiConfig(uiConfig);
        }
    }

    public void reset(View view) {
        if (swDialogMode.isChecked()) {
            loginHelper.setUnifyUiConfig(QuickLoginUiConfig.getDialogUiConfig(LoginActivity.this));
        } else {
            loginHelper.setUnifyUiConfig(QuickLoginUiConfig.getUiConfig(getApplicationContext()));
        }
    }
}
