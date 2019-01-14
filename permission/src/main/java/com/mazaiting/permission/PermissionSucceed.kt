package com.mazaiting.permission

/**
 * 注解权限成功
 *
 * @param value 请求码
 *
 * @Retention 运行时注解
 * @Target 注解位置
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class PermissionSucceed(val value: Int)