package nis.netease.com.quickpassdemo.view

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.netease.nis.quicklogin.QuickLogin
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener
import nis.netease.com.quickpassdemo.R
import nis.netease.com.quickpassdemo.databinding.ActivityBenjiBinding
import nis.netease.com.quickpassdemo.tools.showToast

/**
 * @author liuxiaoshuai
 * @date 2022/3/21
 * @desc
 * @email liulingfeng@mistong.com
 */
class BenjiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBenjiBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        binding.btnBenji.setOnClickListener {
            if (TextUtils.isEmpty(binding.etNumber.text)) {
                "请输入正确的手机号".showToast(this)
            } else {
                QuickLogin.getInstance()
                    .getToken(binding.etNumber.text.toString(), object : QuickLoginTokenListener {
                        override fun onGetTokenSuccess(token: String?, accessCode: String?) {
                            Log.d("获取授权码成功", "易盾token${token}运营商token${accessCode}")
                            // 服务端二次校验
                        }

                        override fun onGetTokenError(token: String?, code: Int, msg: String?) {
                            Log.e("获取授权码失败", "易盾token${token}错误信息${msg}")
                        }

                    })
            }
        }

    }
}