package com.mazaiting.permission.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import com.mazaiting.permission.BuildConfig

/**
 * 权限设置工具类
 */
object PermissionSettingUtil {
  
  /**
   * 跳转到权限管理页面
   * @param context 上下文
   */
  fun gotoPermissionActivity(context: Context) {
    //手机厂商
    val brand = Build.BRAND
    if (TextUtils.equals(brand.toLowerCase(), "redmi") || TextUtils.equals(brand.toLowerCase(), "xiaomi")) {
      //小米
      gotoMiUiPermission(context)
    } else if (TextUtils.equals(brand.toLowerCase(), "meizu")) {
      gotoMeiZuPermission(context)
    } else if (TextUtils.equals(brand.toLowerCase(), "huawei") || TextUtils.equals(brand.toLowerCase(), "honor")) {
      gotoHuaWeiPermission(context)
    } else {
      context.startActivity(getAppDetailSettingIntent(context))
    }
  }
  
  /**
   * 跳转到MiUi的权限管理页面
   * @param context 上下文
   */
  private fun gotoMiUiPermission(context: Context) {
    try {
      // MIUI 8
      val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
      intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
      intent.putExtra("extra_pkgname", context.packageName)
      context.startActivity(intent)
    } catch (e: Exception) {
      try { // MIUI 5/6/7
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
        intent.putExtra("extra_pkgname", context.packageName)
        context.startActivity(intent)
      } catch (ignored: Exception) { // 否则跳转到应用详情
        context.startActivity(getAppDetailSettingIntent(context))
      }
    }
  }
  
  /**
   * 跳转到魅族的权限管理系统
   * @param context 上下文
   */
  private fun gotoMeiZuPermission(context: Context) {
    try {
      val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
      intent.addCategory(Intent.CATEGORY_DEFAULT)
      intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
      context.startActivity(intent)
    } catch (e: Exception) {
      context.startActivity(getAppDetailSettingIntent(context))
    }
  }
  
  /**
   * 华为的权限管理页面
   * @param context 上下文
   */
  private fun gotoHuaWeiPermission(context: Context) {
    try {
      val intent = Intent()
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      //华为权限管理
      val comp = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
      intent.component = comp
      context.startActivity(intent)
    } catch (e: Exception) {
      context.startActivity(getAppDetailSettingIntent(context))
    }
    
  }
  
  /**
   * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
   * @param context 上下文
   * @return 意图
   */
  private fun getAppDetailSettingIntent(context: Context): Intent {
    val intent = Intent()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
    intent.data = Uri.fromParts("package", context.packageName, null)
    return intent
  }
}