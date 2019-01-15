# Android 权限动态申请

#### 介绍
Android 6.0动态权限申请

## v1.1
v1.0 存在的问题, 使用三个注解(拒绝权限, 不再提示权限, 成功权限)反射Activity类, 若类中方法较多, 则比较影响性能.
修复方法：
- 移除PermissionDenied, PermissionNotShow, PermissionSucceed注解, 创建PermissionResult注解
- PermissionResult注解方法传入形参状态码及权限列表
- 用户根据状态码处理结果

#### 软件架构
- app
- permission
    - util
        - PermissionSettingUtil.kt :  跳转应用权限管理页工具类
        - PermissionUtil.kt        :  权限动态申请工具类
        - State.kt                 :  权限状态枚举
    - Permissions.kt               :  权限列表注解
    - PermissionResult.kt          :  权限授权结果注解

#### 使用说明
1. 在类的上方使用注解@Permissions
```
// 参数一：权限列表； 参数二：请求码
@Permissions([Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA], MainActivity.REQUEST_CODE)
class MainActivity : AppCompatActivity() {

}
```
2. 并在onCreate方法中添加请求权限方法
```
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    // 请求权限
    PermissionUtil.requestPermission(this)
  }
```
此时在当前页面启动时会请求此相应的权限, 若想要自己处理授权处理结果, 则在onRequestPermissionsResult方法中自己处理即可.
3. 此步骤开始后为该框架处理权限控制
在onRequestPermissionsResult方法中添加权限处理方法
```
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    // 权限处理
    PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
  }
```
4. 权限授权结果--PermissionResult注解
方法名: 自定义
参数state: 授权状态码, 必须
参数permissions: 权限列表, state为succeed时权限列表为空, 必须
REQUEST_CODE为请求码
```
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
```

## v1.0

#### 软件架构
- app
- permission
    - util
        - PermissionSettingUtil.kt :  跳转应用权限管理页工具类
        - PermissionUtil.kt        :  权限动态申请工具类
    - PermissionDenied.kt          :  权限拒绝注解
    - PermissionNotShow.kt         :  权限不再显示注解
    - Permissions.kt               :  权限列表注解
    - PermissionResult.kt         :  权限申请成功注解

#### 使用说明
1. 在类的上方使用注解@Permissions
```
// 参数一：权限列表； 参数二：请求码
@Permissions([Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA], MainActivity.REQUEST_CODE)
class MainActivity : AppCompatActivity() {

}
```
2. 并在onCreate方法中添加请求权限方法
```
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    // 请求权限
    PermissionUtil.requestPermission(this)
  }
```
此时在当前页面启动时会请求此相应的权限, 若想要自己处理授权处理结果, 则在onRequestPermissionsResult方法中自己处理即可.
3. 此步骤开始后为该框架处理权限控制
在onRequestPermissionsResult方法中添加权限处理方法
```
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    // 权限处理
    PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
  }
```
4. 权限拒绝回调--PermissionDenied注解
这里的方法名可以自定义, 参数不接收, 但如果要接收, 则为字符串列表
REQUEST_CODE为请求码
```
  /**
   * 权限拒绝
   */
  @PermissionDenied(REQUEST_CODE)
  fun denied(permissions: List<String>) {
    permissions.forEach { permission ->
      L.d("MainActivity--denied--$permission")
    }
    // 再次请求权限
    PermissionUtil.requestPermission(this)
  }
```
5. 权限不再提示--注解PermissionNotShow
这里的方法名可以自定义, 参数不接收, 但如果要接收, 则为字符串列表
REQUEST_CODE为请求码
```
  /**
   * 点击不再提示后并拒绝
   */
  @PermissionNotShow(REQUEST_CODE)
  fun notShow(permissions: List<String>) {
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
```
6. 权限授权成功--PermissionSucceed注解
这里的方法名自定义, 无参数接收.
REQUEST_CODE为请求码
```
  /**
   * 权限授权成功
   */
  @PermissionSucceed(REQUEST_CODE)
  fun succeed() {
    L.d("MainActivity--succeed")
    Toast.makeText(this, "权限申请成功!", Toast.LENGTH_SHORT).show()
  }
```


#### 相关信息

1. [码云主页](https://gitee.com/mazaiting)
2. [简书主页](https://www.jianshu.com/u/5d2cb4bfeb15)
3. [CSDN主页](https://blog.csdn.net/mazaiting)
4. [Github主页](https://github.com/mazaiting)
5. Flutter QQ群: 717034802

![FlutterQQ群](https://images.gitee.com/uploads/images/2019/0115/104203_240a69e0_1199005.png "FlutterQQ群")

6. 微信公众号： real_x2019

![微信公众号：凌浩雨](https://images.gitee.com/uploads/images/2019/0115/104253_eccc5a6f_1199005.jpeg "real_x2019")

7. 打赏

![打赏](https://test-1256286377.cos.ap-chengdu.myqcloud.com/%E6%94%AF%E4%BB%98%E5%AE%9D.jpg "支付宝")