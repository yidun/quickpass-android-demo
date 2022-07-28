package nis.netease.com.quickpassdemo.permissionx

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

/**
 * @author liuxiaoshuai
 * @date 2022/3/17
 * @desc
 * @email liulingfeng@mistong.com
 */
typealias PermissionCallback = ((Boolean, List<String>) -> Unit)

class InvisibleFragment : Fragment() {
    companion object {
        private const val CODE_PERMISSION = 1
    }

    private var callback: PermissionCallback? = null
    fun requestNow(
        callback: PermissionCallback,
        vararg permissions: String
    ) {
        this.callback = callback
        this.requestPermissions(permissions, CODE_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CODE_PERMISSION) {
            val deniedList = ArrayList<String>()
            for ((index, result) in grantResults.withIndex()) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permissions[index])
                }
            }
            val allGranted = deniedList.isEmpty()
            callback?.let { it(allGranted, deniedList) }
        }
    }
}