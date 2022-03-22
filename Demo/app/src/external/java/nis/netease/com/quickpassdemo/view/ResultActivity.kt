package nis.netease.com.quickpassdemo.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.netease.nis.basesdk.EncryptUtil.getRandomString
import com.netease.nis.basesdk.HttpUtil
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener
import kotlinx.android.synthetic.external.activity_result.*
import nis.netease.com.quickpassdemo.MyApplication
import nis.netease.com.quickpassdemo.R
import nis.netease.com.quickpassdemo.tools.generateSign
import nis.netease.com.quickpassdemo.tools.showToast
import org.json.JSONObject
import java.util.HashMap

/**
 * @author liuxiaoshuai
 * @date 2022/3/21
 * @desc
 * @email liulingfeng@mistong.com
 */
class ResultActivity : AppCompatActivity() {
    private val businessId = "xxx"
    private val secretId = "xxx"
    private val secretKey = "xxx"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        initData()
        btn_retry.setOnClickListener {
            preLogin()
        }
    }

    private fun initData() {
        val token = intent.getStringExtra("token")
        val accessToken = intent.getStringExtra("accessToken")
        validate(token, accessToken)
    }

    // 建议放在服务端交互
    private fun validate(token: String?, accessCode: String?) {
        val nonce = getRandomString(32)
        val timestamp = System.currentTimeMillis().toString()
        //生成签名信息
        val map = HashMap<String, String>()
        map["accessToken"] = accessCode ?: ""
        map["businessId"] = businessId
        map["token"] = token ?: ""
        map["nonce"] = nonce
        map["timestamp"] = timestamp
        map["version"] = "v1"
        map["secretId"] = secretId
        val sign = generateSign(secretKey, map)
        val sbUrl = StringBuffer()
        sbUrl.append("http://ye.dun.163yun.com/v1/oneclick/check")
        sbUrl.append("?accessToken=$accessCode")
        sbUrl.append("&businessId=${businessId}")
        sbUrl.append("&token=$token")
        sbUrl.append("&signature=$sign")
        sbUrl.append("&nonce=$nonce")
        sbUrl.append("&timestamp=$timestamp")
        sbUrl.append("&version=v1")
        sbUrl.append("&secretId=${secretId}")
        Log.d("ResultActivity", "request url: $sbUrl")
        HttpUtil.doGetRequestByForm(sbUrl.toString(), object : HttpUtil.ResponseCallBack {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(result: String?) {
                Log.d("ResultActivity", "结果${result}")
                result?.let {
                    val json = JSONObject(result)
                    val code = json.getInt("code")
                    if (code == 200) {
                        val data = json.getJSONObject("data")
                        val mobileNumber = data.getString("phone")
                        if (!TextUtils.isEmpty(mobileNumber)) {
                            tv_result.text = mobileNumber
                            "一键登录通过".showToast(this@ResultActivity)
                        } else {
                            val msg = json.getString("msg")
                            tv_result.text = "错误码${code}错误信息${msg}"
                            Log.e("ResultActivity", "错误码${code}错误信息${msg}")
                        }
                    } else {
                        val msg = json.getString("msg")
                        Log.e("ResultActivity", "错误码${code}错误信息${msg}")
                    }
                }
            }

            override fun onError(code: Int, msg: String?) {
                Log.e("ResultActivity", "错误码${code}错误信息${msg}")
            }

        })
    }

    private fun preLogin() {
        val quickLogin = (application as? MyApplication)?.quickLogin
        quickLogin?.prefetchMobileNumber(object : QuickLoginPreMobileListener() {
            override fun onGetMobileNumberSuccess(token: String?, mobileNumber: String?) {
                Log.d("预取号成功", "易盾token${token}掩码${mobileNumber}")
            }

            override fun onGetMobileNumberError(token: String?, msg: String?) {
                msg?.showToast(this@ResultActivity)
                Log.e("预取号失败", "易盾token${token}错误信息${msg}")
            }

        })
    }
}