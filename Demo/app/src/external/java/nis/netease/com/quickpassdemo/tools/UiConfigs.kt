package nis.netease.com.quickpassdemo.tools

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.netease.nis.quicklogin.helper.UnifyUiConfig
import com.netease.nis.quicklogin.listener.LoginListener
import com.netease.nis.quicklogin.utils.LoginUiHelper
import nis.netease.com.quickpassdemo.R

/**
 * @author liuxiaoshuai
 * @date 2022/3/18
 * @desc
 * @email liulingfeng@mistong.com
 */
object UiConfigs {
    fun getAConfig(context: Context): UnifyUiConfig {
        return UnifyUiConfig.Builder()
            .setStatusBarColor(Color.parseColor("#ffffff")) // 状态栏颜色
            .setStatusBarDarkColor(true) // 状态栏字体图标颜色是否为暗色
            .setLogoIconName("ico_logo") // 设置应用 logo 图标
            .setLogoWidth(200) // 设置应用logo宽度
            .setLogoHeight(70) // 设置应用 logo 高度
            .setLogoTopYOffset(90) // 设置 logo 顶部 Y 轴偏移
            .setMaskNumberColor(Color.BLACK) // 设置手机掩码颜色
            .setMaskNumberSize(25) // 设置手机掩码字体大小
            .setMaskNumberTypeface(Typeface.SERIF) // 设置手机掩码字体
            .setMaskNumberTopYOffset(190) // 设置手机掩码顶部Y轴偏移
            .setSloganSize(13) // 设置认证品牌字体大小
            .setSloganColor(Color.parseColor("#9A9A9A")) // 设置认证品牌颜色
            .setSloganTopYOffset(240) // 设置认证品牌顶部 Y 轴偏移
            .setLoginBtnText("易盾一键登录") // 设置登录按钮文本
            .setLoginBtnTextColor(Color.WHITE) // 设置登录按钮文本颜色
            .setLoginBtnBackgroundRes("login_demo_auth_bt") // 设置登录按钮背景资源
            .setLoginBtnWidth(240) // 设置登录按钮宽度
            .setLoginBtnHeight(45) // 设置登录按钮高度
            .setLoginBtnTextSize(15) // 设置登录按钮文本字体大小
            .setLoginBtnTopYOffset(280) // 设置登录按钮顶部Y轴偏移
            .setPrivacyTextStart("我已阅读并同意") // 设置隐私栏声明部分起始文案
            .setProtocolText("用户协议") // 设置隐私栏协议文本
            .setProtocolLink("https://www.baidu.com") // 设置隐私栏协议链接
            .setPrivacyTextEnd("") // 设置隐私栏声明部分尾部文案
            .setPrivacyTextColor(Color.parseColor("#292929")) // 设置隐私栏文本颜色，不包括协议
            .setPrivacyProtocolColor(Color.parseColor("#3F51B5")) // 设置隐私栏协议颜色
            .setPrivacySize(13) // 设置隐私栏区域字体大小
            .setPrivacyBottomYOffset(24) // 设置隐私栏距离屏幕底部偏移
            .setPrivacyMarginLeft(40) // 设置隐私栏水平方向的偏移
            .setPrivacyMarginRight(40) // 设置隐私栏右侧边距
            .setPrivacyTextMarginLeft(8) // 设置隐私栏复选框和文字内边距
            .setCheckBoxGravity(Gravity.TOP) // 设置隐私栏勾选框与文本协议对齐方式
            .setPrivacyTextGravityCenter(true) // 设置隐私栏文案换行后是否居中对齐
            .setPrivacyTextLayoutGravity(Gravity.CENTER) // 设置隐私栏文案与勾选框对齐方式
            .setPrivacyCheckBoxWidth(20) // 设置隐私栏复选框宽度
            .setPrivacyCheckBoxHeight(20) // 设置隐私栏复选框高度
            .setHidePrivacySmh(true) // 是否隐藏书名号
            .setCheckedImageName("login_demo_check_cus") // 设置隐私栏复选框选中时的图片资源
            .setUnCheckedImageName("login_demo_uncheck_cus") // 设置隐私栏复选框未选中时的图片资源
            .setProtocolPageNavTitle("移动服务及隐私协议", "联通服务及隐私协议", "电信服务及隐私协议")// 设置协议详细页标题
            .setProtocolPageNavColor(Color.parseColor("#FFFFFF"))// 设置协议详细页导航栏标题颜色
            .setLoginListener(object : LoginListener() {
                override fun onDisagreePrivacy(privacyTv: TextView?, btnLogin: Button?): Boolean {
                    privacyTv?.let {
                        val animator =
                            ObjectAnimator.ofFloat(privacyTv, "translationX", 0f, 40f, -40f, 0f)
                        animator.duration = 300
                        animator.start()
                    }
                    // 返回true自定义处理协议未勾选点击登录（默认false/弹窗）
                    return true
                }
            })
            .build(context)
    }

    @SuppressLint("InflateParams")
    fun getBConfig(context: Context): UnifyUiConfig {
        val inflater = LayoutInflater.from(context)
        val otherLoginRel =
            inflater.inflate(R.layout.custom_other_login, null) as RelativeLayout
        val layoutParamsOther = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParamsOther.setMargins(
            0,
            0,
            0,
            130f.dip2px(context)
        )
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL)
        layoutParamsOther.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        otherLoginRel.layoutParams = layoutParamsOther
        val wx =
            otherLoginRel.findViewById<ImageView>(R.id.weixin)
        val qq =
            otherLoginRel.findViewById<ImageView>(R.id.qq)
        val wb =
            otherLoginRel.findViewById<ImageView>(R.id.weibo)
        wx.setOnClickListener { "微信登录".showToast(context) }
        qq.setOnClickListener { "qq登录".showToast(context) }
        wb.setOnClickListener { "微博登录".showToast(context) }
        return UnifyUiConfig.Builder()
            .setStatusBarColor(Color.parseColor("#ffffff"))
            .setStatusBarDarkColor(true)
            .setLogoIconName("ico_logo")
            .setLogoWidth(200)
            .setLogoHeight(70)
            .setLogoTopYOffset(90)
            .setMaskNumberColor(Color.BLACK)
            .setMaskNumberSize(25)
            .setMaskNumberTypeface(Typeface.SERIF)
            .setMaskNumberTopYOffset(190)
            .setSloganSize(13)
            .setSloganColor(Color.parseColor("#9A9A9A"))
            .setSloganTopYOffset(240)
            .setLoginBtnText("易盾一键登录")
            .setLoginBtnTextColor(Color.WHITE)
            .setLoginBtnBackgroundRes("login_demo_auth_bt")
            .setLoginBtnWidth(240)
            .setLoginBtnHeight(45)
            .setLoginBtnTextSize(15)
            .setLoginBtnTopYOffset(280)
            .setPrivacyTextStart("我已阅读并同意")
            .setProtocolText("用户协议")
            .setProtocolLink("https://www.baidu.com")
            .setPrivacyTextEnd("")
            .setPrivacyTextColor(Color.parseColor("#292929"))
            .setPrivacyProtocolColor(Color.parseColor("#3F51B5"))
            .setPrivacySize(13)
            .setPrivacyTopYOffset(320)
            .setPrivacyMarginLeft(40)
            .setPrivacyMarginRight(40)
            .setPrivacyTextMarginLeft(8)
            .setPrivacyTextGravityCenter(true)
            .setPrivacyTextLayoutGravity(Gravity.CENTER)
            .setPrivacyCheckBoxWidth(20)
            .setPrivacyCheckBoxHeight(20)
            .setHidePrivacySmh(true)
            .setCheckedImageName("login_demo_check_cus")
            .setUnCheckedImageName("login_demo_uncheck_cus")
            .setProtocolPageNavTitle("移动服务及隐私协议", "联通服务及隐私协议", "电信服务及隐私协议")
            .setProtocolPageNavColor(Color.parseColor("#FFFFFF"))
            // 自定义控件在body
            .addCustomView(otherLoginRel, "relative", UnifyUiConfig.POSITION_IN_BODY, null)
            .build(context)
    }

    fun getCConfig(context: Context, listener: LoginUiHelper.CustomViewListener): UnifyUiConfig {
        val closeBtn = ImageView(context)
        closeBtn.setImageResource(R.drawable.login_demo_close)
        closeBtn.scaleType = ImageView.ScaleType.FIT_XY
        closeBtn.setBackgroundColor(Color.TRANSPARENT)
        val layoutParams = RelativeLayout.LayoutParams(50, 50)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        layoutParams.rightMargin = 50
        closeBtn.layoutParams = layoutParams

        return UnifyUiConfig.Builder()
            .setStatusBarColor(Color.parseColor("#ffffff"))
            .setStatusBarDarkColor(true)
            .setNavigationHeight(56) // 设置导航栏高度
            .setNavigationTitle("一键登录") // 设置导航栏文案
            .setHideNavigationBackIcon(true) // 设置是否隐藏导航栏按钮
            .setLogoIconName("ico_logo")
            .setLogoWidth(200)
            .setLogoHeight(70)
            .setLogoTopYOffset(90)
            .setMaskNumberColor(Color.BLACK)
            .setMaskNumberSize(25)
            .setMaskNumberTypeface(Typeface.SERIF)
            .setMaskNumberTopYOffset(190)
            .setSloganSize(13)
            .setSloganColor(Color.parseColor("#9A9A9A"))
            .setSloganTopYOffset(240)
            .setLoginBtnText("易盾一键登录")
            .setLoginBtnTextColor(Color.WHITE)
            .setLoginBtnBackgroundRes("login_demo_auth_bt")
            .setLoginBtnWidth(240)
            .setLoginBtnHeight(45)
            .setLoginBtnTextSize(15)
            .setLoginBtnTopYOffset(280)
            .setPrivacyTextStart("我已阅读并同意")
            .setProtocolText("用户协议")
            .setProtocolLink("https://www.baidu.com")
            .setPrivacyTextEnd("")
            .setPrivacyTextColor(Color.parseColor("#292929"))
            .setPrivacyProtocolColor(Color.parseColor("#3F51B5"))
            .setPrivacySize(13)
            .setPrivacyBottomYOffset(24)
            .setPrivacyMarginLeft(40)
            .setPrivacyMarginRight(40)
            .setPrivacyTextMarginLeft(8)
            .setPrivacyTextGravityCenter(true)
            .setPrivacyTextLayoutGravity(Gravity.CENTER)
            .setPrivacyCheckBoxWidth(20)
            .setPrivacyCheckBoxHeight(20)
            .setHidePrivacySmh(true)
            .setCheckedImageName("login_demo_check_cus")
            .setUnCheckedImageName("login_demo_uncheck_cus")
            .setProtocolPageNavTitle("移动服务及隐私协议", "联通服务及隐私协议", "电信服务及隐私协议")
            .setProtocolPageNavColor(Color.parseColor("#FFFFFF"))
            // 自定义控件在header
            .addCustomView(
                closeBtn,
                "close_btn",
                UnifyUiConfig.POSITION_IN_TITLE_BAR,
                listener
            )
            .build(context)
    }

    fun getDConfig(context: Context): UnifyUiConfig {
        // dialog的宽度(dp值)
        val dialogWidth = (getScreenWidth(context) * 0.8).toInt()
        // dialog的高度(dp值)
        val dialogHeight = (getScreenHeight(context) * 0.5).toInt()
        return UnifyUiConfig.Builder()
            .setStatusBarColor(Color.parseColor("#ffffff"))
            .setStatusBarDarkColor(true)
            .setNavigationHeight(48)
            .setNavigationTitle("欢迎来到一键登录")
            .setNavigationIcon("login_demo_close")
            .setNavigationIconGravity(Gravity.RIGHT) // 设置导航栏按钮方向
            .setNavTitleBold(true)
            .setNavigationIconMargin(16.0f.dip2px(context))
            .setHideLogo(true)
            .setMaskNumberColor(Color.BLACK)
            .setMaskNumberSize(25)
            .setMaskNumberTypeface(Typeface.SERIF)
            .setMaskNumberTopYOffset(60)
            .setSloganSize(13)
            .setSloganColor(Color.parseColor("#9A9A9A"))
            .setSloganTopYOffset(90)
            .setLoginBtnText("易盾一键登录")
            .setLoginBtnTextColor(Color.WHITE)
            .setLoginBtnBackgroundRes("login_demo_auth_bt")
            .setLoginBtnWidth(240)
            .setLoginBtnHeight(45)
            .setLoginBtnTextSize(15)
            .setLoginBtnTopYOffset(130)
            .setPrivacyTextStart("我已阅读并同意")
            .setProtocolText("用户协议")
            .setProtocolLink("https://www.baidu.com")
            .setPrivacyTextEnd("")
            .setPrivacyTextColor(Color.parseColor("#292929"))
            .setPrivacyProtocolColor(Color.parseColor("#3F51B5"))
            .setPrivacySize(13)
            .setPrivacyBottomYOffset(24)
            .setPrivacyMarginLeft(40)
            .setPrivacyMarginRight(40)
            .setPrivacyTextMarginLeft(8)
            .setPrivacyTextGravityCenter(true)
            .setPrivacyTextLayoutGravity(Gravity.CENTER)
            .setPrivacyCheckBoxWidth(20)
            .setPrivacyCheckBoxHeight(20)
            .setHidePrivacySmh(true)
            .setCheckedImageName("login_demo_check_cus")
            .setUnCheckedImageName("login_demo_uncheck_cus")
            .setProtocolPageNavTitle("移动服务及隐私协议", "联通服务及隐私协议", "电信服务及隐私协议")
            .setProtocolPageNavColor(Color.parseColor("#FFFFFF"))
            // 设置dialog模式
            .setDialogMode(true, dialogWidth, dialogHeight, 0, 0, false)
            .setProtocolDialogMode(true) // 设置协议详情页是否dialog模式
            .setBackgroundImage("login_demo_dialog_bg") // 设置背景
            .setProtocolBackgroundImage("login_demo_dialog_bg") // 设置协议详情页背景
            .setActivityTranslateAnimation("yd_dialog_fade_in", "yd_dialog_fade_out") // 设置进出动画
            .build(context)
    }

    @SuppressLint("RtlHardcoded")
    fun getEConfig(context: Context): UnifyUiConfig {
        // dialog的宽度(dp值)
        val dialogWidth = getScreenWidth(context)
        // dialog的高度(dp值)
        val dialogHeight = (getScreenHeight(context) * 0.5).toInt()
        return UnifyUiConfig.Builder()
            .setStatusBarColor(Color.parseColor("#ffffff"))
            .setStatusBarDarkColor(true) // 设置导航栏
            .setNavigationHeight(48)
            .setNavigationTitle("欢迎来到一键登录")
            .setNavigationIcon("login_demo_close")
            .setNavigationIconGravity(Gravity.RIGHT)
            .setNavTitleBold(true)
            .setNavigationIconMargin(16.0f.dip2px(context))
            .setHideLogo(true)
            .setMaskNumberColor(Color.BLACK)
            .setMaskNumberSize(25)
            .setMaskNumberTypeface(Typeface.SERIF)
            .setMaskNumberTopYOffset(60)
            .setSloganSize(13)
            .setSloganColor(Color.parseColor("#9A9A9A"))
            .setSloganTopYOffset(90)
            .setLoginBtnText("易盾一键登录")
            .setLoginBtnTextColor(Color.WHITE)
            .setLoginBtnBackgroundRes("login_demo_auth_bt")
            .setLoginBtnWidth(240)
            .setLoginBtnHeight(45)
            .setLoginBtnTextSize(15)
            .setLoginBtnTopYOffset(130)
            .setPrivacyTextStart("我已阅读并同意")
            .setProtocolText("用户协议")
            .setProtocolLink("https://www.baidu.com")
            .setPrivacyTextEnd("")
            .setPrivacyTextColor(Color.parseColor("#292929"))
            .setPrivacyProtocolColor(Color.parseColor("#3F51B5"))
            .setPrivacySize(13)
            .setPrivacyBottomYOffset(24)
            .setPrivacyMarginLeft(40)
            .setPrivacyMarginRight(40)
            .setPrivacyTextMarginLeft(8)
            .setPrivacyTextGravityCenter(true)
            .setPrivacyTextLayoutGravity(Gravity.CENTER)
            .setPrivacyCheckBoxWidth(20)
            .setPrivacyCheckBoxHeight(20)
            .setHidePrivacySmh(true)
            .setCheckedImageName("login_demo_check_cus")
            .setUnCheckedImageName("login_demo_uncheck_cus")
            .setProtocolPageNavTitle("移动服务及隐私协议", "联通服务及隐私协议", "电信服务及隐私协议")
            .setProtocolPageNavColor(Color.parseColor("#FFFFFF"))
            .setDialogMode(true, dialogWidth, dialogHeight, 0, 0, true)
            .setProtocolDialogMode(true)
            .setActivityTranslateAnimation("xd_dialog_enter", "xd_dialog_exit")
            .build(context)
    }

    fun getFConfig(context: Context, listener: LoginUiHelper.CustomViewListener): UnifyUiConfig {
        val editBtn = ImageView(context)
        editBtn.setImageResource(R.drawable.login_demo_edit)
        editBtn.scaleType = ImageView.ScaleType.FIT_XY
        editBtn.setBackgroundColor(Color.TRANSPARENT)
        val layoutParams = RelativeLayout.LayoutParams(50, 50)
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.oauth_login)
        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.oauth_login)
        layoutParams.leftMargin = 20
        editBtn.layoutParams = layoutParams

        return UnifyUiConfig.Builder()
            .setStatusBarColor(Color.parseColor("#ffffff"))
            .setStatusBarDarkColor(true)
            .setNavigationHeight(56) // 设置导航栏高度
            .setNavigationTitle("一键登录") // 设置导航栏文案
            .setHideNavigationBackIcon(true) // 设置是否隐藏导航栏按钮
            .setLogoIconName("ico_logo")
            .setLogoWidth(200)
            .setLogoHeight(70)
            .setLogoTopYOffset(90)
            .setMaskNumberColor(Color.BLACK)
            .setMaskNumberSize(25)
            .setMaskNumberTypeface(Typeface.SERIF)
            .setMaskNumberTopYOffset(190)
            .setSloganSize(13)
            .setSloganColor(Color.parseColor("#9A9A9A"))
            .setSloganTopYOffset(240)
            .setLoginBtnText("易盾一键登录")
            .setLoginBtnTextColor(Color.WHITE)
            .setLoginBtnBackgroundRes("login_demo_auth_bt")
            .setLoginBtnWidth(240)
            .setLoginBtnHeight(45)
            .setLoginBtnTextSize(15)
            .setLoginBtnTopYOffset(280)
            .setPrivacyTextStart("我已阅读并同意")
            .setProtocolText("用户协议")
            .setProtocolLink("https://www.baidu.com")
            .setPrivacyTextEnd("")
            .setPrivacyTextColor(Color.parseColor("#292929"))
            .setPrivacyProtocolColor(Color.parseColor("#3F51B5"))
            .setPrivacySize(13)
            .setPrivacyBottomYOffset(24)
            .setPrivacyMarginLeft(40)
            .setPrivacyMarginRight(40)
            .setPrivacyTextMarginLeft(8)
            .setPrivacyTextGravityCenter(true)
            .setPrivacyTextLayoutGravity(Gravity.CENTER)
            .setPrivacyCheckBoxWidth(20)
            .setPrivacyCheckBoxHeight(20)
            .setHidePrivacySmh(true)
            .setCheckedImageName("login_demo_check_cus")
            .setUnCheckedImageName("login_demo_uncheck_cus")
            .setProtocolPageNavTitle("移动服务及隐私协议", "联通服务及隐私协议", "电信服务及隐私协议")
            .setProtocolPageNavColor(Color.parseColor("#FFFFFF"))
            // 自定义控件在header
            .addCustomView(
                editBtn,
                "edit_btn",
                UnifyUiConfig.POSITION_IN_BODY,
                listener
            )
            .build(context)
    }
}
