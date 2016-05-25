# 解决Android应用启动白色闪屏

Android 新建一个应用时，当你启动时总会发现刚开始会闪一次白屏（或黑屏），也许你会认为是 `Activity` 中的 `onCreate()` 中操作太多，可是我们在方法中什么也不做，就是默认的 helloworld 项目也是如此。（效果看下图）

![](https://raw.githubusercontent.com/onlylemi/res/master/android_logo_light_7.gif)

手机上一些 APP 你会发现 QQ、微信、微博等就不存在，打开后直接显示 logo 启动页，但是也有一些应用仍然存在，像知乎、掘金，还有大 Google Chrome

![](https://raw.githubusercontent.com/onlylemi/res/master/android_logo_light_2.gif)

## 原因

仔细分析你会发现这是 `style` 的原因，`AppThemeTheme` 默认的 `parent` 是 `Theme.AppCompat.Light.DarkActionBar`，你一直查看它的父类你会发现，在 `Platform.AppCompat.Light` 中设置 `<item name="android:windowBackground">@color/background_material_light</item>`。所以背景就是白色的。

## 解决方案

修改 Style 之前，我们需要先在 `manifest` 中设置 `activity` 的 `theme` 属性

```xml
<activity
    android:name=".MainActivity"
    android:theme="@style/AppTheme.Boot">
    ...
</activity>
```

### 1. 设置 Style 背景透明

```xml
<style name="AppTheme.Boot" parent="AppTheme">
    <item name="android:windowIsTranslucent">true</item>
    <item name="android:windowNoTitle">true</item>
</style>
```

这种方式会给人感觉程序启动慢，点击之后会反应一会然后直接进入界面

![](https://raw.githubusercontent.com/onlylemi/res/master/android_logo_light_4.gif)

像腾讯视频、WPS、虾米音乐应该是这样设计（个人猜测）

![](https://raw.githubusercontent.com/onlylemi/res/master/android_logo_light_3.gif)

### 2. 设置 Style 背景图片

```xml
<style name="AppTheme.Boot" parent="AppTheme">
    <item name="android:windowBackground">@mipmap/logo</item>
    <item name="android:windowNoTitle">true</item>
</style>
```

采用这种方式后，可能在 `Activity` 中的背景也会变成你设置的图片

![](https://raw.githubusercontent.com/onlylemi/res/master/android_logo_light_6.gif)

这样你可以设置你的 `Activity` 的布局背景，这样就会显示 OK

```xml
<LinearLayout
    ...
    android:background="@android:drawable/screen_background_light">
    
    ...
</LinearLayout>
```
![](https://raw.githubusercontent.com/onlylemi/res/master/android_logo_light_5.gif)

这种设置给人程序启动快的感觉，界面先显示背景图，然后再刷新其他界面控件。像我们看到的 QQ、微信、微博等应该是这样做的（个人猜测）

![](https://raw.githubusercontent.com/onlylemi/res/master/android_logo_light_1.gif)