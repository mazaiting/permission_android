package com.mazaiting.permission.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Build
import androidx.fragment.app.Fragment
import com.mazaiting.log.L
import com.mazaiting.permission.PermissionResult
import com.mazaiting.permission.Permissions

/**
 * 权限工具类
 */
object PermissionUtil {
  
  /**
   * 判断是否6.0以上的版本
   * @return true: 大于6.0; false: 小于6.0
   */
  private fun isVersionOverM(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
  
  /**
   * 获取当前页面
   * @param any 当前页面对象
   * @return 当前界面
   */
  private fun getActivity(any: Any): Activity {
    // 判断是否为Fragment的子类
    if (any is Fragment) {
      return any.activity!!
    }
    return any as Activity
  }
  
  /**
   * 初始化权限界面
   * @param any 当前页面
   */
  fun requestPermission(any: Any) {
    // 检测系统版本
    if (isVersionOverM()) {
      // 获取注解
      val annotation = getActivity(any).javaClass.getAnnotation(Permissions::class.java)
      // 判断注解是否为空
      if (annotation != null) {
        // 权限信息
        val permissions = annotation.value
        // 创建待申请权限数组
        var request: List<String> = ArrayList()
        permissions.forEach { permission ->
          //        L.d(permission)
          // 检测自身是否具有该权限
          val selfPermission = ContextCompat.checkSelfPermission(getActivity(any), permission)
          // 如果没有该权限, 则添加进待申请的权限列表
          if (selfPermission != PackageManager.PERMISSION_GRANTED) {
            request += permission
          }
        }
//      request.forEach { permission -> L.d(permission) }
        // 判断待请求的权限数组是否为空
        if (request.isNotEmpty()) {
          // 不为空则请求权限
          ActivityCompat.requestPermissions(getActivity(any), request.toTypedArray(), annotation.code)
        } else {
          // 获取成功
//          execPermissionSucceed(any, annotation.code)
          execPermission(any, annotation.code, State.SUCCESS, null)
        }
      }
    }
  }
  
  /**
   * 请求权限结果
   */
  fun onRequestPermissionsResult(any: Any, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    // 检测系统版本
    if (isVersionOverM()) {
      // 判断权限列表是否为空
      if (permissions.isNotEmpty()) {
        // 被用户拒绝的权限数组
        var deniedPermissions: List<String> = ArrayList()
        // 被用户勾选不再提示的权限数组
        var notShowPermissions: List<String> = ArrayList()
        // 遍历请求的权限
        for (i in 0 until permissions.size) {
          // 检测未允许的权限
          if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(any), permissions[i])) {
              // 添加到不再提示列表
              notShowPermissions += permissions[i]
            } else {
              // 添加进入拒绝权限数组
              deniedPermissions += permissions[i]
            }
          }
        }
        
        when {
          // 拒绝的权限不为空时
          deniedPermissions.isNotEmpty() -> {
            // 执行拒绝权限的方法
//            L.d("=================拒绝========================")
            execPermission(any, requestCode, State.DENIED, deniedPermissions)
          }
          // 不再提示权限列表不为空
          notShowPermissions.isNotEmpty() -> {
            // 执行不再提示权限的方法
//            L.d("================不再提示======================")
            execPermission(any, requestCode, State.NOT_SHOW, notShowPermissions)
          }
          // 权限请求成功
          else -> {
//            L.d("================请求成功======================")
            execPermission(any, requestCode, State.SUCCESS, null)
          }
        }
      }
    }
  }
  
  /**
   * 处理权限授权结果
   * @param any 类对象
   * @param requestCode 请求码
   * @param state 状态码
   * @param permissions 权限列表
   */
  private fun execPermission(any: Any, requestCode: Int, state: State, permissions: List<String>?) {
    L.d("=============Permission Result================")
    // 获取当前页面所定义的所有方法
    val methods = any.javaClass.declaredMethods
    // Kotlin中使用break方法
    run breaking@{
      // 迭代所有方法
      methods.forEach { method ->
        // 打印方法
//        L.d("$method")
        // 获取方法上的注解
        val annotation = method.getAnnotation(PermissionResult::class.java)
        // 判断注解是否为空
        if (annotation != null) {
          // 获取方法上的请求码
          val value = annotation.value
          // 判断请求码是否相同
          if (requestCode == value) {
            // 执行方法
            // 判断权限集合是否为空, 如果不为空则为拒绝或者不再提示
            method.invoke(any, state, permissions)
            return@breaking
          }
        }
      }
    }
  }
}