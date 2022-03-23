package nis.netease.com.quickpassdemo.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.netease.nis.quicklogin.QuickLogin
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener
import kotlinx.android.synthetic.external.activity_select.*
import nis.netease.com.quickpassdemo.MyApplication
import nis.netease.com.quickpassdemo.R
import nis.netease.com.quickpassdemo.tools.UiConfigs
import nis.netease.com.quickpassdemo.tools.showToast

/**
 * @author liuxiaoshuai
 * @date 2022/3/18
 * @desc
 * @email liulingfeng@mistong.com
 */
class SelectorActivity : BaseActivity() {
    private var quickLogin: QuickLogin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        quickLogin = (application as MyApplication).quickLogin
        initListeners()
    }

    private fun initListeners() {
        demo_seletor_A?.setOnClickListener {
            quickLogin?.setUnifyUiConfig(UiConfigs.getAConfig(this))
            openAuth()
        }
        demo_seletor_B?.setOnClickListener {
            quickLogin?.setUnifyUiConfig(UiConfigs.getBConfig(this))
            openAuth()
        }
        demo_seletor_C?.setOnClickListener {
            quickLogin?.setUnifyUiConfig(UiConfigs.getCConfig(
                this
            ) { _, _ -> quickLogin?.quitActivity() })
            openAuth()
        }
        demo_seletor_D?.setOnClickListener {
            quickLogin?.setUnifyUiConfig(UiConfigs.getDConfig(this))
            openAuth()
        }
        demo_seletor_E?.setOnClickListener {
            quickLogin?.setUnifyUiConfig(UiConfigs.getEConfig(this))
            openAuth()
        }

        demo_seletor_F?.setOnClickListener {
            quickLogin?.setUnifyUiConfig(UiConfigs.getFConfig(
                this
            ) { _, _ -> "编辑".showToast(this) })
            openAuth()
        }

        demo_benji?.setOnClickListener {
            startActivity(Intent(this, BenjiActivity::class.java))
        }
    }

    private fun openAuth() {
        quickLogin?.onePass(object : QuickLoginTokenListener() {
            override fun onGetTokenSuccess(token: String?, accessCode: String?) {
                quickLogin?.quitActivity()
                Log.d("SelectorActivity", "易盾token${token}运营商token${accessCode}")
                token?.let {
                    accessCode?.let { accessCode ->
                        startResultActivity(it, accessCode, "")
                    }
                }
            }

            override fun onGetTokenError(token: String?, msg: String?) {
                quickLogin?.quitActivity()
                msg?.showToast(this@SelectorActivity)
                Log.e("SelectorActivity", "易盾token${token}错误信息${msg}")
            }

            // 取消登录包括按物理返回键返回
            override fun onCancelGetToken() {
                quickLogin?.quitActivity()
                Log.d("SelectorActivity", "用户取消登录")
            }
        })
    }
}