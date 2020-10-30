package nis.netease.com.quickpassdemo.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.netease.nis.quicklogin.helper.UnifyUiConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hzhuqi on 2020/1/2
 */
public class JsonConfigParser {
    private String navBackIcon;
    private int navBackIconWidth = 25;
    private int navBackIconHeight = 25;
    private int navBackgroundColor;
    private String navTitle;
    private int navTitleColor;
    private boolean isHideNav = false;
    private String logoIconName;
    private int logoWidth;
    private int logoHeight;
    private int logoTopYOffset;
    private int logoBottomYOffset;
    private int logoXOffset;
    private boolean isHideLogo = false;
    private int maskNumberColor;
    private int maskNumberSize;
    private int maskNumberTopYOffset;
    private int maskNumberBottomYOffset;
    private int maskNumberXOffset;
    private int sloganSize = 10;
    private int sloganColor = Color.BLUE;
    private int sloganTopYOffset;
    private int sloganBottomYOffset;
    private int sloganXOffset;
    private String loginBtnText = "本机号码一键登录";
    private int loginBtnTextSize = 15;
    private int loginBtnTextColor = -1;
    private int loginBtnWidth;
    private int loginBtnHeight;
    private String loginBtnBackgroundRes;
    private int loginBtnTopYOffset;
    private int loginBtnBottomYOffset;
    private int loginBtnXOffset;
    private int privacyTextColor = Color.BLACK;
    private int privacyProtocolColor = Color.GRAY;
    private int privacySize;
    private int privacyTopYOffset;
    private int privacyBottomYOffset;
    private int privacyXOffset;
    private boolean privacyState = true;
    private boolean isHidePrivacyCheckBox = false;
    private boolean isPrivacyTextGravityCenter = false;
    private String checkedImageName = "yd_checkbox_checked";
    private String unCheckedImageName = "yd_checkbox_unchecked";
    private String privacyTextStart = "登录即同意";
    private String protocolText;
    private String protocolLink;
    private String protocol2Text;
    private String protocol2Link;
    private String privacyTextEnd = "且授权使用本机号码登录";
    private String protocolNavTitle;
    private String protocolNavBackIcon;
    private int protocolNavColor;
    private boolean isDialogMode;
    private int dialogWidth;
    private int dialogHeight;
    private int dialogX;
    private int dialogY;
    private boolean isBottomDialog;
    /**
     * 从服务器拉取UI配置文件，便于自动化测试
     */
    private boolean fetchConfigFromNet = true;
    private String configUrl = "http://ye-test.dun.163yun.com/config.json";

    void parser(String content) throws JSONException {
        // 以下操作用getXX不用optXX防止QA修改配置文件误删除某些选项而未测到
        JSONObject jsonObject = new JSONObject(content);
        navBackIcon = jsonObject.getString("navBackIcon");
        navBackIconWidth = jsonObject.getInt("navBackIconWidth");
        navBackIconHeight = jsonObject.getInt("navBackIconHeight");
        navBackgroundColor = jsonObject.getInt("navBackgroundColor");
        navTitle = jsonObject.getString("navTitle");
        navTitleColor = jsonObject.getInt("navTitleColor");
        isHideNav = jsonObject.getBoolean("isHideNav");

        logoIconName = jsonObject.getString("logoIconName");
        logoWidth = jsonObject.getInt("logoWidth");
        logoHeight = jsonObject.getInt("logoHeight");
        logoTopYOffset = jsonObject.getInt("logoTopYOffset");
        logoBottomYOffset = jsonObject.getInt("logoBottomYOffset");
        logoXOffset = jsonObject.getInt("logoXOffset");
        isHideLogo = jsonObject.getBoolean("isHideLogo");

        maskNumberColor = jsonObject.getInt("maskNumberColor");
        maskNumberSize = jsonObject.getInt("maskNumberSize");
        maskNumberTopYOffset = jsonObject.getInt("maskNumberTopYOffset");
        maskNumberBottomYOffset = jsonObject.getInt("maskNumberBottomYOffset");
        maskNumberXOffset = jsonObject.getInt("maskNumberXOffset");

        sloganSize = jsonObject.getInt("sloganSize");
        sloganColor = jsonObject.getInt("sloganColor");
        sloganTopYOffset = jsonObject.getInt("sloganTopYOffset");
        sloganBottomYOffset = jsonObject.getInt("sloganBottomYOffset");
        sloganXOffset = jsonObject.getInt("sloganXOffset");

        loginBtnText = jsonObject.getString("loginBtnText");
        loginBtnTextSize = jsonObject.getInt("loginBtnTextSize");
        loginBtnTextColor = jsonObject.getInt("loginBtnTextColor");
        loginBtnWidth = jsonObject.getInt("loginBtnWidth");
        loginBtnHeight = jsonObject.getInt("loginBtnHeight");
        loginBtnBackgroundRes = jsonObject.getString("loginBtnBackgroundRes");
        loginBtnTopYOffset = jsonObject.getInt("loginBtnTopYOffset");
        loginBtnBottomYOffset = jsonObject.getInt("loginBtnBottomYOffset");
        loginBtnXOffset = jsonObject.getInt("loginBtnXOffset");

        privacyTextColor = jsonObject.getInt("privacyTextColor");
        privacyProtocolColor = jsonObject.getInt("privacyProtocolColor");
        privacySize = jsonObject.getInt("privacySize");
        privacyTopYOffset = jsonObject.getInt("privacyTopYOffset");
        privacyBottomYOffset = jsonObject.getInt("privacyBottomYOffset");
        privacyXOffset = jsonObject.getInt("privacyXOffset");
        privacyState = jsonObject.getBoolean("privacyState");
        isHidePrivacyCheckBox = jsonObject.getBoolean("isHidePrivacyCheckBox");
        isPrivacyTextGravityCenter = jsonObject.getBoolean("isPrivacyTextGravityCenter");
        checkedImageName = jsonObject.getString("checkedImageName");
        unCheckedImageName = jsonObject.getString("unCheckedImageName");
        privacyTextStart = jsonObject.getString("privacyTextStart");
        protocolText = jsonObject.getString("protocolText");
        protocolLink = jsonObject.getString("protocolLink");
        protocol2Text = jsonObject.getString("protocol2Text");
        protocol2Link = jsonObject.getString("protocol2Link");
        privacyTextEnd = jsonObject.getString("privacyTextEnd");

        protocolNavTitle = jsonObject.getString("protocolNavTitle");
        protocolNavBackIcon = jsonObject.getString("protocolNavBackIcon");
        protocolNavColor = jsonObject.getInt("protocolNavColor");

        isDialogMode = jsonObject.getBoolean("isDialogMode");
        dialogWidth = jsonObject.getInt("dialogWidth");
        dialogHeight = jsonObject.getInt("dialogHeight");
        dialogX = jsonObject.getInt("dialogX");
        dialogY = jsonObject.getInt("dialogY");
        isBottomDialog = jsonObject.getBoolean("isBottomDialog");
    }

    public UnifyUiConfig getUiConfig(Context context) {
        boolean isSuccess = true;
        String destPath = context.getExternalFilesDir("config").getAbsolutePath() + File.separator + "config.json";
        if (fetchConfigFromNet) {
            downloadConfig(destPath, new Downloader.DownloadListener() {
                @Override
                public void onSuccess() {
                    Log.d(QuickLoginApplication.TAG, "下载配置文件成功");
                }

                @Override
                public void onFailed(int code, String msg) {
                    Log.e(QuickLoginApplication.TAG, "下载配置文件失败，code: " + code + " msg:" + msg);
                }
            });
        } else {
            try {
                freeConfig(context, "config.json", destPath);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(QuickLoginApplication.TAG, "释放配置文件失败:" + e.toString());
            }
        }
        try {
            parser(getJsonString(destPath));
        } catch (JSONException e) {
            e.printStackTrace();
            isSuccess = false;
            Toast.makeText(context, "解析配置文件出现异常: " + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e(QuickLoginApplication.TAG, "解析配置文件失败:" + e.toString());
        }
        if (isSuccess) {
            Toast.makeText(context, "配置更新完成", Toast.LENGTH_SHORT).show();
        }
        return buildUiConfig(context);
    }

    private void downloadConfig(String dstPath, Downloader.DownloadListener listener) {
        Downloader.download(configUrl, dstPath, listener);
    }

    private UnifyUiConfig buildUiConfig(final Context context) {
        UnifyUiConfig.Builder builder = new UnifyUiConfig.Builder()
                .setNavigationIcon(navBackIcon)
                .setNavigationBackIconWidth(navBackIconWidth)
                .setNavigationBackIconHeight(navBackIconHeight)
                .setNavigationBackgroundColor(navBackgroundColor)
                .setNavigationTitle(navTitle)
                .setNavigationTitleColor(navTitleColor)
                .setHideNavigation(isHideNav)

                .setLogoIconName(logoIconName)
                .setLogoWidth(logoWidth)
                .setLogoHeight(logoHeight)
                .setLogoTopYOffset(logoTopYOffset)
                .setLogoBottomYOffset(logoBottomYOffset)
                .setLogoXOffset(logoXOffset)
                .setHideLogo(isHideLogo)

                .setMaskNumberSize(maskNumberSize)
                .setMaskNumberColor(maskNumberColor)
                .setMaskNumberXOffset(maskNumberXOffset)
                .setMaskNumberTopYOffset(maskNumberTopYOffset)
                .setMaskNumberBottomYOffset(maskNumberBottomYOffset)

                .setSloganSize(sloganSize)
                .setSloganColor(sloganColor)
                .setSloganTopYOffset(sloganTopYOffset)
                .setSloganXOffset(sloganXOffset)
                .setSloganBottomYOffset(sloganBottomYOffset)

                .setLoginBtnText(loginBtnText)
                .setLoginBtnBackgroundRes(loginBtnBackgroundRes)
                .setLoginBtnTextColor(loginBtnTextColor)
                .setLoginBtnTextSize(loginBtnTextSize)
                .setLoginBtnHeight(loginBtnHeight)
                .setLoginBtnWidth(loginBtnWidth)
                .setLoginBtnTopYOffset(loginBtnTopYOffset)
                .setLoginBtnBottomYOffset(loginBtnBottomYOffset)
                .setLoginBtnXOffset(loginBtnXOffset)

                .setPrivacyTextColor(privacyTextColor)
                .setPrivacyProtocolColor(privacyProtocolColor)
                .setPrivacySize(privacySize)
                .setPrivacyTopYOffset(privacyTopYOffset)
                .setPrivacyBottomYOffset(privacyBottomYOffset)
                .setPrivacyXOffset(privacyXOffset)
                .setPrivacyState(privacyState)
                .setHidePrivacyCheckBox(isHidePrivacyCheckBox)
                .setPrivacyTextGravityCenter(isPrivacyTextGravityCenter)
                .setCheckedImageName(checkedImageName)
                .setUnCheckedImageName(unCheckedImageName)
                .setPrivacyTextStart(privacyTextStart)
                .setProtocolText(protocolText)
                .setProtocolLink(protocolLink)
                .setProtocol2Text(protocol2Text)
                .setProtocol2Link(protocol2Link)
                .setPrivacyTextEnd(privacyTextEnd)

                .setProtocolPageNavTitle(protocolNavTitle)
                .setProtocolPageNavBackIcon(protocolNavBackIcon)
                .setProtocolPageNavColor(protocolNavColor)
                .setDialogMode(isDialogMode, dialogWidth, dialogHeight, dialogX, dialogY, isBottomDialog);
        return builder.build(context);
    }

    private String getJsonString(String jsonFilePath) {
        BufferedReader reader = null;
        String data = "";
        try {
            FileInputStream fis = new FileInputStream(jsonFilePath);
            reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                data += line;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(QuickLoginApplication.TAG, "json配置文件不存在");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(QuickLoginApplication.TAG, "读取json配置文件内容出错: " + e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    private void freeConfig(Context context, String assetsName, String destPath) throws IOException {
        InputStream is = context.getAssets().open(assetsName);
        FileOutputStream fos = new FileOutputStream(new File(destPath));
        byte[] buffer = new byte[1024];
        int byteCount = 0;
        while ((byteCount = is.read(buffer)) != -1) {
            fos.write(buffer, 0, byteCount);
        }
        fos.flush();
        is.close();
        fos.close();
    }
}
