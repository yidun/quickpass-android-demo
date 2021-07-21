一键登录 Android SDK 接入指南
===
## 1 概览

### 1.1 环境要求

| 条目        | 说明                                                         |
| ----------- | ------------------------------------------------------------ |
| 适配版本    | minSdkVersion 16及以上                                              |
| 网络制式    | 支持移动2G/3G/4G/5G<br>联通3G/4G/5G<br>电信4G/5G<br>2G、3G因网络环境问题，成功率低于4G。 |
| 网络环境    | - 打开蜂窝网络且赋予其权限<br>- 蜂窝网络+WIFI同开<br>- <font color=red>双卡手机，取当前发流量的卡号</font> |



### 1.2 开发说明

| 条目        | 说明           |
| ----------- | -------------- |
| 产品流程图  | [交互时序图](http://support.dun.163.com/documents/287305921855672320?docId=288803165532508160&locale=zh-cn) |
| SDK资源包   | [去下载](http://support.dun.163.com/documents/287305921855672320?docId=289905327964606464&locale=zh-cn)     |
| 常见问题    | [常见问题](http://support.dun.163.com/documents/287305921855672320?docId=320640624725512192&locale=zh-cn)   |
| SDK当前版本 | 3.0.6.6          |



### 1.3 业务场景详述

| 业务场景 | 说明                                                         |
| -------- | ------------------------------------------------------------ |
| 一键登录 | 用户无需输入手机号码，只需集成并调用SDK拉起授权页方法<br>用户确认授权后，SDK会获取token<br/>服务端携带token到运营商网关获取用户当前上网使用的流量卡号码，并返回给APP服务端 |
| 本机校验 | 用户输入手机号码<br/>服务端携带手机号码和token去运营商网关进行校验比对<br/>返回的校验结果为：用户当前流量卡号码与服务端携带的手机号码是否一致 |


## 2 SDK集成
### 方式一
#### 从3.0.4版本开始，提供远程依赖的方式，本地依赖的方式逐步淘汰。原先以本地依赖集成的想尝试远程依赖请先去除干净本地包，避免重复依赖冲突
确认Android Studio的Project根目录的主gradle中配置了 mavenCentral 支持

```
 buildscript {
            repositories {
                jcenter()
                mavenCentral()
            }
            ......
        }

        allprojects {
            repositories {
                jcenter()
                mavenCentral()
            }
        }
```
在对应module的gradle 中添加依赖
```
implementation 'io.github.yidun:quicklogin:3.0.7'
```
**<font color = red>NOTE：为了解决外抛onActivityResult，从3.0.6开始移动原先的类LoginAuthActivity中转到CmccLoginActivity。如是之前为了Activity的样式覆盖过theme，现在请覆盖CmccLoginActivity</font>**
```
以前
 <activity
            android:name="com.cmic.sso.wy.activity.LoginAuthActivity"
            android:theme="@style/Theme.ActivityTransparentStyle"
            tools:replace="theme" />
现在
 <activity
            android:name="com.netease.nis.quicklogin.ui.CmccLoginActivity"
            android:theme="@style/Theme.ActivityTransparentStyle"
            tools:replace="theme" />
```

### 方式二
将从官网下载下来的一键登录aar包放到项目的libs目录下，然后在模块的build.gradle中的dependencies添加相关依赖

示例：

```
dependencies {
    implementation(name: 'quicklogin-external-release-3.0.4', ext: 'aar') // aar包具体名称请以官网下载下来为准
    implementation(name: 'CMCCSSOSDK-WY-release', ext: 'aar')
    implementation(name: 'Ui-factory_oauth_mobile_4.0.3', ext: 'aar')
    implementation(name: 'CTAccount_sdk_api_v3.8.3_all_wy', ext: 'aar')
    implementation(name: 'base-sdk-libary-release', ext: 'aar')
    implementation 'com.google.code.gson:gson:2.8.5'    // 配置对gson的依赖
}
```
**<font color = red>NOTE：为了避免不同Android Studio版本对依赖库资源替换的顺序不同导致的一些问题，请将`quicklogin-external-release`的依赖放到上述其他依赖库的最前面</font>**

然后在app的build.gradle的android下添加

```
 repositories {
        flatDir {
            dirs 'libs'
        }
    }
```

## 3 SDK接口
本机校验和一键登录功能的提供类，主要提供获取单例，预取号，本机校验/一键登录，设置预取url等接口
### 3.1 获取QuickLogin单例

```
QuickLogin login = QuickLogin.getInstance(getApplicationContext(), BUSINESS_ID);// BUSINESS_ID为从易盾官网申请的业务id
```

### 3.2 预取号<font color = red>（一键登录前请务必先调用该接口获取手机掩码）</font>
<font color=red>使用场景建议：</font>

- **<font color=red>用户处于未登录状态时，调用该方法</font>**
- **<font color=red>已登录的用户退出当前帐号时，调用该方法</font>**
- <font color=red>在执行一键登录的方法之前，提前调用此方法，以提升用户前端体验</font>
- <font color=red>此方法需要1~2s的时间取得临时凭证，不要和拉起授权页方法一起串行调用</font>
- <font color=red>不要频繁的多次调用</font>
- <font color=red>不要在拉起授权页后调用</font>

```
login.prefetchMobileNumber(new QuickLoginPreMobileListener() {
        @Override
        public void onGetMobileNumberSuccess(String YDToken, final String mobileNumber) {
         // 注:2.0.0及以后版本，直接在该回调中调用取号接口onePass即可
        }

        @Override
        public void onGetMobileNumberError(String YDToken, final String msg) {
        
        }
    });
```
### 3.3 一键登录

<font color = red>调用一键登录接口前请务必调用预取号接口，在预取号接口的成功回调中调用一键登录接口，获取运营商授权码与易盾token，5秒超时  </font><br>
API定义：

```
 /**
 * 一键登录功能，使用该接口前需要先调用prefetchMobileNumber接口进行预取号
 *
 * @param listener 回调监听器
 */
public void onePass(final QuickLoginTokenListener listener)
```
使用示例

```
login.onePass(new QuickLoginTokenListener() {
    @Override
    public void onGetTokenSuccess(final String YDToken, final String accessCode) {
        Log.d(TAG, String.format("yd token is:%s accessCode is:%s", YDToken, accessCode));
        tokenValidate(YDToken, accessCode, true);
    }

    @Override
    public void onGetTokenError(String YDToken, String msg) {
        Log.d(TAG, "获取运营商授权码失败:" + msg);
    }
});

```
<font color=red>使用场景建议：</font>

- <font color=red>在预取号成功后调用</font>
- <font color=red>已登录状态不要调用</font>
### 3.4 本机校验
API定义：
```
public void getToken(final String mobileNumber, final QuickLoginTokenListener listener)
```
第一个参数表示用户输入的进行本机校验的手机号码，第二个参数是获取token的回调监听器    
使用示例：

```
  login.getToken(mobileNumber, new QuickLoginTokenListener() {
            @Override
            public void onGetTokenSuccess(final String YDToken, final String accessCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "获取Token成功" + YDToken + accessCode);
                        tokenValidate(YDToken, accessCode, false);
                    }
                });
            }

            @Override
            public void onGetTokenError(final String YDToken, final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "获取Token失败" + YDToken + msg, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
```
### 3.5 判断运营商类型

```
/**
* @param context
* @return int类型，标识运营商类型，具体含义如下
* 1:      电信
* 2:      移动
* 3:      联通
* 5:      未知
*/
public int getOperatorType(Context context) 
```
### 3.6 退出授权页

SDK从v2.2.6版本开始，调用onePass接口后不再主动关闭授权页，接入者可根据自己业务需求在合适时机调以下接口来主动关闭授权页

```
public void quitActivity() // 退出一键登录页面
```

### 3.7 其他接口

```
public void setUnifyUiConfig(UnifyUiConfig uiConfig) // 设置一键登录页面自定义属性，详细可配置信息{@link UnifyUiConfig#Builder}
public void setPrefetchNumberTimeout(int timeout) // 设置预取号超时时间，单位s
public void setFetchNumberTimeout(int timeout)    // 设置取号超时时间，单位s

public void setPreCheckUrl(String url) // 设置预取url接口，一般无需设置，当开发者希望接管preCheck逻辑时可设置代理preCheck Url，具体规则请查看易盾一键登录后端接入说明文档
public void setExtendData(JSONObject extendData) // 设置扩展数据，一般无需设置
public boolean onExtendMsg(JSONObject extendMsg) // 当用户自定义预取Url后，如果在自己业务后端判断调用非法，可直接调用该接口返回false实现快速降级，以及获取自己业务后端处理后返回的数据
public void setPrivacyState(boolean isChecked) //api自动设置协议复选框的选中状态
public void quitActivity() // 退出一键登录页面

```



### 3.7 预取号回调监听-QuickLoginPreMobileListener
预取号的回调监听器，接入者需要实现该接口的如下2个抽象方法

```
public abstract class QuickLoginPreMobileListener implements QuickLoginListener {
     /**
     * @param YDToken      易盾Token
     * @param mobileNumber 获取的手机号码掩码
     */
    void onGetMobileNumberSuccess(String YDToken, String mobileNumber);

    /**
     * @param YDToken 易盾Token
     * @param msg     获取手机号掩码失败原因
     */
    void onGetMobileNumberError(String YDToken, String msg);
    
     /**
     * 业务方自定义preCheck后，业务方扩展字段的回调，
     * 返回false表示业务方希望中断sdk后续流程处理，直接降级
     *
     * @param extendMsg
     * @return 返回true表示继续后续处理，返回false表示业务方希望降级终止后续处理，默认返回true
     */
     boolean onExtendMsg(JSONObject extendMsg);
}
```
### 3.8 运营商token回调监听-QuickLoginTokenListener
一键登录或本机校验的获取运营商accessToken的回调监听器，接入者需要实现该接口的如下2个抽象方法

```
public abstract class QuickLoginTokenListener implements QuickLoginListener {
   /**
     * @param YDToken    易盾token
     * @param accessCode 运营商accessCode
     */
    void onGetTokenSuccess(String YDToken, String accessCode);

    /**
     * @param YDToken 易盾token
     * @param msg     出错提示信息
     */
    void onGetTokenError(String YDToken, String msg);
    
     /**
     * 业务方自定义PreCheck后，业务方扩展字段的回调，
     * 返回false表示业务方希望中断sdk后续流程处理，直接降级
     *
     * @param extendMsg
     * @return 返回true表示继续后续处理，返回false表示业务方希望降级终止后续处理，默认返回true
     */
    boolean onExtendMsg(JSONObject extendMsg);
}
```

## 4 SDK使用步骤
- 获取QuickLogin对象实例

```
QuickLogin login = QuickLogin.getInstance(getApplicationContext(), BUSINESS_ID);
```

- 根据本机校验或一键登录需求调用对应的接口

<font color = red>**NOTE: 以下回调接口有可能来自子线程回调，如果您要在回调中修改UI状态，请在回调中抛到主线程中处理，如像Demo示例那样使用runOnUiThread API**</font>

### 4.1 一键登录
#### 4.1.1 调用prefetchMobileNumber接口预取号

```
login.prefetchMobileNumber(new QuickLoginPreMobileListener() {
        @Override
        public void onGetMobileNumberSuccess(String YDToken, final String mobileNumber) {
        // 在该成功回调中直接调用onePass接口进行一键登录即可
        }

        @Override
        public void onGetMobileNumberError(String YDToken, final String msg) {
        //  在该错误回调中能够获取到此次请求的易盾token以及预取号获取手机掩码失败的原因
        }
        @Override
        public boolean onExtendMsg(JSONObject extendMsg) {
           Log.d(TAG, "获取的扩展字段内容为:" + extendMsg.toString());
           // 如果接入者自定义了preCheck接口，可在该方法中通过返回true或false来控制是否快速降级
           return super.onExtendMsg(extendMsg);
        }
    });
```
#### 4.1.2 调用onePass一键登录

```
login.onePass(new QuickLoginTokenListener() {
    @Override
    public void onGetTokenSuccess(final String YDToken, final String accessCode) {
        Log.d(TAG, String.format("yd token is:%s accessCode is:%s", YDToken, accessCode));
        // 在一键登录获取token的成功回调中使用易盾token和运营商token去做token的验证，具体验证规则请参看服务端给出的说明文档
        tokenValidate(YDToken, accessCode, true);
    }

    @Override
    public void onGetTokenError(String YDToken, String msg) {
        Log.d(TAG, "获取运营商token失败:" + msg);
        // 一键登录获取token失败的回调
    }
});
```
### 4.2 本机校验


```
// 本机校验获取token
login.getToken(mobileNumber, new QuickLoginTokenListener() {
    @Override
    public void onGetTokenSuccess(final String YDToken, final String accessCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "获取Token成功" + YDToken + accessCode, Toast.LENGTH_LONG).show();
                Log.d(TAG, "获取Token成功" + YDToken + accessCode);
                tokenValidate(YDToken, accessCode, false);
            }
        });

    }

    @Override
    public void onGetTokenError(final String YDToken, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "获取Token失败" + YDToken + msg, Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public boolean onExtendMsg(JSONObject extendMsg) {
        Log.d(TAG, "获取的扩展字段内容为:" + extendMsg.toString());
        // 如果接入者自定义了preCheck接口，可在该方法中通过返回true或false来控制是否快速降级
        return super.onExtendMsg(extendMsg);
    }
});
```
### 4.3 使用自定义preCheck接口与扩展字段功能
如果接入者需要接管preCheck过程做自己的一些业务逻辑，可以使用如下方式
```
login.setPreCheckUrl(customUrl); // 使用自定义url代理preCheck接口
JSONObject extData = new JSONObject();
        try {
            extData.put("parameter1", "param1");
            extData.put("parameter2", "param2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
login.setExtendData(extData); // 如果自定义url需要接受一些自己的业务参数，通过该接口进行设置
```
## 5 授权页面属性自定义
### 5.1 设计规范
**<font color=red>开发者不得通过任何技术手段，将授权页面的隐私栏、手机掩码号、供应商品牌内容隐藏、覆盖</font>**<br>
**<font color=red>网易易盾与运营商会对应用授权页面进行审查，若发现上述违规行为，网易易盾有权将您的一键登录功能下线</font>**
![安卓规范示意图](https://nos.netease.com/cloud-website-bucket/fc608fc8c376e8b384e947e575ef8b5f.jpg)
![自定义展示图](https://nos.netease.com/cloud-website-bucket/410d6012173c5531b1065909c9484d36.jpg)

### 5.2 UI设置接口

```
public void setUnifyUiConfig(UnifyUiConfig uiConfig)
```

uiConfig表示3网统一的UI配置对象，以下是一个简单示例：

```
QuickLogin.getInstance(getApplicationContext(),onePassId).setUnifyUiConfig(QuickLoginUiConfig.getUiConfig(getApplicationContext()));
```

详细UI配置示例可参考Demo代码示例中QuickLoginUiConfig类代码

### 5.3 可配置元素及其接口

**注意：**

<font color = red>- 以下所有API接口中涉及到图片，样式等资源名称的形参，均表示资源名，且该资源需要放置在drawable目录下</font><br>以设置导航栏图标接口为例：

  ```
  setNavigationIcon(String backIcon) 
  ```

  假设在drawable目录下有back_icon.jpg，则该值为"back_icon"

<font color = red>- 以下所有API接口中如果涉及到顶部偏移和底部偏移的接口，顶部都是相对标题栏底部而言，底部都是相对屏幕底部而言</font>

### 5.4 状态栏

| 方法                                              | 说明                                   |
| ------------------------------------------------- | -------------------------------------- |
| setStatusBarColor(int statusBarColor)             | 设置状态栏颜色                         |
| setStatusBarDarkColor(boolean statusBarDarkColor) | 设置状态栏字体图标颜色是否为暗色(黑色) |

### 5.5 导航栏

| 方法                                              | 说明                                                         |
| :------------------------------------------------ | ------------------------------------------------------------ |
| setNavigationIcon(String backIcon)                | 设置导航栏返回图标，backIcon 导航栏图标名称，需要放置在drawable目录下， |
| setNavigationBackIconWidth(int backIconWidth)     | 设置导航栏返回图标的宽度                                     |
| setNavigationBackIconHeight(int backIconHeight)   | 设置导航栏返回图标的高度                                     |
| setHideNavigationBackIcon(boolean isHideBackIcon) | 设置隐藏导航栏返回按钮                                       |
| setNavigationBackgroundColor(int backgroundColor) | 设置导航栏背景颜色                                           |
| setNavigationHeight(int navHeight)                | 设置导航栏高度，单位dp                                       |
| setNavigationTitle(String title)                  | 设置导航栏标题                                               |
| setNavigationTitleColor(int titleColor)           | 设置导航栏标题颜色                                           |
| setNavTitleSize(int navTitleSize)                 | 设置导航栏标题大小，单位sp                                   |
| setNavTitleDpSize(int navTitleDpSize)             | 设置导航栏标题大小，单位dp                                   |
| setNavTitleBold(boolean navTitleBold)             | 设置导航栏标题是否为粗体                                     |
| setHideNavigation(boolean isHideNavigation)       | 设置是否隐藏导航栏                                           |

### 5.6 应用Logo

| 方法                                        | 说明                                                         |
| :------------------------------------------ | ------------------------------------------------------------ |
| setLogoIconName(String logoIconName)        | 设置应用logo图标，logoIconName：logo图标名称，需要放置在drawable目录下 |
| setLogoWidth(int logoWidth)                 | 设置应用logo宽度，单位dp                                     |
| setLogoHeight(int logoHeight)               | 设置应用logo高度，单位dp                                     |
| setLogoTopYOffset(int logoTopYOffset)       | 设置logo顶部Y轴偏移，单位dp                                  |
| setLogoBottomYOffset(int logoBottomYOffset) | 设置logo距离屏幕底部偏移，单位dp                             |
| setLogoXOffset(int logoXOffset)             | 设置logo水平方向的偏移，单位dp                               |
| setHideLogo(boolean hideLogo)               | 设置是否隐藏Logo                                             |

### 5.7 手机掩码

| 方法                                                         | 说明                                                         |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| setMaskNumberColor(int maskNumberColor)                      | 设置手机掩码颜色                                             |
| setMaskNumberSize(int maskNumberSize)                        | 设置手机掩码字体大小，单位sp                                 |
| setMaskNumberXOffset(int maskNumberXOffset)                  | 设置手机掩码水平方向的偏移，单位dp                           |
| setMaskNumberDpSize(int maskNumberDpSize)                    | 设置手机掩码字体大小，单位dp                                 |
| setMaskNumberTypeface(Typeface tf)                           | 设置手机掩码字体                                           |
| setMaskNumberBottomYOffset(int maskNumberBottomYOffset)      | 设置手机掩码距离屏幕底部偏移，单位dp                         |
| setMaskNumberXOffset(int maskNumberXOffset)                  | 设置手机掩码水平方向的偏移，单位dp                           |
| setMaskNumberListener(MaskNumberListener maskNumberListener) | 设置手机掩码自定义监听器，用于对手机掩码栏实现自定义功能（可参见Demo示例工程） |

### 5.8 认证品牌

| 方法                                            | 说明                                 |
| :---------------------------------------------- | ------------------------------------ |
| setSloganSize(int sloganSize)                   | 设置认证品牌字体大小，单位sp         |
| setSloganDpSize(int sloganDpSize)               | 设置认证品牌字体大小，单位dp         |
| setSloganColor(int sloganColor)                 | 设置认证品牌颜色                     |
| setSloganTopYOffset(int sloganTopYOffset)       | 设置认证品牌顶部Y轴偏移，单位dp      |
| setSloganBottomYOffset(int sloganBottomYOffset) | 设置认证品牌距离屏幕底部偏移，单位dp |
| setSloganXOffset(int sloganXOffset)             | 设置认证品牌水平方向的偏移，单位dp   |

### 5.9 登录按钮

| 方法                                                   | 说明                                                 |
| :----------------------------------------------------- | ---------------------------------------------------- |
| setLoginBtnText(String loginBtnText)                   | 设置登录按钮文本                                     |
| setLoginBtnTextSize(int loginBtnTextSize)              | 设置登录按钮文本字体大小，单位sp                     |
| setLoginBtnTextDpSize(int loginBtnTextDpSize)          | 设置登录按钮文本字体大小，单位dp                     |
| setLoginBtnTextColor(int loginBtnTextColor)            | 设置登录按钮文本颜色                                 |
| setLoginBtnWidth(int loginBtnWidth)                    | 设置登录按钮宽度，单位dp                             |
| setLoginBtnHeight(int loginBtnHeight)                  | 设置登录按钮高度，单位dp                             |
| setLoginBtnBackgroundRes(String loginBtnBackgroundRes) | 设置登录按钮背景资源，该资源需要放置在drawable目录下 |
| setLoginBtnTopYOffset(int loginBtnTopYOffset)          | 设置登录按钮顶部Y轴偏移，单位dp                      |
| setLoginBtnBottomYOffset(int loginBtnBottomYOffset)    | 设置登录按钮距离屏幕底部偏移，单位dp                 |
| setLoginBtnXOffset(int loginBtnXOffset)                | 设置登录按钮水平方向的偏移，单位dp                   |

### 5.10 隐私协议

| 方法                                                         | 说明                                                         |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| setPrivacyTextColor(int privacyTextColor)                    | 设置隐私栏文本颜色，不包括协议 ，如若隐私栏协议文案为：登录即同意《中国移动认证条款》且授权QuickLogin登录， 则该API对除协议‘《中国移动认证条款》’区域外的其余文本生效 |
| setPrivacyProtocolColor(int privacyProtocolColor)            | 设置隐私栏协议颜色 。例如：登录即同意《中国移动认证条款》且授权QuickLogin登录 ， 则该API仅对‘《中国移动认证条款》’文案生效 |
| setPrivacySize(int privacySize)                              | 设置隐私栏区域字体大小，单位sp                               |
| setPrivacyDpSize(int privacyDpSize)                          | 设置隐私栏区域字体大小，单位dp                               |
| setPrivacyTopYOffset(int privacyTopYOffset)                  | 设置隐私栏顶部Y轴偏移，单位dp                                |
| setPrivacyBottomYOffset(int privacyBottomYOffset)            | 设置隐私栏距离屏幕底部偏移，单位dp                           |
| setPrivacyTextMarginLeft(int privacyTextMarginLeft)          | 设置隐私栏复选框和文字内边距，单位dp                             |
| setPrivacyMarginLeft(int privacyMarginLeft)                  | 设置隐私栏水平方向的偏移，单位dp                             |
| setPrivacyMarginRight(int privacyMarginRight)                | 设置隐私栏右侧边距，单位dp                                   |
| setPrivacyState(boolean privacyState)                        | 设置隐私栏协议复选框勾选状态，true勾选，false不勾选          |
| setHidePrivacyCheckBox(boolean hidePrivacyCheckBox)          | 设置是否隐藏隐私栏勾选框                                     |
| setCheckBoxGravity(int checkBoxGravity)                      | 设置隐私栏勾选框与文本协议对齐方式，可选择顶部（Gravity.TOP），居中（Gravity.CENTER），底部（Gravity.BOTTOM）等 |
| setPrivacyTextGravityCenter(boolean privacyTextGravityCenter | 设置隐私栏文案换行后是否居中对齐，如果为true则居中对齐，否则左对齐 |
| setPrivacyCheckBoxWidth(int privacyCheckBoxWidth)            | 设置隐私栏复选框宽度，单位dp   |
| setPrivacyCheckBoxHeight(int privacyCheckBoxHeight)          | 设置隐私栏复选框高度，单位dp   |
| setCheckedImageName(String checkedImageName)                 | 设置隐私栏复选框选中时的图片资源，该图片资源需要放到drawable目录下 |
| setUnCheckedImageName(String unCheckedImageName)             | 设置隐私栏复选框未选中时的图片资源，该图片资源需要放到drawable目录下 |
| setPrivacyTextStart(String privacyTextStart)                 | 设置隐私栏声明部分起始文案 。如：隐私栏声明为"登录即同意《隐私政策》和《中国移动认证条款》且授权易盾授予本机号码"，则可传入"登录即同意" |
| setPrivacyTextStartSize(float spVal)                         | 设置隐私开始字体大小                                         |
| setPrivacyLineSpacing(float add,float mult)                  | 设置隐私行间距       add行间距 mult倍数                       |
| setProtocolText(String protocolText)                         | 设置隐私栏协议文本                                           |
| setProtocolLink(String protocolLink)                         | 设置隐私栏协议链接                                           |
| setProtocol2Text(String protocol2Text)                       | 设置隐私栏协议2文本                                          |
| setProtocol2Link(String protocol2Link)                       | 设置隐私栏协议2链接                                          |
| setPrivacyTextEnd(String privacyTextEnd)                     | 设置隐私栏声明部分尾部文案。如：隐私栏声明为"登录即同意《隐私政策》和《中国移动认证条款》且授权易盾授予本机号码"，则可传入"且授权易盾授予本机号码" |

### 5.11 协议详情Web页面导航栏

| 方法                                                         | 说明                                                         |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| setProtocolPageNavTitle(String protocolNavTitle)             | 设置协议Web页面导航栏标题，如果需要根据不同运营商设置不同标题，可使用下面重载接口 |
| setProtocolPageNavTitle(String cmProtocolNavTitle, String cuProtocolNavTitle, String ctProtocolNavTitle) | 设置协议Web页面导航栏标题，可针对不同运营商单独设置          |
| setProtocolPageNavTitle(String cmProtocolNavTitle, String cuProtocolNavTitle,                                        String ctProtocolNavTitle, String protocolTextNavTitle, String protocol2TextNavTitle) | 设置协议Web页面导航栏标题，可根据运营商类型单独设置不同标题，也可针对自身协议设置不同标题 |
| setProtocolPageNavTitleColor(int protocolNavTitleColor)      | 设置协议Web页面导航栏标题颜色                                |
| setProtocolPageNavBackIcon(String protocolNavBackIcon)       | 设置协议Web页面导航栏返回图标                                |
| setProtocolPageNavColor(int protocolNavColor)                | 设置协议Web页面导航栏颜色                                    |
| setProtocolPageNavHeight(int protocolNavHeight)              | 设置协议Web页面导航栏高度                                    |
| setProtocolPageNavTitleSize(int protocolNavTitleSize)        | 设置协议Web页面导航栏标题大小，单位sp                        |
| setProtocolPageNavTitleDpSize(int protocolNavTitleDpSize)    | 设置协议Web页面导航栏标题大小，单位dp                        |
| setProtocolPageNavBackIconWidth(int protocolNavBackIconWidth) | 设置协议Web页面导航栏返回按钮宽度，单位dp                    |
| setProtocolPageNavBackIconHeight(int protocolNavBackIconHeight) | 设置协议Web页面导航栏返回按钮高度，单位dp                    |


### 5.12 其它

| 方法                                                         | 说明                                                         |
| :----------------------------------------------------------- | ------------------------------------------------------------ |
| setBackgroundImage(String backgroundImage)                   | 设置登录页面背景，图片资源需放置到drawable目录下             |
| setBackgroundGif(String backgroundGif)                       | 设置登录页面背景为Gif，Gif资源需要放置到drawable目录下，传入资源名称即可 |
| setBackgroundVideo(String videoPath, String videoImage)      | 设置登录页面背景为视频，参数videoPath为背景Video文件路径:(支持本地路径如："android.resource://" + context.getPackageName() + "/" + R.raw.xxxVideo；支持网络路径如"https://xxx"(建议下载到本地后使用本地路径，网络路径由于网络环境的不可控体验不如直接加载本地视频)，参数videoImage为视频播放前的背景图片(需要放置到drawable文件中，传入图片名称即可)，2个参数必须都设置 |
| setBackgroundShadowView(View view)                           | 设置登录页面背景蒙层View，设置的View将位于背景之上其它控件之下。必须配合视频背景使用 |
| setLoginListener(LoginListener loginListener)                | 设置未同意隐私协议但点击一键登录按钮时的事件监听器，可用于自定义相关提示信息，使用示例可参看Demo示例工程 |
| setClickEventListener(ClickEventListener clickEventListener) | 设置授权页面点击事件监听，包括左上角返回按钮，登录按钮，隐私协议复选框，隐私协议。当这4类元素被点击时会回调传入的ClickEventListener的onClick(int viewType, int code)接口，viewType为1时表示隐私协议，2表示复选框，3表示左上角返回按钮，4表示登录按钮。当viewType为2或4时，code字段为1则表示复选框勾选，为0则表示复选框未勾选 |
| setActivityTranslateAnimation(String enterAnimation, String exitAnimation) | 设置授权页进场与出场动画，enterAnimation进场动画xml无后缀文件名，exitAnimation出场动画xml无后缀文件名，如进场动画文件名为activity_enter_anim.xml，则进场动画参数传入“activity_enter_anim" |
| setActivityLifecycleCallbacks(ActivityLifecycleCallbacks Callbacks)      | 设置登录页面生命周期监听回调 |
| setActivityResultCallbacks(ActivityResultCallbacks callbacks)      | 设置登录页面onActivityResult回调 |

## 6. 弹窗模式与横竖屏设置

### 6.1 弹窗模式

```
setDialogMode(boolean isDialogMode, int dialogWidth, int dialogHeight, int dialogX, int dialogY, boolean isBottomDialog)
```

各参数及其意义如下：

- isDialogMode：是否开启对话框模式，true开启，false关闭
- dialogWidth：对话框宽度
- dialogHeight：对话框高度
- dialogX：当弹窗模式为中心模式时，弹窗X轴偏移（以屏幕中心为基准）
- dialogY：当弹窗模式为中心模式时，弹窗Y轴偏移（以屏幕中心为基准）
- isBottomDialog：是否为底部对话框模式，true则为底部对话框模式，否则为中心模式

**注意：** 设置弹窗效果背景透明度则需要在AndroidManifest.xml中配置授权界面样式，如下是一个简单示例：

1. 为授权界面的activity设置弹窗theme主题，以移动登录界面为例：

   ```
   <activity
      android:name="com.cmic.sso.sdk.activity.LoginAuthActivity"
      android:configChanges="keyboardHidden|orientation|screenSize"
      android:launchMode="singleTop"
      android:screenOrientation="behind"
      android:theme="@style/Theme.ActivityDialogStyle"/>
   ```

2. 设置theme主题的style样式

   ```
   <style name="Theme.ActivityDialogStyle" parent="Theme.AppCompat.Light.NoActionBar">
       <!--背景透明-->
       <item name="android:windowBackground">@android:color/transparent</item>
       <item name="android:windowIsTranslucent">true</item>
       <!--dialog的整个屏幕的背景是否有遮障层-->
       <item name="android:backgroundDimEnabled">true</item>
   </style>
   ```

### 6.2 横竖屏设置

```
setLandscape(boolean landscape) // 设置是否为横屏模式，默认竖屏
```

如果要设置为横屏，请在清单文件中对登录页面Activity配置`android:configChanges="keyboardHidden|orientation|screenSize"`，如下是简单示例：

```
       <!--移动登录类名-->
        <activity
            android:name="com.cmic.sso.wy.activity.LoginAuthActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:launchMode="singleTop"
            android:screenOrientation="behind"
            android:theme="@style/Theme.ActivityDialogStyle"
            tools:replace="android:screenOrientation,android:configChanges" />
        <!--联通登录类名-->
        <activity
            android:name="com.sdk.mobile.manager.login.cucc.OauthActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:launchMode="singleTop"
            android:screenOrientation="behind"
            android:theme="@style/Theme.ActivityDialogStyle"
            tools:replace="android:screenOrientation,android:theme" />
        <!--电信登录类名-->
        <activity
            android:name="com.netease.nis.quicklogin.ui.YDQuickLoginActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:launchMode="singleTop"
            android:screenOrientation="behind"
            android:theme="@style/Theme.ActivityDialogStyle" />
```

<font color = red>**注意:** 当开发者项目targetSdkVersion指定为26以上时，**只有全屏不透明的Activity才能设置方向**，否则在8.0系统版本上会出现Only fullscreen opaque activities can request orientation异常</font>



## 7. 自定义控件

### 7.1 接口

```
addCustomView(View customView, String viewId, int positionType, LoginUiHelper.CustomViewListener listener
```

各参数及其意义如下：

- customView：待添加自定义View对象
- viewId：待添加自定义View的id
- positionType：添加位置，包含2种类型：`UnifyUiConfig.POSITION_IN_TITLE_BAR`表示添加在标题栏中，`UnifyUiConfig.POSITION_IN_BODY`表示添加到标题栏下方的BODY中
- listener：待添加的自定义View的事件监听器

### 7.2 示例

```
 // 创建相关自定义View
 ImageView closeBtn = new ImageView(context);
 closeBtn.setImageResource(R.drawable.close);
 closeBtn.setScaleType(ImageView.ScaleType.FIT_XY);
 closeBtn.setBackgroundColor(Color.TRANSPARENT);
 RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(50, 50);
 layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.CENTER_VERTICAL);
 layoutParams.topMargin = 30;
 layoutParams.rightMargin = 50;
 closeBtn.setLayoutParams(layoutParams);

LayoutInflater inflater = LayoutInflater.from(context);
RelativeLayout otherLoginRel = (RelativeLayout) inflater.inflate(R.layout.custom_other_login, null);
RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
layoutParamsOther.setMargins(0, 0, 0, Utils.dip2px(context, 130));
layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
layoutParamsOther.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
otherLoginRel.setLayoutParams(layoutParamsOther);

// 使用添加自定义View接口将其添加到登录界面
UnifyUiConfig uiConfig = new UnifyUiConfig.Builder()
          .addCustomView(otherLoginRel, "relative", UnifyUiConfig.POSITION_IN_BODY, null)
          .addCustomView(closeBtn, "close_btn", UnifyUiConfig.POSITION_IN_TITLE_BAR, new         	  				LoginUiHelper.CustomViewListener() {
           			 @Override
                	  public void onClick(Context context, View view) {
                   		    Toast.makeText(context, "点击了右上角X按钮", Toast.LENGTH_SHORT).show();
                      }
           })
           .build(context);
```

## 8 监听用户取消一键登录

在调用onePass接口时传入的QuickLoginTokenListener回调参数中重写onCancelGetToken方法，该方法即表示用户放弃一键登录

```
 login.onePass(new QuickLoginTokenListener() {
    @Override
    public void onGetTokenSuccess(final String YDToken, final String accessCode) {
        Log.d(TAG, String.format("yd token is:%s accessCode is:%s", YDToken, accessCode));
    }
    
    @Override
    public void onGetTokenError(String YDToken, String msg) {
        Log.d(TAG, "获取运营商token失败:" + msg);
    }

    @Override
    public void onCancelGetToken() {
        Log.d(TAG, "用户取消登录");
    }
 });
```



## 9 防混淆配置

```
-dontwarn com.cmic.sso.sdk.**
-dontwarn com.cmic.sso.wy.**
-keep public class com.cmic.sso.sdk.**{*;}
-keep class cn.com.chinatelecom.account.api.**{*;}
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
-dontwarn com.sdk.**
-keep class com.sdk.** { *;}
-dontwarn com.netease.nis.basesdk.HttpUtil
```

## 10 常见问题

### 10.1 联通常见问题

- 预取号返回的错误信息为"公网IP无效"

答：联通返回公网IP无效一般是如下几类原因导致：

  1. 用户未开启数据流量，仅使用wifi访问(包含虽然开启了数据流量，但因欠费等原因实际上等同于未开启)：

  ​       解决：开启数据流量即可

  2. 用户虽然使用的是数据流量，但是是以wap方式访问的：

  ​       解决：在手机的设置中将网络切换到3gnet接口，具体路径：设置→数据流量→APN切换到3gnet就行了 


### 10.2 体验Demo下载

扫描二维码下载体验Demo
![cu](https://nos.netease.com/cloud-website-bucket/172420806df9c24d3aecc3ff9e661f88.png)

### 10.3 Demo代码示例

[Demo工程](https://github.com/yidun/quickpass-android-demo/tree/master/Demo)
