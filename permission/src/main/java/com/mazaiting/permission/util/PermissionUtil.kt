package com.mazaiting.permission.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Build
import androidx.fragment.app.Fragment
import com.mazaiting.log.L
import com.mazaiting.permission.PermissionDenied
import com.mazaiting.permission.PermissionNotShow
import com.mazaiting.permission.PermissionSucceed
import com.mazaiting.permission.Permissions

/**
 * 权限工具类
 */
object PermissionUtil {
  /**
   * 状态枚举
   */
  enum class State{
    /** 权限请求成功 */
    SUCCESS,
    /** 权限请求被拒绝 */
    DENIED,
    /** 权限请求不再显示 */
    NOT_SHOW
  }
  
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
          execPermissionSucceed(any, annotation.code)
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
            execPermissionSucceed(any, requestCode)
          }
        }
      }
    }
  }
//
//  /**
//   * 请求权限结果
//   */
//  fun onRequestPermissionsResult(any: Any, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//    // 检测系统版本
//    if (isVersionOverM()) {
//      // 被用户拒绝的权限数组
//      var deniedPermissions: List<String> = ArrayList()
//      // 遍历请求的权限
//      for (i in 0 until permissions.size) {
//        // 检测未允许的权限
//        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//
//          // 添加进入拒绝权限数组
//          deniedPermissions += permissions[i]
//        }
//      }
//
//      // 区分拒绝权限与不再提示权限
//      // 被用户勾选不再提示的权限数组
//      var notShowPermissions: List<String> = ArrayList()
//      deniedPermissions.forEach { permission ->
//        // 被用户勾选了不再提示的权限
//        // true -- 拒绝
//        // false -- 允许
//        // false -- 不再询问
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(any), permission)) {
//          // 加入不再提示的权限数组
//          notShowPermissions += permission
//          // 从拒绝权限中移除
//          deniedPermissions -= permission
//        }
//      }
//      // 拒绝的权限不为空时
//      if (deniedPermissions.isNotEmpty()) {
//        // 执行拒绝权限的方法
//        L.d("======================拒绝====================")
////        deniedPermissions.forEach { permission ->
////          L.d(permission)
////          execPermission(any, requestCode, permission)
////        }
//        execPermission(any, requestCode, State.DENIED,deniedPermissions)
//      }
//
//      if (notShowPermissions.isNotEmpty()) {
//        // 执行不再提示权限的方法
//        L.d("================不再提示======================")
////        notShowPermissions.forEach { permission ->
////          L.d(permission)
////        }
//        execPermission(any, requestCode, State.NOT_SHOW, notShowPermissions)
//      }
//    }
//  }
  
  /**
   * 处理权限授权结果
   * @param any 类对象
   * @param requestCode 请求码
   * @param state 状态码
   * @param permissions 权限列表
   */
  private fun execPermission(any: Any, requestCode: Int, state: State, permissions: List<String>) {
    when (state) {
      State.DENIED -> execPermissionDenied(any, requestCode, permissions)
      State.SUCCESS -> execPermissionSucceed(any, requestCode)
      State.NOT_SHOW -> execPermissionNotShow(any, requestCode, permissions)
    }
  }
  
  /**
   * 执行拒绝权限方法
   * @param any 类对象
   * @param requestCode 请求码
   * @param permissions 权限列表
   */
  private fun execPermissionDenied(any: Any, requestCode: Int, permissions: List<String>) {
    L.d("=============Denied Permission================")
    // 获取当前页面所定义的所有方法
    val methods = any.javaClass.declaredMethods
    // Kotlin中使用break方法
    run breaking@ {
      // 迭代所有方法
      methods.forEach { method ->
        // 打印方法
//        L.d("$method")
        // 获取方法上的注解
        val annotation = method.getAnnotation(PermissionDenied::class.java)
        // 判断注解是否为空
        if (annotation != null) {
          // 获取方法上的请求码
          val value = annotation.value
          // 判断请求码是否相同
          if (requestCode == value) {
            if (method.parameterTypes.isNotEmpty()) {
              // 执行所要执行的方法
              method.invoke(any, permissions)
            } else {
              method.invoke(any)
            }
            return@breaking
          }
        }
      }
    }
  }
  
  /**
   * 执行拒绝权限方法
   * @param any 类对象
   * @param requestCode 请求码
   * @param permissions 权限列表
   */
  private fun execPermissionNotShow(any: Any, requestCode: Int, permissions: List<String>) {
    L.d("=============Denied NotShow================")
    // 获取当前页面所定义的所有方法
    val methods = any.javaClass.declaredMethods
    // Kotlin中使用break方法
    run breaking@ {
      // 迭代所有方法
      methods.forEach { method ->
        // 打印方法
//        L.d("$method")
        // 获取方法上的注解
        val annotation = method.getAnnotation(PermissionNotShow::class.java)
        // 判断注解是否为空
        if (annotation != null) {
          // 获取方法上的请求码
          val value = annotation.value
          // 判断请求码是否相同
          if (requestCode == value) {
            if (method.parameterTypes.isNotEmpty()) {
              // 执行所要执行的方法
              method.invoke(any, permissions)
            } else {
              method.invoke(any)
            }
            return@breaking
          }
        }
      }
    }
  }
  
  /**
   * 执行成功获取权限方法
   * @param any 类对象
   * @param requestCode 请求码
   */
  private fun execPermissionSucceed(any: Any, requestCode: Int) {
    L.d("=============Denied Succeed================")
    // 获取当前页面所定义的所有方法
    val methods = any.javaClass.declaredMethods
    // Kotlin中使用break方法
    run breaking@ {
      // 迭代所有方法
      methods.forEach { method ->
        // 打印方法
//        L.d("$method")
        // 获取方法上的注解
        val annotation = method.getAnnotation(PermissionSucceed::class.java)
        // 判断注解是否为空
        if (annotation != null) {
          // 获取方法上的请求码
          val value = annotation.value
          // 判断请求码是否相同
          if (requestCode == value) {
            // 执行所要执行的方法
            method.invoke(any)
            return@breaking
          }
        }
      }
    }
  }
}