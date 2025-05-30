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

## 注意事项
<font color=red>***和其他易盾产品一起使用需要考虑版本兼容性，若同时接多个易盾sdk，需要测试回归下是否有异常***</font>

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
implementation 'io.github.yidun:quicklogin:3.5.1'
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

SDK内部权限说明

| 权限名字 | 权限说明   | 权限说明 |
| ------- | -------- | -------- |
| INTERNET | 允许应用程序联网 | 用于访问网关和认证服务器 |
| ACCESS_WIFI_STATE | 允许访问WiFi网络状态信息 | 允许程序访问WiFi网络状态信息 |
| ACCESS_NETWORK_STATE | 允许访问网络状态 | 区分移动网络或WiFi网络 |
| CHANGE_NETWORK_STATE | 允许改变网络连接状态 | 设备在WiFi跟数据双开时，强行切换使用数据网络 |
| CHANGE_WIFI_STATE | 允许改变WiFi网络连接状态 | 设备在WiFi跟数据双开时，强行切换使用 |
| READ_PHONE_STATE | 允许读取手机状态 | 获取IMSI用于判断双卡和换卡（可选，不赋予预取号成功率降低1%） |


### 混淆配置

在 proguard-rules.pro 文件中添加如下混淆规则

```
-dontwarn com.cmic.gen.sdk.**
-keep class com.cmic.gen.sdk.**{*;}
-dontwarn com.unicom.online.account.shield.**
-keep class com.unicom.online.account.shield.** {*;}
-dontwarn com.unicom.online.account.kernel.**
-keep class com.unicom.online.account.kernel.** {*;}
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

### 资源保护配置

打包配置了 shrinkResources true 之后（或者使用了微信资源混淆andResGuard）发现资源找不到情况需要配置下资源保护
在 res/raw/keep.xml 添加需要保护的资源

```
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools"
    tools:keep="@layout/yd_*,@anim/yd_*,@anim/umcsdk*",@drawable/umcsdk*",@drawable/yd_*",@drawable/dialog_loading,@drawable/loading,...配置文件通过api设置的所有资源"/>
```

## 快速调用示例

### 一键登录

```
public class DemoActivity extends AppCompatActivity {
    private boolean prefetchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final QuickLogin quickLogin = QuickLogin.getInstance();
        quickLogin.init(this, "业务id");
        quickLogin.prefetchMobileNumber(new QuickLoginPreMobileListener() {
            @Override
            public void onGetMobileNumberSuccess(String ydToken, String mobileNumber) {
                //预取号成功
                prefetchResult = true;
            }

            @Override
            public void onGetMobileNumberError(String ydToken, int code, String msg) {

            }
        });

        Button btn = new Button(this);
        UnifyUiConfig.Builder builder = new UnifyUiConfig.Builder();
        quickLogin.setUnifyUiConfig(builder.build(this));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefetchResult) {
                    quickLogin.onePass(new QuickLoginTokenListener() {
                        @Override
                        public void onGetTokenSuccess(String ydToken, String accessCode) {
                            quickLogin.quitActivity();
                            //一键登录成功 运营商token：accessCode获取成功
                            //拿着获取到的运营商token二次校验（建议放在自己的服务端）
                        }

                        @Override
                        public void onGetTokenError(String ydToken, int code, String msg) {
                            quickLogin.quitActivity();
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

        QuickLogin quickLogin = QuickLogin.getInstance();
        quickLogin.init(this,"业务id");
        quickLogin.getToken("手机号码", new QuickLoginTokenListener() {
            @Override
            public void onGetTokenSuccess(String ydToken, String accessCode) {
                //运营商token：accessCode获取成功
                //拿着获取到的运营商token二次校验（建议放在自己的服务端）
            }

            @Override
            public void onGetTokenError(String ydToken,int code, String msg) {

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
QuickLogin quickLogin = QuickLogin.getInstance();
quickLogin.init(Context context, String businessId);
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
- **此方法需要 1~2s 的时间取得临时凭证，不要和拉取授权页方法一起串行调用。建议放在启动页的 onCreate() 方法中或者 Application 的 onCreate() 方法中去调用**
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
     * @param ydToken      易盾Token
     * @param mobileNumber 获取的手机号码掩码
     */
    void onGetMobileNumberSuccess(String ydToken, String mobileNumber);

    /**
     * 预取号失败
     * @param ydToken 易盾Token
     * @param msg     获取手机号掩码失败原因
     */
    void onGetMobileNumberError(String ydToken, int code, String msg);
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
     * @param ydToken    易盾token
     * @param accessCode 运营商accessCode
     */
    void onGetTokenSuccess(String ydToken, String accessCode);

    /**
     * 获取运营商token失败
     * @param ydToken 易盾token
     * @param code    错误异常码
     * @param msg     出错提示信息
     */
    void onGetTokenError(String ydToken,int code, String msg);

     /**
     * 取消一键登录
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
| 方法                   | 参数                       | 说明 |
| ----------------------| -------------------------- |--------------------------------|
| setStatusBarColor     | statusBarColor:int         |设置状态栏背景颜色，Android 5.0 及以上生效|
| setStatusBarDarkColor | statusBarDarkColor:boolean |设置状态栏字体图标颜色是否为暗色(黑色)，暗色 Android6.0 及以上生效|

##### 导航栏（默认高度0）

| 方法                          | 参数        | 说明                                                     |
| :----------------------------| -----------------------------|----------------------------------------|
| setNavigationIcon            | backIcon:String              | 设置导航栏返回按钮图标，backIcon 导航栏图标名称 |
| setNavigationIconDrawable    | navBackIconDrawable:Drawable | 设置导航栏返回按钮图标 Drawable 值           |
| setNavigationBackIconWidth   | backIconWidth:int            | 设置导航栏返回图标的宽度，单位 dp             |
| setNavigationBackIconHeight  | backIconHeight:int           | 设置导航栏返回图标的高度，单位 dp              |
| setNavigationIconGravity  | navigationIconGravity:int           | 设置导航栏返回图标位置，居左Gravity.LEFT，居右Gravity.RIGHT             |
| setNavigationIconMargin  | navigationIconMargin:int           | 设置导航栏返回图标上下左右的边距，单位 dp              |
| setHideNavigationBackIcon    | isHideBackIcon:boolean       | 设置是否隐藏导航栏返回按钮                   |
| setNavigationBackgroundColor | backgroundColor:int          | 设置导航栏背景颜色  |
| setNavigationHeight          | navHeight:int                | 设置导航栏高度，单位 dp    |
| setNavigationTitle           | title:String                 | 设置导航栏标题     |
| setNavigationTitleColor      | titleColor:int               | 设置导航栏标题颜色   |
| setNavTitleSize              | navTitleSize:int             | 设置导航栏标题大小，单位 sp |
| setNavTitleDpSize            | navTitleDpSize:int           | 设置导航栏标题大小，单位 dp |
| setNavTitleBold              | navTitleBold:boolean         | 设置导航栏标题是否为粗体    |
| setHideNavigation            | isHideNavigation:boolean     | 设置是否隐藏导航栏     |
| setNavTitleDrawable          | navTitleDrawable:Drawable    | 设置导航栏标题居左图标     |
| setNavTitleDrawablePadding   | navTitleDrawablePadding:int  | 设置导航栏标题居左图标和文本的间距     |


##### 应用 Logo

| 方法                  | 参数                      | 说明                                        |
| :--------------------| ------------------------- |-------------------------------------------|
| setLogoIconName      | logoIconName:String       | 设置应用 logo 图标，logoIconName：logo 图标名称 |
| setLogoIconDrawable  | logoIconDrawable:Drawable | 设置应用 logo 图标 Drawable 值 |
| setLogoWidth         | logoWidth:int             | 设置应用logo宽度，单位dp |
| setLogoHeight        | logoHeight:int            | 设置应用 logo 高度，单位 dp  |
| setLogoTopYOffset    | logoTopYOffset:int        | 设置 logo 顶部 Y 轴偏移，单位 dp |
| setLogoBottomYOffset | logoBottomYOffset:int     | 设置 logo 距离屏幕底部偏移，单位 dp     |
| setLogoXOffset       | logoXOffset:int           | 设置 logo 水平方向的偏移，单位 dp|
| setHideLogo          | hideLogo:boolean          | 设置是否隐藏 logo   |

##### 手机掩码

| 方法                        | 参数                                  | 说明                         |
| :--------------------------| --------------------------------------|--------------|
| setMaskNumberColor         | maskNumberColor:int                   | 设置手机掩码颜色 |
| setMaskNumberSize          | maskNumberSize:int                    | 设置手机掩码字体大小，单位 sp |
| setMaskNumberXOffset       | maskNumberXOffset:int                 | 设置手机掩码水平方向的偏移，单位 dp |
| setMaskNumberDpSize        | maskNumberDpSize:int                  | 设置手机掩码字体大小，单位 dp |
| setMaskNumberTypeface      | tf:Typeface                           | 设置手机掩码字体 |
| setMaskNumberTopYOffset    | maskNumberTopYOffset:int              | 设置手机掩码顶部Y轴偏移，单位 dp |
| setMaskNumberBottomYOffset | maskNumberBottomYOffset:int           | 设置手机掩码距离屏幕底部偏移，单位 dp |
| setMaskNumberBackgroundRes | maskNumberBackgroundRes:String           | 设置手机掩码背景资源，该资源需要放置在 drawable 目录下 |
| setMaskNumberListener      | maskNumberListener:MaskNumberListener | 设置手机掩码外抛，用于对手机掩码栏实现背景和宽高等自定义功能（可参见 Demo 示例工程）|
| setMaskNumberBold | isMaskNumberBold:boolean           | 设置手机掩码字体是否粗体 |

##### 认证品牌

| 方法                    | 参数              | 说明 |
| :--------------------- | ----------------------- |--------------------------|
| setSloganSize          | sloganSize:int          | 设置认证品牌字体大小，单位 sp |
| setSloganDpSize        | sloganDpSize:int        | 设置认证品牌字体大小，单位 dp |
| setSloganColor         | sloganColor:int         | 设置认证品牌颜色 |
| setSloganTopYOffset    | sloganTopYOffset:int    | 设置认证品牌顶部 Y 轴偏移，单位 dp |
| setSloganBottomYOffset | sloganBottomYOffset:int | 设置认证品牌距离屏幕底部偏移，单位 dp |
| setSloganXOffset       | sloganXOffset:int       | 设置认证品牌水平方向的偏移，单位 dp |
| setSloganBold       | isSloganBold:boolean       | 设置认证品牌字体是否粗体 |

##### 登录按钮

| 方法                           | 参数                               | 说明 |
| :---------------------------- | ---------------------------------- |---|
| setLoginBtnText               | loginBtnText:String                | 设置登录按钮文本 |
| setLoginBtnTextSize           | loginBtnTextSize:int               | 设置登录按钮文本字体大小，单位 sp |
| setLoginBtnTextDpSize         | loginBtnTextDpSize:int             | 设置登录按钮文本字体大小，单位 dp |
| setLoginBtnTextColor          | loginBtnTextColor:int              | 设置登录按钮文本颜色 |
| setLoginBtnWidth              | loginBtnWidth:int                  | 设置登录按钮宽度，单位 dp |
| setLoginBtnHeight             | loginBtnHeight:int                 | 设置登录按钮高度，单位 dp |
| setLoginBtnMarginLeft         | loginBtnMarginLeft:int             | 设置登录按钮距离屏幕左边间距，单位 dp |
| setLoginBtnMarginRight        | loginBtnMarginRight:int            | 设置登录按钮距离屏幕右边间距，单位 dp |
| setLoginBtnBackgroundRes      | loginBtnBackgroundRes:String       | 设置登录按钮背景资源，该资源需要放置在 drawable 目录下 |
| setLoginBtnBackgroundDrawable | loginBtnBackgroundDrawable:Drawable| 设置登录按钮背景资源 Drawable 值 |
| setLoginBtnTopYOffset         | loginBtnTopYOffset:int             | 设置登录按钮顶部Y轴偏移，单位 dp |
| setLoginBtnBottomYOffset      | loginBtnBottomYOffset:int          | 设置登录按钮距离屏幕底部偏移，单位 dp |
| setLoginBtnXOffset            | loginBtnXOffset:int                | 设置登录按钮水平方向的偏移，单位 dp |
| setLoginBtnBold            | isLoginBtnBold:boolean                | 设置登录按钮字体是否粗体 |

##### 隐私协议

| 方法                                                            | 参数                          | 说明 |
| :-------------------------- | -------------------------------  |----------------------------------------------|
| setPrivacyTextColor         | privacyTextColor:int             | 设置隐私栏文本颜色，不包括协议 ，如若隐私栏协议文案为：登录即同意《中国移动认证条款》且授权 QuickLogin 登录， 则该API对除协议‘《中国移动认证条款》’区域外的其余文本生效|
| setPrivacyDialogTextColor   | privacyDialogTextColor:int       | 设置隐私弹窗模式隐私栏文本颜色，不包括协议                                                                           |
| setPrivacyProtocolColor     | privacyProtocolColor:int         | 设置隐私栏协议颜色 。例如：登录即同意《中国移动认证条款》且授权 QuickLogin 登录 ， 则该 API 仅对‘《中国移动认证条款》’文案生效  |
| setPrivacyDialogProtocolColor| privacyProtocolColor:int         | 设置隐私弹窗隐私栏协议颜色   |
| setPrivacySize              | privacySize:int                  | 设置隐私栏区域字体大小，单位 sp|
| setPrivacyBold              | isPrivacyBold:boolean            | 设置隐私栏区域字体是否粗体 |
| setPrivacyDpSize            | privacyDpSize:int                | 设置隐私栏区域字体大小，单位 dp|
| setPrivacyTopYOffset        | privacyTopYOffset:int            | 设置隐私栏顶部Y轴偏移，单位 dp |
| setPrivacyBottomYOffset     | privacyBottomYOffset:int         | 设置隐私栏距离屏幕底部偏移，单位 dp |
| setPrivacyWidth             | privacyWidth:int                 | 设置隐私栏宽度，单位 dp |
| setPrivacyTextMarginLeft    | privacyTextMarginLeft:int        | 设置隐私栏复选框和文字内边距，单位 dp  |
| setPrivacyMarginLeft        | privacyMarginLeft:int            | 设置隐私栏水平方向的偏移，单位 dp |
| setPrivacyMarginRight       | privacyMarginRight:int           | 设置隐私栏右侧边距，单位 dp |
| setPrivacyState             | privacyState:boolean             | 设置隐私栏协议复选框勾选状态，true 勾选，false 不勾选 |
| setHidePrivacyCheckBox      | hidePrivacyCheckBox:boolean      | 设置是否隐藏隐私栏勾选框  |
| setCheckBoxGravity          | checkBoxGravity:int              | 设置隐私栏勾选框与文本协议对齐方式，可选择顶部（Gravity.TOP），居中（Gravity.CENTER），底部（Gravity.BOTTOM）等 |
| setPrivacyTextGravityCenter | privacyTextGravityCenter:boolean | 设置隐私栏文案换行后是否居中对齐，如果为 true 则居中对齐，否则左对齐 |
| setPrivacyCheckBoxWidth     | privacyCheckBoxWidth:int         | 设置隐私栏复选框宽度，单位 dp |
| setPrivacyCheckBoxHeight    | privacyCheckBoxHeight:int        | 设置隐私栏复选框高度，单位 dp |
| setCheckedImageName         | checkedImageName:String          | 设置隐私栏复选框选中时的图片资源，该图片资源需要放到 drawable 目录下 |
| setUnCheckedImageName       | unCheckedImageName:String        | 设置隐私栏复选框未选中时的图片资源，该图片资源需要放到 drawable 目录下 |
| setPrivacyTextStart         | privacyTextStart:String          |  设置隐私栏声明部分起始文案 。如：隐私栏声明为"登录即同意《隐私政策》和《中国移动认证条款》且授权易盾授予本机号码"，则可传入"登录即同意" |
| setPrivacyTextStartSize     | spVal:float                      | 设置隐私开始字体大小 |
| setPrivacyLineSpacing       | add:float,mult:float             | 设置隐私行间距 add 行间距 mult 倍数 |
| setHidePrivacySmh       | hidePrivacySmh:boolean             | 是否隐藏书名号 |
| setProtocolConnect          | protocolConnect:String           | 运营商协议与自定义隐私栏协议连接符"和" |
| setUserProtocolConnect      | userProtocolConnect:String       | 自定义隐私栏协议之间连接符"、" |
| setOperatorPrivacyAtLast    | operatorPrivacyAtLast:boolean    | 运营商协议的位置是否在末尾  |
| setProtocolText             | protocolText:String              | 设置隐私栏协议文本 |
| setProtocolLink             | protocolLink:String              | 设置隐私栏协议链接 |
| setProtocol2Text            | protocol2Text:String             | 设置隐私栏协议 2 文本 |
| setProtocol2Link            | protocol2Link:String             | 设置隐私栏协议 2 链接 |
| setProtocol3Text            | protocol3Text:String             | 设置隐私栏协议 3 文本 |
| setProtocol3Link            | protocol3Link:String             | 设置隐私栏协议 3 链接 |
| setPrivacyTextEnd           | privacyTextEnd:String            | 设置隐私栏声明部分尾部文案。如：隐私栏声明为"登录即同意《隐私政策》和《中国移动认证条款》且授权易盾授予本机号码"，则可传入"且授权易盾授予本机号码" |
| setPrivacyDialogWidth | privacyDialogText:String | 协议未勾选隐私弹窗宽度 |
| setPrivacyDialogHeight | privacyDialogText:String | 协议未勾选隐私弹窗高度 |
| setPrivacyDialogBg | privacyDialogText:String | 协议未勾选隐私弹窗背景 |
| setPrivacyDialogTitle | privacyDialogText:String | 协议未勾选隐私弹窗标题 |
| setPrivacyDialogTitleSize | privacyDialogText:String | 协议未勾选隐私弹窗标题字体大小 |
| setPrivacyDialogTitleColor | privacyDialogText:String | 协议未勾选隐私弹窗标题颜色 |
| setPrivacyDialogTitleMarginTop | privacyDialogText:String | 协议未勾选隐私弹窗标题距离顶部的距离 |
| setIsPrivacyDialogTitleBold | privacyDialogText:String | 协议未勾选隐私弹窗标题是否加粗 |
| setPrivacyDialogContentStart | privacyDialogText:String | 协议未勾选隐私弹窗开始文本 |
| setPrivacyDialogContentEnd | privacyDialogText:String | 协议未勾选隐私弹窗结束文本 |
| setPrivacyDialogTextSize | privacyDialogTextSize:float | 协议未勾选弹窗文本字体大小 |
| setIsPrivacyDialogContentBold | privacyDialogText:String | 协议未勾选隐私弹窗文本是否加粗 |
| setPrivacyDialogContentMarginLeft | privacyDialogText:String | 协议未勾选隐私弹窗文本距离左边的距离 |
| setPrivacyDialogContentMarginRight | privacyDialogText:String | 协议未勾选隐私弹窗文本距离右边的距离 |
| setPrivacyDialogContentMarginTop | privacyDialogText:String | 协议未勾选隐私弹窗文本距离顶部的距离 |
| setPrivacyDialogBtnTextSize | privacyDialogText:String | 协议未勾选隐私弹窗按钮文本大小 |
| setPrivacyDialogBtnWidth | privacyDialogText:String | 协议未勾选隐私弹窗按钮宽度 |
| setPrivacyDialogBtnHeight | privacyDialogText:String | 协议未勾选隐私弹窗按钮高度 |
| setPrivacyDialogBtnMarginLeft | privacyDialogText:String | 协议未勾选隐私弹窗按钮距离左边距离 |
| setPrivacyDialogBtnMarginRight | privacyDialogText:String | 协议未勾选隐私弹窗按钮距离右边距离 |
| setPrivacyDialogBtnMarginTop | privacyDialogText:String | 协议未勾选隐私弹窗按钮距离顶部距离 |
| setPrivacyDialogBtnMarginBottom | privacyDialogText:String | 协议未勾选隐私弹窗按钮距离底部距离 |
| setPrivacyDialogBtnAgreeText | privacyDialogText:String | 协议未勾选隐私弹窗按钮同意文本|
| setPrivacyDialogBtnAgreeTextColor | privacyDialogText:String | 协议未勾选隐私弹窗按钮同意文本颜色 |
| setPrivacyDialogBtnAgreeBg | privacyDialogText:String | 协议未勾选隐私弹窗按钮同意文本背景 |
| setPrivacyDialogBtnDisagreeText | privacyDialogText:String | 协议未勾选隐私弹窗按钮拒绝文本 |
| setPrivacyDialogBtnDisagreeTextColor | privacyDialogText:String | 协议未勾选隐私弹窗按钮拒绝文本颜色 |
| setPrivacyDialogBtnDisagreeBg | privacyDialogText:String | 协议未勾选隐私弹窗按钮拒绝文本背景 |
| setPrivacyDialogText | privacyDialogText:String | 协议未勾选弹窗自定义message |
| setPrivacyDialogAuto | privacyDialogAuto:boolean | 协议未勾选弹窗点击确认是否自动登录 |

##### 协议详情 Web 页面导航栏

| 方法                              | 参数                                                         | 说明 |
| :------------------------------- | ------------------------------ | ---------------------------------- |
| setProtocolPageNavTitle          | protocolNavTitle:String                                                       | 设置协议 Web 页面导航栏标题，如果需要根据不同运营商设置不同标题，可使用下面重载接口 |
| setProtocolPageNavTitle          | cmProtocolNavTitle:String,<br>cuProtocolNavTitle:String,<br>ctProtocolNavTitle:String | 设置协议 Web 页面导航栏标题，可针对不同运营商单独设置 |
| setProtocolPageNavTitleColor     | protocolNavTitleColor:int                    | 设置协议 Web 页面导航栏标题颜色  |
| setProtocolPageNavBackIcon       | protocolNavBackIcon:String                   | 设置协议 Web 页面导航栏返回图标 |
| setProtocolPageNavColor          | protocolNavColor:int                        | 设置协议Web页面导航栏颜色 |
| setProtocolPageNavHeight         | protocolNavHeight:int                      | 设置协议 Web 页面导航栏高度 |
| setProtocolPageNavTitleSize      | protocolNavTitleSize:int            | 设置协议Web页面导航栏标题大小，单位 sp |
| setProtocolPageNavTitleDpSize    | protocolNavTitleDpSize:int             | 设置协议 Web 页面导航栏标题大小，单位 dp |
| setProtocolPageNavBackIconWidth  | protocolNavBackIconWidth:int            | 设置协议 Web 页面导航栏返回按钮宽度，单位 dp |
| setProtocolPageNavBackIconHeight | protocolNavBackIconHeight:int          | 设置协议 Web 页面导航栏返回按钮高度，单位 dp |
| setProtocolPageNavBackIconMargin | protocolNavBackIconMargin:int          | 设置协议 Web 页面导航栏返回按钮距离左边的距离，单位 dp |
| setProtocolNavTitleBold       | isProtocolNavTitleBold:boolean                   | 设置协议Web页面导航栏标题是否粗体 |

##### 其他

| 方法                                                         | 参数                                                         | 说明 |
| :----------------------------------------------------------- | ------------------------------------------------------------ |---|
| setBackgroundImage            | backgroundImage:String | 设置登录页面背景，图片资源需放置到 drawable 目录下 |
| setBackgroundGif              | backgroundGif:String | 设置登录页面背景为 Gif，Gif 资源需要放置到 drawable 目录下，传入资源名称即可 |
| setBackgroundVideo            | videoPath:String, videoImage:String | 设置登录页面背景为视频，参数 videoPath 为背景 Video 文件路径:(支持本地路径如："android.resource://" + context.getPackageName() + "/" + R.raw.xxxVideo；支持网络路径如"https://xxx"(建议下载到本地后使用本地路径，网络路径由于网络环境的不可控体验不如直接加载本地视频)，参数 videoImage 为视频播放前的背景图片名字，2 个参数必须都设置 |
| setBackgroundShadowView       | view:View  | 设置登录页面背景蒙层 View，设置的 View 将位于背景之上其它控件之下。必须配合视频背景使用 |
| setLoginListener              | loginListener:LoginListener | 设置未同意隐私协议但点击一键登录按钮时的事件监听器，可用于自定义相关提示(默认为弹窗提示)，使用示例可参看 Demo 示例工程 |
| setClickEventListener         | clickEventListener:ClickEventListener | 设置授权页面点击事件监听，包括左上角返回按钮，登录按钮，隐私协议复选框，隐私协议。当这4类元素被点击时会回调传入的ClickEventListener的onClick(int viewType, int code)接口，viewType为1时表示隐私协议，2表示复选框，3表示左上角返回按钮，4表示登录按钮。当viewType为2或4时，code字段为1则表示复选框勾选，为0则表示复选框未勾选 |
| setActivityTranslateAnimation | enterAnimation:String,exitAnimation:String | 设置授权页进场与出场动画，enterAnimation 进场动画xml无后缀文件名，exitAnimation 出场动画xml无后缀文件名，如进场动画文件名为 activity_enter_anim.xml，则进场动画参数传入“activity_enter_anim" |
| setActivityLifecycleCallbacks | Callbacks:ActivityLifecycleCallbacks | 设置登录页面生命周期监听回调 |
| setActivityResultCallbacks    | callbacks:ActivityResultCallbacks | 设置登录页面 onActivityResult 回调 |
| setProtocolBackgroundImage    | protocolBackgroundImage:String | 设置协议详情页背景，图片资源需放置到 drawable 目录下 |
| setBackPressedAvailable| backPressedAvailable:boolean | 设置物理返回键是否可用 |
| setLoadingVisible| loadingVisible:boolean | 点击登录按钮loading是否显示 |
| setLoadingView| loadingView:ViewGroup | 自定义loadingView |
| setVirtualButtonHidden| virtualButtonHidden:boolean | 是否隐藏虚拟按键 |

##### 弹窗模式

```
setDialogMode(boolean isDialogMode, int dialogWidth, int dialogHeight, int dialogX, int dialogY, boolean isBottomDialog) // 授权页弹窗模式
setDialogHideOnTouchOutside(boolean isDialogHideOnTouchOutside) // 授权页弹窗模式点击阴影部分是否允许关闭，默认允许
setProtocolDialogMode(boolean isProtocolDialogMode) // 协议详情页是否开启弹窗模式
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
可以通过如下 api 设置

```
setLandscape(true)
```

注意：只有全屏不透明的 Activity 才能指定方向，否则在 8.0 系统版本上会报"only fullscreen opaque activities can request orientation"异常。全屏透明的 Activity 如需指定方向，可以通过指定授权页前面一个界面的方向，授权页跟随前面界面方向，即 “android:screenOrientation="behind"”

##### 全面屏(背景延伸到状态栏)

1. 隐藏导航栏
```
setHideNavigation(true)
```
2. 设置状态栏透明(去除透明阴影)
```
setStatusBarColor(android.R.color.transparent)
```
3. 在 res/values-v21 文件夹下新建 styles.xml 添加如下主题

```
<style name="Theme.ActivityTransparentStyle" parent="Theme.AppCompat.Light.NoActionBar">
      <!--背景透明-->
      <item name="android:windowBackground">@android:color/transparent</item>
      <!--状态栏透明/有一层阴影-->
      <item name="android:windowTranslucentStatus">true</item>
      <!--导航栏透明/有一层阴影-->
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
|positionType|int|添加位置，包含 3 种类型：`UnifyUiConfig.POSITION_IN_TITLE_BAR`表示添加在导航栏中，`UnifyUiConfig.POSITION_IN_BODY`表示添加到导航栏下方的 BODY 中，`UnifyUiConfig.POSITION_IN_ROOT`表示添加到根布局中|
|listener|CustomViewListener|待添加的自定义 View 的事件监听器|

### 6. 判断运营商类型(非必须)

#### 代码说明

```
quickLogin.checkNetWork(Context context)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|context|Context|是|无| 上下文 |

#### 返回值说明

|类型|描述|
|----|----|
| int | 1：电信 2：移动 3：联通 4:wifi 5：未知 |

### 7. 设置授权页协议复选框是否选中(授权页拉起之后调用，非必须)

#### 代码说明

```
quickLogin.setPrivacyState(boolean isChecked)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|isChecked|boolean|是|true| 复选框是否选中 |

### 8. 获取授权页协议复选框是否选中(授权页拉起之后调用，非必须)

#### 代码说明

```
quickLogin.getPrivacyState()
```

### 9. 设置预取号超时时间(非必须)

#### 代码说明

```
quickLogin.setPrefetchNumberTotalTimeout(int timeout)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|timeout|int|是|8| 单位秒 |

### 10. 设置本机校验超时时间(非必须，仅联通有效)

#### 代码说明

```
quickLogin.setFetchNumberTimeout(int timeout)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|timeout|int|是|5| 单位秒 |

### 11. 返回 SDK 版本号(非必须)

#### 代码说明

```
quickLogin.getSdkVersion()
```

#### 返回值说明

|类型|描述|
|----|----|
| String | 版本号 |

### 12. 设置是否打开日志(方便排查问题，非必须)

#### 代码说明

```
quickLogin.setDebugMode(boolean debug)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|debug|boolean|是|false| 是否打印日志 |

### 13. 设置预取号携带额外参数(非必须)

#### 代码说明

```
quickLogin.setExtendData(JSONObject extendData)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|extendData|JSONObject|是|无| 额外参数 |

### 14. 清预取号缓存(非必须)

#### 代码说明

```
quickLogin.clearScripCache()
```

### 15. 移除自定义view(非必须)

#### 代码说明

```
quickLogin.removeCustomView(int positionType,View view)
```

### 16. 本机校验

在初始化之后执行，本机校验和一键登录可共用初始化，本机校验界面需自行实现

#### 代码说明

```
getToken(QuickLoginTokenListener listener)
```

#### 参数说明

|参数|类型|是否必填|默认值|描述|
|----|----|--------|------|----|
|listener|QuickLoginTokenListener|是|无| 本机校验监听器 |

#### QuickLoginTokenListener 接口说明

```
public interface QuickLoginTokenListener {
    /**
     * 获取运营商token成功
     * @param ydToken    易盾token
     * @param accessCode 运营商accessCode
     */
    void onGetTokenSuccess(String ydToken, String accessCode);

    /**
     * 获取运营商token失败
     * @param ydToken 易盾token
     * @param code    出错异常码
     * @param msg     出错提示信息
     */
    void onGetTokenError(String ydToken, int code, String msg);
}
```

## 常见报错

### 公共异常
|code|说明|
|----|--------|
| 401| 易盾业务id不准确 |
| -2 |json解析异常|
| -3 |移动运营商注入页面body失败|
| -4 |网络环境仅wifi|
| -5 |网络环境未知|
| -6 |SDK内部异常|
| -7 |Aes解密失败|

### 移动运营商异常
|code|说明|
|----|--------|
| 102101 |无网络|
| 102102 |网络异常|
| 102103 |未开启数据网络|
| 102203 |输入参数错误|
| 102223 |数据解析异常（一般是卡欠费）|
| 102508 |数据网络切换失败（从9.6.1版本开始，Android5.0以下不支持网络切换）|
| 102507 |授权登录超时|
| 103101 |请求签名错误（appkey不正确）|
| 103102 |包签名错误（appid和包签名不对应）|
| 103111 |网关IP错误（开了VPN或境外ip）|
| 103119 |appid不存在|
| 103412 |无效的请求（1.加密方式错误2.非json格式3.空请求）|
| 103414 |参数校验异常|
| 103511 |服务器ip白名单校验失败|
| 103811 |token为空|
| 103902 |scrip失效|
| 103911 |token请求过于频繁|
| 104201 |token已失效或不存在|
| 105002 |移动取号失败（一般是物联网卡）|
| 105018 |token权限不足|
| 105019 |应用未授权|
| 105021 |当天已达取号限额|
| 105302 |appid不在白名单|
| 105312 |余量不足|
| 105313 |非法请求|
| 105317 |受限用户|
| 200002 |用户未安装sim卡|
| 200010 |无法识别sim卡或没有sim卡|
| 200023 |登录超时|
| 200005 |用户未授权（READN_PHONE_STATE）|
| 200021 |数据解析异常（一般是卡欠费|
| 200022 |无网络|
| 200023 |请求超时|
| 200024 |数据网络切换失败|
| 200025 |socket、系统未授权数据蜂窝权限|
| 200026 |输入参数错误|
| 200027 |未开启数据网络或网络不稳定|
| 200028 |网络异常|
| 200038 |异网取号网络请求异常|
| 200039 |异网取号网关取号异常|
| 200040 |UI资源加载异常|
| 200050 |EOF异常|
| 200072 |CA根证书校验失败|
| 200080 |本机号码验证仅支持移动手机号|
| 200082 |服务器繁忙|

### 电信运营商异常
|code|说明|
|----|--------|
| -64 |服务器繁忙|
| -65 |服务器繁忙|
| -10001 |取号失败|
| -10002 |参数错误|
| -10003 |解密失败|
| -10004 |ip受限|
| -10005 |异网取号回调参数异常|
| -10006 |Mdn取号失败|
| -10007 |重定向到异网取号|
| -10008 |超过预设取号阈值|
| -10009 |时间戳过期|
| -20005 |sign-invalid（签名错误）|
| -20006 |应用不存在|
| -20007 |公钥数据不存在|
| -20100 |内部解析错误|
| -20102 |加密参数解析失败|
| -30001 |非法时间戳|
| -30003 |topClass失效|
| 51002 |参数为空|
| 51114 |无法获取手机号数据|
| 80000 |请求超时|
| 80001 |请求网络异常|
| 80002 |响应码错误|
| 80003 |无网络连接|
| 80004 |移动网络未打开|
| 80005 |Socket超时异常|
| 80006 |域名解析异常|
| 80007 |IO异常|
| 80100 |登录结果为空|
| 80101 |登录结果异常|
| 80102 |预登录异常|
| 80103 |SDK未初始化|
| 80104 |未调用预登录接口|
| 80105 |加载nib文件异常|
| 80800 |WIFI切换超时|
| 80801 |WIFI切换超时|

### 联通运营商异常
|code|说明|
|----|--------|
| 101001 |授权码不能为空|
| 101002 |认证的手机号不能为空|
| 101003 |UiConfig不能为空|
| 101004 |ApiKey或PublicKey不能为空|
| 101005 |超时|
| 101006 |公钥出错|
| 102001 |选择流量通道失败|
| 201001 |操作频繁，稍后再试|
| 302001 |SDK解密异常|
| 302002 |网络访问异常|
| 302003 |服务端数据格式出错|
| 111 |认证失败|
| 112 |认证失败|
| 1101 |公网ip无效|
| 1102 |私网ip无效|
| 1104 |授权码为空|
| 1105 |参数信息错误|
| 1106 |应用秘钥信息不匹配|
| 1107 |余额不足|
| 1108 |调用能力不匹配|
| 1201 |取号失败|
| 1202 |认证失败|
| 1203 |获取置换码失败|
| 2101 |鉴权失败|
| 2102 |accessCode已失效|
| 2103 |序列号不存在|
| 2201 |addid无效|
| 2202 |应用信息错误|
| 2203 |sdk信息错误|
| 2205 |接入信息解析错误|
| 2206 |流控制超限|
| 3201 |系统繁忙|
| 3202 |内部网关错误|
| 3203 |内部路由错误|
| 3205 |当前省份不支持取号|
| 3206 |取号功能暂时不可用|
