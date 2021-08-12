# 号码认证
直连三大运营商，一步校验手机号与当前 SIM 卡号一致性。优化注册/登录/支付等场景验证流程，有效提升拉新转化率和用户留存率

## 业务场景介绍
| 业务场景 | 说明                                                         |
| -------- | ------------------------------------------------------------ |
| 一键登录 | 用户无需输入手机号码，通过 SDK 拉起授权页，用户确认授权后，SDK 会获取 token，服务端携带 token 到运营商网关获取用户当前上网使用的流量卡号码，并返回给 APP 服务端 |
| 本机校验 | 用户输入手机号码，服务端携带手机号码和 token 去运营商网关进行校验比对，返回的校验结果是用户当前流量卡号码与服务端携带的手机号码是否一致 |

## 兼容性
| 条目        | 说明                                                                      |
| ----------- | -----------------------------------------------------------------------  |
| 适配版本    | minSdkVersion 16 及以上版本                                                 |

## 环境准备
| 条目        | 说明           |
| ----------- | -------------- |
| 网络制式    | 支持移动2G/3G/4G/5G<br>联通3G/4G/5G<br>电信4G/5G<br>2G、3G因网络环境问题，成功率低于4G |
| 网络环境    | 蜂窝网络<br> 蜂窝网络+WIFI同开<br> 双卡手机，取当前发流量的卡号                         |

## 资源引入

### 远程仓库依赖(推荐)
从 3.0.4 版本开始，提供远程依赖的方式，本地依赖的方式逐步淘汰。本地依赖集成替换为远程依赖请先去除干净本地包，避免重复依赖冲突

确认 Project 根目录的 build.gradle 中配置了 mavenCentral 支持

```
buildscript {
    repositories {
        mavenCentral()
    }
    ...
}

allprojects {
    repositories {
        mavenCentral()
    }
}
```
在对应 module 的 build.gradle 中添加依赖

```
implementation 'io.github.yidun:quicklogin:3.0.8'
```
### 本地手动依赖

#### 获取 SDK 

从易盾官网下载号码认证 sdk 的 aar 包 [包地址](https://github.com/yidun/quickpass-android-demo/tree/master/SDK)

#### 添加 aar 包依赖

将获取到的 aar 文件拷贝到对应 module 的 libs 文件夹下（如没有该目录需新建），然后在 build.gradle 文件中增加如下代码

```
android{
    repositories {
        flatDir {
            dirs 'libs'
        }
    } 
}    

dependencies {
    implementation(name: 'quicklogin-external-release-3.0.4', ext: 'aar') // aar 包版本以官网下载下来为准
    implementation(name: 'CMCCSSOSDK-WY-release', ext: 'aar') // aar 包版本以官网下载下来为准
    implementation(name: 'Ui-factory_oauth_mobile_4.0.3', ext: 'aar') // aar 包版本以官网下载下来为准
    implementation(name: 'CTAccount_sdk_api_v3.8.3_all_wy', ext: 'aar') // aar 包版本以官网下载下来为准
    implementation(name: 'base-sdk-libary-release', ext: 'aar')
    implementation 'com.google.code.gson:gson:2.8.5'    // 若项目中原本存在无需添加         
}
```

## 各种配置

### 权限配置

SDK 建议开发者申请如下权限

```
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```
本权限用于移动运营商在双卡情况下更精准的获取数据流量卡的运营商类型，缺少该权限存在取号失败率上升的风险

READ_PHONE_STATE 权限是隐私权限，Android 6.0 及以上需要动态申请。使用前务必先动态申请权限

```
ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
```

### 混淆配置

在 proguard-rules.pro 文件中添加如下混淆规则

```
-dontwarn com.cmic.sso.sdk.**
-keep class com.cmic.sso.**{*;}
-dontwarn com.sdk.**
-keep class com.sdk.** { *;}
-keep class cn.com.chinatelecom.account.**{*;}
-keep public class * extends android.view.View
-keep class com.netease.nis.quicklogin.entity.**{*;}
-keep class com.netease.nis.quicklogin.listener.**{*;}
-keep class com.netease.nis.quicklogin.QuickLogin{
    public <methods>;
    public <fields>;
}
-keep class com.netease.nis.quicklogin.helper.UnifyUiConfig{*;}
-keep class com.netease.nis.quicklogin.helper.UnifyUiConfig$Builder{
     public <methods>;
     public <fields>;
 }
-keep class com.netease.nis.quicklogin.utils.LoginUiHelper$CustomViewListener{
     public <methods>;
     public <fields>;
}
-keep class com.netease.nis.basesdk.**{
    public *;
    protected *;
}
```

## 快速调用示例

### 一键登录

```
public class DemoActivity extends AppCompatActivity {
    private boolean prefetchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final QuickLogin quickLogin = QuickLogin.getInstance(this, "业务id");
        quickLogin.prefetchMobileNumber(new QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                //预取号成功
                prefetchResult = true;
            }

            @Override
            public void onGetMobileNumberError(String YDToken, String msg) {

            }
        });

        Button btn = new Button(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefetchResult) {
                    quickLogin.onePass(new QuickLoginTokenListener() {
                        @Override
                        public void onGetTokenSuccess(String YDToken, String accessCode) {
                            quicklogin.quitActivity();
                            //一键登录成功 运营商token：accessCode获取成功
                            //拿着获取到的运营商token二次校验（建议放在自己的服务端）
                        }

                        @Override
                        public void onGetTokenError(String YDToken, String msg) {
                            quicklogin.quitActivity();
                        }
                    });
                }
            }
        });
    }
}
```

### 本机校验

```
public class DemoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QuickLogin quickLogin = QuickLogin.getInstance(this,"业务id");
        quickLogin.getToken("手机号码", new QuickLoginTokenListener() {
            @Override
            public void onGetTokenSuccess(String YDToken, String accessCode) {
                //运营商token：accessCode获取成功
                //拿着获取到的运营商token二次校验（建议放在自己的服务端）
            }

            @Override
            public void onGetTokenError(String YDToken, String msg) {

            }
        });
    }
}
```

更多使用场景请参考
[demo](https://github.com/yidun/quickpass-android-demo)

demo使用注意事项：

1. 将 app 的 build.gradle 里面的 applicationId 换成自己的测试包名
2. 将 app 的 build.gradle 里面的签名配置改成您自己的签名配置
3. 将初始化的 businessId 换成您在易盾平台创建应用后生成的 businessId

## SDK 方法说明

### 1. 初始化

使用拉取授权页功能前必须先进行初始化操作，建议放在 Application 的 onCreate() 方法中

#### 代码说明

```
QuickLogin quickLogin = QuickLogin.getInstance(Context context, String businessId);
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|context|Context|是|无| 上下文 |
|businessId|String|是|无| 号码认证业务 id |

### 2. 预取号

#### 注意事项

- 用户处于未授权状态时，调用该方法
- 已授权的用户退出当前帐号时，调用该方法
- 在执行拉取授权页的方法之前，提前调用此方法，以提升用户前端体验
- 此方法需要 1~2s 的时间取得临时凭证，不要和拉取授权页方法一起串行调用。建议放在启动页的 onCreate() 方法中或者 Application 的 onCreate() 方法中去调用
- 不要频繁的多次调用

#### 代码说明

```
quickLogin.prefetchMobileNumber(QuickLoginPreMobileListener listener)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|listener|QuickLoginPreMobileListener|是|无| 预取号回调监听 |

#### QuickLoginPreMobileListener 接口说明

```
public interface QuickLoginPreMobileListener {
    /**
     * 预期号成功
     * @param YDToken      易盾Token
     * @param mobileNumber 获取的手机号码掩码
     */
    void onGetMobileNumberSuccess(String YDToken, String mobileNumber);

    /**
     * 预取号失败
     * @param YDToken 易盾Token
     * @param msg     获取手机号掩码失败原因
     */
    void onGetMobileNumberError(String YDToken, String msg);
}
```

### 3. 拉取授权页

#### 注意事项

- 调用拉取授权页方法后将会调起运营商授权页面，已登录状态下请勿调用
- 每次调用拉取授权页方法前需先调用授权页配置方法(setUnifyUiConfig)，否则授权页可能展示异常
- 1 秒之内只能调用一次，必须保证上一次拉起的授权页已经销毁再调用，否则 SDK 会返回请求频繁

#### 代码说明

```
quickLogin.onePass(QuickLoginTokenListener listener)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|listener|QuickLoginTokenListener|是|无| 拉取授权页回调监听 |

#### QuickLoginTokenListener 接口说明

```
public interface QuickLoginTokenListener {
    /**
     * 获取运营商token成功
     * @param YDToken    易盾token
     * @param accessCode 运营商accessCode
     */
    void onGetTokenSuccess(String YDToken, String accessCode);

    /**
     * 获取运营商token失败
     * @param YDToken 易盾token
     * @param msg     出错提示信息
     */
    void onGetTokenError(String YDToken, String msg);

     /**
     * 取消一键登录
     * 包括物理返回键的取消
     */
    void onCancelGetToken();
}
```

### 4. 退出授权页

#### 代码说明

SDK 从 2.2.6 版本开始，不再主动关闭授权页，接入者可根据自己业务需求在合适时机(如一键登录成功)调以下接口来主动关闭授权页

```
quickLogin.quitActivity()
```

### 5. 设置授权页自定义配置

#### 设计规范

开发者不得通过任何技术手段，将授权页面的隐私栏、手机掩码号、供应商品牌内容隐藏、覆盖网易易盾与运营商会对应用授权页面进行审查，若发现上述违规行为，网易易盾有权将您的一键登录功能下线

![安卓规范示意图](https://nos.netease.com/cloud-website-bucket/fc608fc8c376e8b384e947e575ef8b5f.jpg)
![自定义展示图](https://nos.netease.com/cloud-website-bucket/410d6012173c5531b1065909c9484d36.jpg)

#### 注意事项

- 调用该方法可实现对三网运营商授权页面个性化设计，每次调起拉取授权页方法前必须先调用该方法，否则授权界面会显示异常
- 三网界面配置内部实现逻辑不同，请务必使用移动、电信、联通卡分别测试三网界面

#### 代码说明

```
quickLogin.setUnifyUiConfig(UnifyUiConfig uiConfig)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|uiConfig|UnifyUiConfig|是|无| 自定义配置  |

#### UnifyUiConfig.java 可配置元素说明
##### 注意
- 以下所有 API 接口中涉及到图标，样式等资源名称的形参，均表示资源名，且该资源需要放置在 drawable 目录下
- 以下所有 API 接口中如果涉及到顶部偏移和底部偏移的接口，顶部都是相对导航栏底部而言，底部都是相对屏幕底部而言

##### 状态栏
| 方法                                              | 说明                                   |
| ------------------------------------------------- | -------------------------------------- |
| setStatusBarColor(int statusBarColor)             | 设置状态栏背景颜色                         |
| setStatusBarDarkColor(boolean statusBarDarkColor) | 设置状态栏字体图标颜色是否为暗色(黑色) |

##### 导航栏

| 方法                                              | 说明                                                         |
| :------------------------------------------------ | ------------------------------------------------------------ |
| setNavigationIcon(String backIcon)                | 设置导航栏返回按钮图标，backIcon 导航栏图标名称 |
| setNavigationIconDrawable(Drawable navBackIconDrawable)                | 设置导航栏返回按钮图标 Drawable 值 |
| setNavigationBackIconWidth(int backIconWidth)     | 设置导航栏返回图标的宽度，单位 dp                                     |
| setNavigationBackIconHeight(int backIconHeight)   | 设置导航栏返回图标的高度，单位 dp                                     |
| setHideNavigationBackIcon(boolean isHideBackIcon) | 设置是否隐藏导航栏返回按钮                                       |
| setNavigationBackgroundColor(int backgroundColor) | 设置导航栏背景颜色                                           |
| setNavigationHeight(int navHeight)                | 设置导航栏高度，单位 dp                                       |
| setNavigationTitle(String title)                  | 设置导航栏标题                                               |
| setNavigationTitleColor(int titleColor)           | 设置导航栏标题颜色                                           |
| setNavTitleSize(int navTitleSize)                 | 设置导航栏标题大小，单位 sp                                   |
| setNavTitleDpSize(int navTitleDpSize)             | 设置导航栏标题大小，单位 dp                                   |
| setNavTitleBold(boolean navTitleBold)             | 设置导航栏标题是否为粗体                                     |
| setHideNavigation(boolean isHideNavigation)       | 设置是否隐藏导航栏                                           |

##### 应用 Logo

| 方法                                        | 说明                                                         |
| :------------------------------------------ | ------------------------------------------------------------ |
| setLogoIconName(String logoIconName)        | 设置应用 logo 图标，logoIconName：logo 图标名称 |
| setLogoIconDrawable(Drawable logoIconDrawable)        | 设置应用 logo 图标 Drawable 值 |
| setLogoWidth(int logoWidth)                 | 设置应用logo宽度，单位dp                                     |
| setLogoHeight(int logoHeight)               | 设置应用 logo 高度，单位 dp                                     |
| setLogoTopYOffset(int logoTopYOffset)       | 设置 logo 顶部 Y 轴偏移，单位 dp                                  |
| setLogoBottomYOffset(int logoBottomYOffset) | 设置 logo 距离屏幕底部偏移，单位 dp                             |
| setLogoXOffset(int logoXOffset)             | 设置 logo 水平方向的偏移，单位 dp                               |
| setHideLogo(boolean hideLogo)               | 设置是否隐藏 logo                                             |

##### 手机掩码

| 方法                                                         | 说明                                                         |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| setMaskNumberColor(int maskNumberColor)                      | 设置手机掩码颜色                                             |
| setMaskNumberSize(int maskNumberSize)                        | 设置手机掩码字体大小，单位 sp                                 |
| setMaskNumberXOffset(int maskNumberXOffset)                  | 设置手机掩码水平方向的偏移，单位 dp                           |
| setMaskNumberDpSize(int maskNumberDpSize)                    | 设置手机掩码字体大小，单位 dp                                 |
| setMaskNumberTypeface(Typeface tf)                           | 设置手机掩码字体                                           |
| setMaskNumberTopYOffset(int maskNumberTopYOffset)      | 设置手机掩码顶部Y轴偏移，单位 dp                         |
| setMaskNumberBottomYOffset(int maskNumberBottomYOffset)                  | 设置手机掩码距离屏幕底部偏移，单位 dp                           |
| setMaskNumberListener(MaskNumberListener maskNumberListener) | 设置点击手机掩码监听器，用于对手机掩码栏实现自定义功能（可参见 Demo 示例工程） |

##### 认证品牌

| 方法                                            | 说明                                 |
| :---------------------------------------------- | ------------------------------------ |
| setSloganSize(int sloganSize)                   | 设置认证品牌字体大小，单位 sp         |
| setSloganDpSize(int sloganDpSize)               | 设置认证品牌字体大小，单位 dp         |
| setSloganColor(int sloganColor)                 | 设置认证品牌颜色                     |
| setSloganTopYOffset(int sloganTopYOffset)       | 设置认证品牌顶部 Y 轴偏移，单位 dp      |
| setSloganBottomYOffset(int sloganBottomYOffset) | 设置认证品牌距离屏幕底部偏移，单位 dp |
| setSloganXOffset(int sloganXOffset)             | 设置认证品牌水平方向的偏移，单位 dp   |

##### 登录按钮

| 方法                                                   | 说明                                                 |
| :----------------------------------------------------- | ---------------------------------------------------- |
| setLoginBtnText(String loginBtnText)                   | 设置登录按钮文本                                     |
| setLoginBtnTextSize(int loginBtnTextSize)              | 设置登录按钮文本字体大小，单位 sp                     |
| setLoginBtnTextDpSize(int loginBtnTextDpSize)          | 设置登录按钮文本字体大小，单位 dp                     |
| setLoginBtnTextColor(int loginBtnTextColor)            | 设置登录按钮文本颜色                                 |
| setLoginBtnWidth(int loginBtnWidth)                    | 设置登录按钮宽度，单位 dp                             |
| setLoginBtnHeight(int loginBtnHeight)                  | 设置登录按钮高度，单位 dp                             |
| setLoginBtnBackgroundRes(String loginBtnBackgroundRes) | 设置登录按钮背景资源，该资源需要放置在 drawable 目录下 |
| setLoginBtnBackgroundDrawable(Drawable loginBtnBackgroundDrawable) | 设置登录按钮背景资源 Drawable 值 |
| setLoginBtnTopYOffset(int loginBtnTopYOffset)          | 设置登录按钮顶部Y轴偏移，单位 dp                      |
| setLoginBtnBottomYOffset(int loginBtnBottomYOffset)    | 设置登录按钮距离屏幕底部偏移，单位 dp                 |
| setLoginBtnXOffset(int loginBtnXOffset)                | 设置登录按钮水平方向的偏移，单位 dp                   |

##### 隐私协议

| 方法                                                         | 说明                                                         |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| setPrivacyTextColor(int privacyTextColor)                    | 设置隐私栏文本颜色，不包括协议 ，如若隐私栏协议文案为：登录即同意《中国移动认证条款》且授权 QuickLogin 登录， 则该API对除协议‘《中国移动认证条款》’区域外的其余文本生效 |
| setPrivacyProtocolColor(int privacyProtocolColor)            | 设置隐私栏协议颜色 。例如：登录即同意《中国移动认证条款》且授权 QuickLogin 登录 ， 则该 API 仅对‘《中国移动认证条款》’文案生效 |
| setPrivacySize(int privacySize)                              | 设置隐私栏区域字体大小，单位 sp                               |
| setPrivacyDpSize(int privacyDpSize)                          | 设置隐私栏区域字体大小，单位 dp                               |
| setPrivacyTopYOffset(int privacyTopYOffset)                  | 设置隐私栏顶部Y轴偏移，单位 dp                                |
| setPrivacyBottomYOffset(int privacyBottomYOffset)            | 设置隐私栏距离屏幕底部偏移，单位 dp                           |
| setPrivacyTextMarginLeft(int privacyTextMarginLeft)          | 设置隐私栏复选框和文字内边距，单位 dp                             |
| setPrivacyMarginLeft(int privacyMarginLeft)                  | 设置隐私栏水平方向的偏移，单位 dp                             |
| setPrivacyMarginRight(int privacyMarginRight)                | 设置隐私栏右侧边距，单位 dp                                   |
| setPrivacyState(boolean privacyState)                        | 设置隐私栏协议复选框勾选状态，true 勾选，false 不勾选          |
| setHidePrivacyCheckBox(boolean hidePrivacyCheckBox)          | 设置是否隐藏隐私栏勾选框                                     |
| setCheckBoxGravity(int checkBoxGravity)                      | 设置隐私栏勾选框与文本协议对齐方式，可选择顶部（Gravity.TOP），居中（Gravity.CENTER），底部（Gravity.BOTTOM）等 |
| setPrivacyTextGravityCenter(boolean privacyTextGravityCenter | 设置隐私栏文案换行后是否居中对齐，如果为 true 则居中对齐，否则左对齐 |
| setPrivacyCheckBoxWidth(int privacyCheckBoxWidth)            | 设置隐私栏复选框宽度，单位 dp   |
| setPrivacyCheckBoxHeight(int privacyCheckBoxHeight)          | 设置隐私栏复选框高度，单位 dp   |
| setCheckedImageName(String checkedImageName)                 | 设置隐私栏复选框选中时的图片资源，该图片资源需要放到 drawable 目录下 |
| setUnCheckedImageName(String unCheckedImageName)             | 设置隐私栏复选框未选中时的图片资源，该图片资源需要放到 drawable 目录下 |
| setPrivacyTextStart(String privacyTextStart)                 | 设置隐私栏声明部分起始文案 。如：隐私栏声明为"登录即同意《隐私政策》和《中国移动认证条款》且授权易盾授予本机号码"，则可传入"登录即同意" |
| setPrivacyTextStartSize(float spVal)                         | 设置隐私开始字体大小                                         |
| setPrivacyLineSpacing(float add,float mult)                  | 设置隐私行间距       add 行间距 mult 倍数                       |
| setProtocolText(String protocolText)                         | 设置隐私栏协议文本                                           |
| setProtocolLink(String protocolLink)                         | 设置隐私栏协议链接                                           |
| setProtocol2Text(String protocol2Text)                       | 设置隐私栏协议 2 文本                                          |
| setProtocol2Link(String protocol2Link)                       | 设置隐私栏协议 2 链接                                          |
| setPrivacyTextEnd(String privacyTextEnd)                     | 设置隐私栏声明部分尾部文案。如：隐私栏声明为"登录即同意《隐私政策》和《中国移动认证条款》且授权易盾授予本机号码"，则可传入"且授权易盾授予本机号码" |

##### 协议详情 Web 页面导航栏

| 方法                                                         | 说明                                                         |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| setProtocolPageNavTitle(String protocolNavTitle)             | 设置协议 Web 页面导航栏标题，如果需要根据不同运营商设置不同标题，可使用下面重载接口 |
| setProtocolPageNavTitle(String cmProtocolNavTitle, String cuProtocolNavTitle, String ctProtocolNavTitle) | 设置协议 Web 页面导航栏标题，可针对不同运营商单独设置          |
| setProtocolPageNavTitle(String cmProtocolNavTitle, String cuProtocolNavTitle,                                        String ctProtocolNavTitle, String protocolTextNavTitle, String protocol2TextNavTitle) | 设置协议 Web 页面导航栏标题，可根据运营商类型单独设置不同标题，也可针对自身协议设置不同标题 |
| setProtocolPageNavTitleColor(int protocolNavTitleColor)      | 设置协议 Web 页面导航栏标题颜色                                |
| setProtocolPageNavBackIcon(String protocolNavBackIcon)       | 设置协议 Web 页面导航栏返回图标                                |
| setProtocolPageNavColor(int protocolNavColor)                | 设置协议Web页面导航栏颜色                                    |
| setProtocolPageNavHeight(int protocolNavHeight)              | 设置协议 Web 页面导航栏高度                                    |
| setProtocolPageNavTitleSize(int protocolNavTitleSize)        | 设置协议Web页面导航栏标题大小，单位 sp                        |
| setProtocolPageNavTitleDpSize(int protocolNavTitleDpSize)    | 设置协议 Web 页面导航栏标题大小，单位 dp                        |
| setProtocolPageNavBackIconWidth(int protocolNavBackIconWidth) | 设置协议 Web 页面导航栏返回按钮宽度，单位 dp                    |
| setProtocolPageNavBackIconHeight(int protocolNavBackIconHeight) | 设置协议 Web 页面导航栏返回按钮高度，单位 dp                    |


##### 其他

| 方法                                                         | 说明                                                         |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| setBackgroundImage(String backgroundImage)                   | 设置登录页面背景，图片资源需放置到 drawable 目录下             |
| setBackgroundGif(String backgroundGif)                       | 设置登录页面背景为 Gif，Gif 资源需要放置到 drawable 目录下，传入资源名称即可 |
| setBackgroundVideo(String videoPath, String videoImage)      | 设置登录页面背景为视频，参数 videoPath 为背景 Video 文件路径:(支持本地路径如："android.resource://" + context.getPackageName() + "/" + R.raw.xxxVideo；支持网络路径如"https://xxx"(建议下载到本地后使用本地路径，网络路径由于网络环境的不可控体验不如直接加载本地视频)，参数 videoImage 为视频播放前的背景图片名字，2 个参数必须都设置 |
| setBackgroundShadowView(View view)                           | 设置登录页面背景蒙层 View，设置的 View 将位于背景之上其它控件之下。必须配合视频背景使用 |
| setLoginListener(LoginListener loginListener)                | 设置未同意隐私协议但点击一键登录按钮时的事件监听器，可用于自定义相关提示信息，使用示例可参看 Demo 示例工程 |
| setClickEventListener(ClickEventListener clickEventListener) | 设置授权页面点击事件监听，包括左上角返回按钮，登录按钮，隐私协议复选框，隐私协议。当这4类元素被点击时会回调传入的ClickEventListener的onClick(int viewType, int code)接口，viewType为1时表示隐私协议，2表示复选框，3表示左上角返回按钮，4表示登录按钮。当viewType为2或4时，code字段为1则表示复选框勾选，为0则表示复选框未勾选 |
| setActivityTranslateAnimation(String enterAnimation, String exitAnimation) | 设置授权页进场与出场动画，enterAnimation 进场动画xml无后缀文件名，exitAnimation 出场动画xml无后缀文件名，如进场动画文件名为 activity_enter_anim.xml，则进场动画参数传入“activity_enter_anim" |
| setActivityLifecycleCallbacks(ActivityLifecycleCallbacks Callbacks)      | 设置登录页面生命周期监听回调 |
| setActivityResultCallbacks(ActivityResultCallbacks callbacks)      | 设置登录页面 onActivityResult 回调 |

##### 弹窗模式

```
setDialogMode(boolean isDialogMode, int dialogWidth, int dialogHeight, int dialogX, int dialogY, boolean isBottomDialog)
```

参数说明

|参数|类型|说明|
|----|----|-------|
|isDialogMode|boolean|是否 dialog 样式<br>true 弹窗样式<br>false：非弹窗样式 |
|dialogWidth|int|授权页弹窗宽度，单位 dp|
|dialogHeight|int|授权页弹窗高度，单位 dp|
|dialogX|int|授权页弹窗 X 轴偏移量，以屏幕中心为原点|
|dialogY|int|授权页弹窗 Y 轴偏移量，以屏幕中心为原点|
|isBottomDialog|boolean|授权页弹窗是否贴于屏幕底部<br>true：显示在屏幕底部，dialogY 失效<br> false：不显示在屏幕底部，以 dialogY 参数为准

##### 设置横竖屏
在 manifest 文件中，指定授权页 activity 的 screenOrientation 为 landscape 即可

注意：只有全面屏不透明的 Activity 才能指定方向，否则在 8.0 系统版本上会报"only fullscreen opaque activities can request orientation"异常

3.0.6 版本之前
```
<!--移动登录类名-->
<activity
    android:name="com.cmic.sso.wy.activity.LoginAuthActivity"
    android:screenOrientation="landscape"
    tools:replace="android:screenOrientation" />
<!--电信联通登录类名-->
<activity
    android:name="com.netease.nis.quicklogin.ui.YDQuickLoginActivity"
    android:configChanges="keyboardHidden|orientation|screenSize"
    android:launchMode="singleTop"
    android:screenOrientation="landscape"
    />
```
3.0.6 版本之后
```
<activity
    android:name="com.netease.nis.quicklogin.ui.CmccLoginActivity"
    android:screenOrientation="landscape"
    tools:replace="android:screenOrientation" />
<!--电信联通登录类名-->
<activity
    android:name="com.netease.nis.quicklogin.ui.YDQuickLoginActivity"
    android:screenOrientation="landscape"
    tools:replace="android:screenOrientation"
    />
```

##### 全面屏(背景延伸到状态栏)

1. 隐藏导航栏
```
setHideNavigation(true)
```
2. 设置状态栏透明
```
setStatusBarColor(android.R.color.transparent)
```
3. 在 res/values-v21 文件夹下新建 styles.xml 添加如下主题

```
<style name="Theme.ActivityTransparentStyle" parent="Theme.AppCompat.Light.NoActionBar">
    <!--背景透明-->
    <item name="android:windowBackground">@android:color/transparent</item>
    <!--设置状态栏透明-->
    <item name="android:statusBarColor">@android:color/transparent</item>
    <!--延伸到顶部状态栏-->
    <item name="android:windowTranslucentNavigation">true</item>
</style>
```
4. 重置授权页的主题 theme

3.0.6 版本之前

```
<activity
    android:name="com.cmic.sso.wy.activity.LoginAuthActivity"
    android:theme="@style/Theme.ActivityTransparentStyle"
    tools:replace="theme" />
<activity
    android:name="com.netease.nis.quicklogin.ui.YDQuickLoginActivity"
    android:theme="@style/Theme.ActivityTransparentStyle"
    tools:replace="theme" />
```

3.0.6 版本之后
```
<activity
    android:name="com.netease.nis.quicklogin.ui.CmccLoginActivity"
    android:theme="@style/Theme.ActivityTransparentStyle"
    tools:replace="theme" />
<activity
    android:name="com.netease.nis.quicklogin.ui.YDQuickLoginActivity"
    android:theme="@style/Theme.ActivityTransparentStyle"
    tools:replace="theme" />
```

授权页默认主题样式如下

```
<style name="Theme.ActivityDialogStyle" parent="Theme.AppCompat.Light.NoActionBar">
    <!-- 不透明 -->
    <item name="android:windowIsTranslucent">true</item>
    <!-- 背景 -->
    <item name="android:windowBackground">@android:color/transparent</item>
    <!-- 模糊 -->
    <item name="android:backgroundDimEnabled">true</item>
    <item name="android:windowContentOverlay">@null</item>
    <item name="android:windowCloseOnTouchOutside">false</item>
</style>
```
##### 自定义控件

```
addCustomView(View customView, String viewId, int positionType, LoginUiHelper.CustomViewListener listener)
```

参数说明

|参数|类型|说明|
|----|----|-------|
|customView|View|待添加自定义 View 对象 |
|viewId|String|待添加自定义 View 的 id|
|positionType|int|添加位置，包含 2 种类型：`UnifyUiConfig.POSITION_IN_TITLE_BAR`表示添加在导航栏中，`UnifyUiConfig.POSITION_IN_BODY`表示添加到导航栏下方的 BODY 中|
|listener|CustomViewListener|待添加的自定义 View 的事件监听器|

### 6. 判断运营商类型(非必须)

#### 代码说明

```
quickLogin.getOperatorType(Context context)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|context|Context|是|无| 上下文 |

#### 返回值说明

|类型|描述|
|----|----|
| int | 1：电信 2：移动 3：联通 5：未知 |

### 7. 设置授权页协议复选框是否选中(授权页拉起之后调用，非必须)

#### 代码说明

```
quickLogin.setPrivacyState(boolean isChecked)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|isChecked|boolean|是|true| 复选框是否选中 |

### 8. 设置预取号超时时间(非必须)

#### 代码说明

```
quickLogin.setPrefetchNumberTimeout(int timeout)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|timeout|int|是|6| 单位秒 |

### 9. 设置取号超时时间(非必须)

#### 代码说明

```
quickLogin.setFetchNumberTimeout(int timeout)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|timeout|int|是|5| 单位秒 |

### 10. 返回 SDK 版本号(非必须)

#### 代码说明

```
quickLogin.getSDKVersion()
```

#### 返回值说明

|类型|描述|
|----|----|
| String | 版本号 |

### 11. 设置是否打开日志(非必须)

#### 代码说明

```
quickLogin.setDebugMode(boolean debug)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|debug|boolean|是|false| 是否打印日志 |

### 12. 设置预取号携带额外参数(非必须)

#### 代码说明

```
quickLogin.setExtendData(JSONObject extendData)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|extendData|JSONObject|是|无| 额外参数 |

### 13. 本机校验

在初始化之后执行，本机校验和一键登录可共用初始化，本机校验界面需自行实现

#### 代码说明

```
getToken(String mobileNumber，QuickLoginTokenListener listener)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|mobileNumber|String|是|无| 待校验手机号 |
|listener|QuickLoginTokenListener|是|无| 本机校验监听器 |

#### QuickLoginTokenListener 接口说明

```
public interface QuickLoginTokenListener {
    /**
     * 获取运营商token成功
     * @param YDToken    易盾token
     * @param accessCode 运营商accessCode
     */
    void onGetTokenSuccess(String YDToken, String accessCode);

    /**
     * 获取运营商token失败
     * @param YDToken 易盾token
     * @param msg     出错提示信息
     */
    void onGetTokenError(String YDToken, String msg);
}
```
