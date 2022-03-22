package nis.netease.com.quickpassdemo.view

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.netease.nis.quicklogin.QuickLogin
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener
import kotlinx.android.synthetic.external.activity_benji.*
import nis.netease.com.quickpassdemo.MyApplication
import nis.netease.com.quickpassdemo.R
import nis.netease.com.quickpassdemo.tools.showToast

/**
 * @author liuxiaoshuai
 * @date 2022/3/21
 * @desc
 * @email liulingfeng@mistong.com
 */
class BenjiActivity : AppCompatActivity() {
    private var quickLogin: QuickLogin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_benji)

        quickLogin = (application as MyApplication).quickLogin
        btn_benji.setOnClickListener {
            if (TextUtils.isEmpty(et_number.text)) {
                "请输入正确的手机号".showToast(this)
            } else {
                quickLogin?.getToken(et_number.text.toString(), object : QuickLoginTokenListener() {
                    override fun onGetTokenSuccess(token: String?, accessCode: String?) {
                        Log.d("获取授权码成功", "易盾token${token}运营商token${accessCode}")
                        // 服务端二次校验
                    }

                    override fun onGetTokenError(token: String?, msg: String?) {
                        Log.e("获取授权码失败", "易盾token${token}错误信息${msg}")
                    }

                })
            }
        }

    }
}