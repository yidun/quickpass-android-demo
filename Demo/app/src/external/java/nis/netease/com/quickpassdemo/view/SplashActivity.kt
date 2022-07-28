package nis.netease.com.quickpassdemo.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.netease.nis.quicklogin.QuickLogin
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener
import kotlinx.android.synthetic.external.activity_splash.*
import nis.netease.com.quickpassdemo.MyApplication
import nis.netease.com.quickpassdemo.R
import nis.netease.com.quickpassdemo.permissionx.PermissionX
import nis.netease.com.quickpassdemo.tools.showToast

/**
 * @author liuxiaoshuai
 * @date 2022/3/18
 * @desc
 * @email liulingfeng@mistong.com
 */
class SplashActivity : BaseActivity() {
    private var mCountDownTimer: MyCountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mCountDownTimer = MyCountDownTimer(3000, 1000, object : CountDownListener {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                login_demo_countdown_tv?.text = "${millisUntilFinished / 1000}s跳过"
            }

            override fun onFinish() {
                gotoSelect()
            }

        })
        setListeners()
        requestPermission()
    }

    private fun setListeners() {
        login_demo_countdown_tv?.setOnClickListener {
            gotoSelect()
        }
    }

    private fun requestPermission() {
        if (PermissionX.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {
            mCountDownTimer?.start()
            // 预取号
            preLogin()
        } else {
            PermissionX.request(this, Manifest.permission.READ_PHONE_STATE) { allGranted, _ ->
                mCountDownTimer?.start()
                preLogin()
            }
        }
    }

    private fun preLogin() {
        QuickLogin.getInstance().prefetchMobileNumber(object : QuickLoginPreMobileListener() {
            override fun onGetMobileNumberSuccess(token: String?, mobileNumber: String?) {
                Log.d("预取号成功", "易盾token${token}掩码${mobileNumber}")
            }

            override fun onGetMobileNumberError(token: String?, msg: String?) {
                msg?.showToast(this@SplashActivity)
                Log.e("预取号失败", "易盾token${token}错误信息${msg}")
            }

        })
    }

    private fun gotoSelect() {
        startActivity(Intent(this, SelectorActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        mCountDownTimer?.cancel()
        super.onDestroy()
    }

    private class MyCountDownTimer constructor(
        millisInFuture: Long,
        countDownInterval: Long,
        val countDownListener: CountDownListener
    ) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            countDownListener.onTick(millisUntilFinished)
        }

        override fun onFinish() {
            countDownListener.onFinish()
        }

    }

    interface CountDownListener {
        fun onTick(millisUntilFinished: Long)
        fun onFinish()
    }
}