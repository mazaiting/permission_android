package com.mazaiting.permission

/**
 * Android 6.0 权限注解框架
 *
 * @Retention 运行时注解
 * @Target 设置注解位置 -- 类
 *
 * @param value 请求权限数组
 * @param value 请求码
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(allowedTargets = [AnnotationTarget.CLASS])
annotation class Permissions(
        /** 权限列表 */
        val value: Array<String>,
        /** 权限请求码 */
        val code: Int
)
