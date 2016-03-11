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

## Service

## ListView

### ListView 优化

* 重用view
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

* 加载的对象过大
* 无用的对象仍然存在引用

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

### Activity中如何动态的添加Fragment


## 内存不足时，怎么保持Activity的一些状态，在哪个方法里面做具体操作？


## Scrollview怎么判断是否滑倒底部

## ViewPager 的怎么做性能优化

## Asynctask具体用法？

## Asynctask的Do in background方法是怎么通知UI线程刷新进度条的？

## Asynctask的Do in background方法默认是返回 true ，表示任务完成，如果想返回具体的数据呢，怎么做。如果Activity被销毁了，还会执行到postexcutd方法吗？

## View中onTouch，onTouchEvent，onClick的执行顺序

## 不使用动画，怎么实现一个动态的 View？

## Postvalidata与Validata有什么区别？

## Asset与raw都能存放资源，他们有什么区别？

## 如何自定义ViewGroup？

## 什么是 MVC 模式？MVC 模式的好处是什么？

## JVM 和Dalvik虚拟机的区别

## 应用常驻后台，避免被第三方杀掉的方法，讲讲你用过的奇淫巧技？

## 数据持久化的四种方式有哪些？

## Android的新技术
