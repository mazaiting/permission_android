package com.mazaiting.permissiontest

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mazaiting.log.L
import com.mazaiting.permission.PermissionResult
import com.mazaiting.permission.Permissions
import com.mazaiting.permission.util.PermissionSettingUtil
import com.mazaiting.permission.util.PermissionUtil
import com.mazaiting.permission.util.State

/// 待申请的权限
@Permissions([Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA], MainActivity.REQUEST_CODE)
class MainActivity : AppCompatActivity() {
  
  companion object {
    /** 权限请求码 */
    const val REQUEST_CODE = 0x1000
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    // 请求权限
    PermissionUtil.requestPermission(this)
  }
  
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    // 权限处理
    PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
  }
  
  /**
   * 权限请求结果处理
   * @param state 权限授权状态
   * @param permissions 当状态为Succeed时权限列表为空
   */
  @PermissionResult(REQUEST_CODE)
  fun permissionResult(state: State, permissions: List<String>?) {
    when (state) {
      State.DENIED -> // 再次请求权限
        PermissionUtil.requestPermission(this)
      State.SUCCESS -> Toast.makeText(this, "权限申请成功!", Toast.LENGTH_SHORT).show()
      State.NOT_SHOW -> notShow(permissions!!)
    }
  }
  
  /**
   * 点击不再提示后并拒绝
   * @param permissions 权限列表
   */
  private fun notShow(permissions: List<String>) {
    val sb = StringBuilder()
    // 迭代读取权限
    permissions.forEach { permission ->
      L.d("MainActivity--notShow--$permission")
      sb.append("$permission\n")
    }
    // 跳转到应用设置界面
    AlertDialog.Builder(this)
            .setTitle("友情提示！")
            .setMessage("请跳转到设置界面同意下面权限：\n" + sb.toString())
            .setPositiveButton("同意") { _, _ ->
              // 跳转到应用设置界面
//              goToAppSetting(this)
              PermissionSettingUtil.gotoPermissionActivity(this)
            }
            .setNegativeButton("关闭") { _, _ ->
              // 关闭当前页面
              finish()
            }
            .setCancelable(false).show()
  }
  
}
