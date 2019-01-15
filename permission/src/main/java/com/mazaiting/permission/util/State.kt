package com.mazaiting.permission.util

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