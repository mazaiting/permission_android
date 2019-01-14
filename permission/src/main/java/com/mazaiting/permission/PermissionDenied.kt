package com.mazaiting.permission

/**
 * 拒绝权限注解
 * @Retention 运行时注解
 * @Target 注解位置
 *
 * @param value 请求码
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class PermissionDenied(val value: Int)