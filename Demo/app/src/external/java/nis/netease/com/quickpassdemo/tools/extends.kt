package nis.netease.com.quickpassdemo.tools

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import org.apache.commons.mycodec.digest.DigestUtils
import java.util.*
import kotlin.math.roundToInt

/**
 * @author liuxiaoshuai
 * @date 2022/3/21
 * @desc
 * @email liulingfeng@mistong.com
 */
inline fun <reified T> startActivity(context: Context, block: Intent.() -> Unit) {
    val intent = Intent(context, T::class.java)
    intent.block()
    context.startActivity(intent)
}

fun String.showToast(context: Context, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(context, this, duration).show()
}

fun Float.dip2px(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun Float.px2dp(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this / scale + 0.5f).toInt()
}

fun getScreenWidth(context: Context): Int {
    val wm = context
        .getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(outMetrics)
    return (outMetrics.widthPixels.toFloat()).px2dp(context)
}

fun getScreenHeight(context: Context): Int {
    val wm = context
        .getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(outMetrics)
    return (outMetrics.heightPixels.toFloat()).px2dp(context)
}

fun getRandomString(length: Int): String {
    val keyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val sb = StringBuffer()
    val len = keyString.length
    for (i in 0 until length) {
        sb.append(keyString[(Math.random() * (len - 1)).roundToInt()])
    }
    return sb.toString()
}

fun generateSign(secretKey: String, params: Map<String, String>): String? {
    var signature: String? = ""
    if (TextUtils.isEmpty(secretKey) || params.isEmpty()) {
        return signature
    }
    try {
        // 1. 参数名按照ASCII码表升序排序
        val keys = params.keys.toTypedArray()
        Arrays.sort(keys)
        // 2. 按照排序拼接参数名与参数值
        val paramBuffer = StringBuilder()
        for (key in keys) {
            paramBuffer.append(key).append(if (params[key] == null) "" else params[key])
        }
        // 3. 将secretKey拼接到最后
        paramBuffer.append(secretKey)
        // 4. MD5是128位长度的摘要算法，用16进制表示，一个十六进制的字符能表示4个位，所以签名后的字符串长度固定为32个十六进制字符。
        signature = DigestUtils.md5Hex(paramBuffer.toString().toByteArray(charset("UTF-8")))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return signature
}