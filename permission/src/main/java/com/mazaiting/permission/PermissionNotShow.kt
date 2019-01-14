package com.mazaiting.permission

/**
 * 不再提示注解
 * @Retention 运行时注解
 * @Target 注解位置
 *
 * @param value 请求码
 */
annotation class PermissionNotShow(val value: Int)