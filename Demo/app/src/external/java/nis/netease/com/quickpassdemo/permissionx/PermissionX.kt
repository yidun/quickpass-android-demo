package nis.netease.com.quickpassdemo.permissionx

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * @author liuxiaoshuai
 * @date 2022/3/17
 * @desc
 * @email liulingfeng@mistong.com
 */
object PermissionX {
    private const val TAG = "InvisibleFragment"

    fun hasPermissions(@NonNull context: Context, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(
                TAG,
                "hasPermissions: API version < M, returning true by default"
            )
            return true
        }

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun request(
        host: FragmentActivity,
        vararg permissions: String,
        callback: PermissionCallback
    ) {
        val fragmentManager = host.supportFragmentManager
        val exitedFragment = fragmentManager.findFragmentByTag(TAG)
        val fragment = if (exitedFragment != null) {
            exitedFragment as InvisibleFragment
        } else {
            val invisibleFragment = InvisibleFragment()
            fragmentManager.beginTransaction().add(invisibleFragment, TAG).commitNow()
            invisibleFragment
        }
        fragment.requestNow(callback, *permissions)
    }
}