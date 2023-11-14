package nis.netease.com.quickpassdemo

import android.app.Application
import com.netease.nis.quicklogin.QuickLogin

/**
 * @author liuxiaoshuai
 * @date 2022/3/18
 * @desc
 * @email liulingfeng@mistong.com
 */
class MyApplication : Application() {
    /**
     * 一键登录业务id
     */
    private val onePassId: String = "b55f3c7d4729455c9c3fb23872065401"

    /**
     * 本机校验业务id
     */
    private val mobileVerifyId: String = "xxx"

    override fun onCreate() {
        super.onCreate()

        QuickLogin.getInstance().init(this, onePassId)
        QuickLogin.getInstance().setDebugMode(true)
    }
}