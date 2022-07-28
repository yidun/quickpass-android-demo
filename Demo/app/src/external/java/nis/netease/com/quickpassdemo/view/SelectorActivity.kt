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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        initListeners()
    }

    private fun initListeners() {
        demo_seletor_A?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getAConfig(this))
            openAuth()
        }
        demo_seletor_B?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getBConfig(this))
            openAuth()
        }
        demo_seletor_C?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getCConfig(
                this
            ) { _, _ -> QuickLogin.getInstance().quitActivity() })
            openAuth()
        }
        demo_seletor_D?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getDConfig(this))
            openAuth()
        }
        demo_seletor_E?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getEConfig(this))
            openAuth()
        }

        demo_seletor_F?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getFConfig(
                this
            ) { _, _ -> "编辑".showToast(this) })
            openAuth()
        }

        demo_benji?.setOnClickListener {
            startActivity(Intent(this, BenjiActivity::class.java))
        }
    }

    private fun openAuth() {
        QuickLogin.getInstance().onePass(object : QuickLoginTokenListener() {
            override fun onGetTokenSuccess(token: String?, accessCode: String?) {
                QuickLogin.getInstance().quitActivity()
                Log.d("SelectorActivity", "易盾token${token}运营商token${accessCode}")
                token?.let {
                    accessCode?.let { accessCode ->
                        startResultActivity(it, accessCode, "")
                    }
                }
            }

            override fun onGetTokenError(token: String?, msg: String?) {
                QuickLogin.getInstance().quitActivity()
                QuickLogin.getInstance().clearScripCache(this@SelectorActivity)
                msg?.showToast(this@SelectorActivity)
                Log.e("SelectorActivity", "易盾token${token}错误信息${msg}")
            }

            // 取消登录包括按物理返回键返回
            override fun onCancelGetToken() {
                QuickLogin.getInstance().quitActivity()
                Log.d("SelectorActivity", "用户取消登录/包括物理返回")
            }
        })
    }
}