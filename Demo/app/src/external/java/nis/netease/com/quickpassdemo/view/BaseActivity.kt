package nis.netease.com.quickpassdemo.view

import androidx.fragment.app.FragmentActivity

/**
 * @author liuxiaoshuai
 * @date 2022/3/18
 * @desc
 * @email liulingfeng@mistong.com
 */
open class BaseActivity : FragmentActivity() {
    fun startResultActivity(token: String, accessToken: String, mobileNumber: String) {
        nis.netease.com.quickpassdemo.tools.startActivity<ResultActivity>(this) {
            putExtra("token", token)
            putExtra("accessToken", accessToken)
            putExtra("mobileNumber", mobileNumber)
        }

    }
}