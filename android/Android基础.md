# Android 基础

## Activity 的生命流程图

![activity生命周期图](http://pic001.cnblogs.com/img/tea9/201008/2010080516521645.png)

* 弹出Toast和AlertDialog的时候Activity的生命周期不会有改变

### 横竖屏切换

* 设置 android:configChanges="orientation" 和不设置这个属性，生命周期表现为重新创建activity
* 设置 android:configChanges="orientation|keyboardHidden"，在2.3上表现为不重新创建activity，4.0如下
    * android:targetSdkVersion<="12"，生命周期表现为不重新创建activity
    * android:targetSdkVersion>"12"，表现为重新创建activity
* 设置 android:configChanges="orientation|keyboardHidden|screenSize"，在2.3和4.0上都表现为不重新创建

参考：http://www.cnblogs.com/xiaoQLu/p/3324503.html

### 内存不足时，怎么保持Activity的一些状态，在哪个方法里面做具体操作？

onPause()方法

## ListView

### ListView 优化

* 重用 view，不用每次都加载
* ViewHolder，对空间实例进行缓存

### 怎么实现ListView多种布局？

* 继承 BaseAdapter
* 重写 getViewTypeCount() – 该方法返回多少个不同的布局
```java
public int getItemViewType(int position) {  
    return list.get(position).type;  
}  
```
* 重写 getItemViewType(int) – 根据position返回相应的Item

### ListView与数据库绑定的实现

### ListView 局部刷新

参考：http://www.cnblogs.com/liuling/p/2015-10-20-01.html

### ListView卡顿的原因与性能优化

* convertView 没有潘孔，每次都加载布局
* ViewHolder 没有使用setTag和getTag方式
* inflate的row 嵌套太深（布局过于复杂）
* 图片没有做异步加载
* 多余或者不合理的notifySetDataChanged，没有采用局部刷新
* listview 被多层嵌套，多次的onMessure导致卡顿
* item加载时需要进行大量的计算

### ListView和recycleview的区别（x）

## Android中的动画

* tween 补间动画。通过指定View的初末状态和变化时间、方式，对View的内容完成一系列的图形变换来实现动画效果。
    * Alpha
    * Scale
    * Translate
    * Rotate。
* frame 帧动画
    * AnimationDrawable 控制
    * animation-list xml布局
    
## JNI怎么使用（没写）

## OOM（内存不足、泄漏、溢出）

* 应用中需要加载大对象，例如Bitmap
* 持有无用的对象使其无法被gc

### 解决方法

* 在内存引用上做些处理，常用的有软引用、强化引用、弱引用
* 在内存中加载图片时直接在内存中做处理，如：边界压缩.
* 动态回收内存
* 优化Dalvik虚拟机的堆内存分配
* 自定义堆内存大小

## ANR（应用没有响应）

* 主线程 (“事件处理线程” / “UI线程”) 在5秒内没有响应输入事件
* BroadcastReceiver 没有在10秒内完成返回

### 原因

* 在主线程中进行网络操作
* 在主线程中进行一些缓慢的磁盘操作（无优化的SQL操作）

### 解决

* 异步操作，通过handler更新UI线程

## 图片缓存加载机制（X）



## Fragment

### 生命周期

![Fragment生命周期](http://img.my.csdn.net/uploads/201301/22/1358840998_2990.png)

```
03-12 09:27:40.781 11786-11786/com.onlylemi.androidtest I/FragmentActivity: onCreate
03-12 09:27:40.783 11786-11786/com.onlylemi.androidtest I/TestFragment: onAttach
03-12 09:27:40.783 11786-11786/com.onlylemi.androidtest I/TestFragment: onCreate
03-12 09:27:40.783 11786-11786/com.onlylemi.androidtest I/TestFragment: onCreateView
03-12 09:27:40.786 11786-11786/com.onlylemi.androidtest I/TestFragment: onActivityCreated
03-12 09:27:40.786 11786-11786/com.onlylemi.androidtest I/TestFragment: onStart
03-12 09:27:40.786 11786-11786/com.onlylemi.androidtest I/FragmentActivity: onStart
03-12 09:27:40.788 11786-11786/com.onlylemi.androidtest I/FragmentActivity: onResume
03-12 09:27:40.788 11786-11786/com.onlylemi.androidtest I/TestFragment: onResume
03-12 09:28:11.294 11786-11786/com.onlylemi.androidtest I/TestFragment: onPause
03-12 09:28:11.294 11786-11786/com.onlylemi.androidtest I/FragmentActivity: onPause
03-12 09:28:11.870 11786-11786/com.onlylemi.androidtest I/TestFragment: onStop
03-12 09:28:11.870 11786-11786/com.onlylemi.androidtest I/FragmentActivity: onStop
03-12 09:28:26.128 11786-11786/com.onlylemi.androidtest I/FragmentActivity: onRestart
03-12 09:28:26.128 11786-11786/com.onlylemi.androidtest I/TestFragment: onStart
03-12 09:28:26.128 11786-11786/com.onlylemi.androidtest I/FragmentActivity: onStart
03-12 09:28:26.129 11786-11786/com.onlylemi.androidtest I/FragmentActivity: onResume
03-12 09:28:26.129 11786-11786/com.onlylemi.androidtest I/TestFragment: onResume
03-12 09:28:57.327 11786-11786/com.onlylemi.androidtest I/TestFragment: onPause
03-12 09:28:57.327 11786-11786/com.onlylemi.androidtest I/FragmentActivity: onPause
03-12 09:28:57.797 11786-11786/com.onlylemi.androidtest I/TestFragment: onStop
03-12 09:28:57.797 11786-11786/com.onlylemi.androidtest I/FragmentActivity: onStop
03-12 09:28:57.797 11786-11786/com.onlylemi.androidtest I/TestFragment: onDestroyView
03-12 09:28:57.798 11786-11786/com.onlylemi.androidtest I/TestFragment: onDestroy
03-12 09:28:57.798 11786-11786/com.onlylemi.androidtest I/TestFragment: onDetach
03-12 09:28:57.798 11786-11786/com.onlylemi.androidtest I/FragmentActivity: onDestroy
```

### Activity中如何动态的添加Fragment

```java
FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
// 添加
transaction.add(R.id.container,
                    new TestFragment()).commit();
// 替换
transaction.replace(R.id.container,
                    new TestFragment()).commit();

// 返回栈
transaction.addToBackStack(null);
```

### Activity 与 Fragment 通信

* FragmentManager 提供了 findviewbyid 来得到 fragmnet 的对象
* Fragment 中提供了 getActivity() 获取 activity 实例



## Scrollview怎么判断是否滑倒底部

* 实现OnTouchListener来监听是否滑动到最底部
* 重写ScrollView的onScrollChanged的方法，在onScrollChanged函数中判断

参考：http://www.07net01.com/2015/07/883727.html

## Asynctask

## Service

## 广播

### 注册广播

* 代码中注册，进行监听。广播跟随程序的生命周期
```java
IntentFilter intentFilter = new IntentFilter();
intentFilter.addAction("android.net.conn.CONNECTIVITY_VHANGE");
BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 监听action 的处理事件
    }
};
registerReceiver(receiver, intentFilter);
```
* 配置xml文件中注册，静态注册实现开机启动。是常驻型
```xml
<receiver android:name=".MyBroadcastReceiver">
    <intent-filter>
        <action android:name="android.net.conn.CONNECTIVITY_VHANGE"/>
    </intent-filter>
</receiver>
```
* 本地广播只能使用代码注册

## 数据持久化

### 文件储存

* 存：openFileOutput
* 读：openFileInput

### SharedPreferences 储存（原理是xml文件）

```java
// 存
SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
editor.putString("name", "xiaoming");
editor.commit();

// 读
SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
preferences.getString("name", "");
```

### SQLite 数据库储存

使用 SQLiteOpenHelper，重写 onCreate() 和 onUpgrade()

* onCreate()。只会在第一次新建数据库的时候会被调用
* onUpgrade()。当数据库的版本发生变化时，会调用

## Content Provider

