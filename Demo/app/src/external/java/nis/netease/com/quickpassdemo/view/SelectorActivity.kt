package nis.netease.com.quickpassdemo.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.netease.nis.quicklogin.QuickLogin
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener
import nis.netease.com.quickpassdemo.databinding.ActivitySelectBinding
import nis.netease.com.quickpassdemo.tools.UiConfigs
import nis.netease.com.quickpassdemo.tools.showToast

/**
 * @author liuxiaoshuai
 * @date 2022/3/18
 * @desc
 * @email liulingfeng@mistong.com
 */
class SelectorActivity : BaseActivity() {
    private var binding: ActivitySelectBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBinding.inflate(layoutInflater).apply { setContentView(root) }

        initListeners()
    }

    private fun initListeners() {
        binding?.demoSeletorA?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getAConfig(this))
            openAuth()
        }
        binding?.demoSeletorB?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getBConfig(this))
            openAuth()
        }
        binding?.demoSeletorC?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getCConfig(
                this
            ) { _, _ -> QuickLogin.getInstance().quitActivity() })
            openAuth()
        }
        binding?.demoSeletorD?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getDConfig(this))
            openAuth()
        }
        binding?.demoSeletorE?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getEConfig(this))
            openAuth()
        }

        binding?.demoSeletorF?.setOnClickListener {
            QuickLogin.getInstance().setUnifyUiConfig(UiConfigs.getFConfig(
                this
            ) { _, _ -> "编辑".showToast(this) })
            openAuth()
        }

        binding?.demoBenji?.setOnClickListener {
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