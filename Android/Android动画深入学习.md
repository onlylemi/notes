# Android动画深入学习

> 参考任玉刚的《Android开发艺术探索》第七章  
学习代码：[https://github.com/onlylemi/AndroidTest/tree/master/Test8_Animation](https://github.com/onlylemi/AndroidTest/tree/master/Test8_Animation)  
View动画库可参考代码家的 [AndroidViewAnimations](https://github.com/daimajia/AndroidViewAnimations)

* View 动画
* 帧动画
* 属性动画（Property Animation）3.0 以后加入

## View 动画

> 特点：view 经过变化后，其真实位置仍然在原位置，例如加入点击事件后，平移改变后的 view 不具有点击时间，点击仍然在原位置（假象），该动画是在 `draw` 期间进行矩阵变换，并不会改变控件的实际参数

* translate —— TranslateAnimation
* scale —— ScaleAnimation
* rotate —— RotateAnimation
* alpha —— AlphaAnimation

这四种动画都可以在 XML 中声明（`res/anim/`），在 XML 文件中 `<set` 标签对应于 `AnimationSet` 类，其他的就是动画标签。也可以在代码实现，分别对应其后的四个类。

### translate 平移

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
     android:duration="1000"
     android:interpolator="@android:anim/accelerate_decelerate_interpolator">

    <translate
        android:fromXDelta="0"
        android:fromYDelta="0"
        android:toXDelta="200"
        android:toYDelta="200"/>

</set>
```

**AnimationSet 的一些属性**

* duration —— 时间长
* interpolator —— 插值器，影响动画的速度
* shareInterpolator —— 子标签动画是否共享同一个插值器

**TranslateAnimation 的一些属性** （都是相对与 View 的左上点，而不是屏幕的左上点）

* fromXDelta —— x 的起始值。例 0
* fromYDelta —— y 的起始值。例 0
* toXDelta —— x 的结束值。例 200
* toYDelta —— y 的结束值。例 200

```java
		// 平移，布局加载
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.anim_translate);

        // 平移，代码设置
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 200, 200);
        translateAnimation.setDuration(1000);
        // 移动之后保留状态（默认为不保留）
        translateAnimation.setFillAfter(true);

        // view 启动动画
        view.startAnimation(alphaAnimation);
```

## scale 缩放

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
     android:duration="1000"
     android:interpolator="@android:anim/accelerate_decelerate_interpolator">

    <scale
        android:fromXScale="1.0"
        android:fromYScale="1.0"
        android:pivotX="100"
        android:pivotY="50"
        android:toXScale="0.1"
        android:toYScale="0.1"/>

</set>
```

**ScaleAnimation 的一些属性** （都是相对与 View 的左上点，而不是屏幕的左上点）

* fromXScale —— 水平方向缩放的起始值。例如 1.0（也就是整个 width）
* fromYScale —— 垂直方向缩放的起始值。例如 1.0
* pivotX —— 缩放的轴心点 x。例如 100 （默认为 0，而不是 view 的中心点，[书中存在错误](https://github.com/singwhatiwanna/android-art-res/issues/22)，根据源码得到）
* pivotY —— 缩放的轴心点 y。例如 50
* toXScale —— 水平方向缩放的结束值。例如 0.0（也就是最后 view 没 width 了）
* toYScale —— 垂直方向缩放的结束值。例如 0.0

```java
		// 缩放 xml 加载
		Animation animation3 = AnimationUtils.loadAnimation(this, R.anim.anim_scale);

		// 缩放 代码设置，这里缩放轴心点为 View 的中心点
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0, view.getWidth() / 2,
                view.getHeight() / 2);
        scaleAnimation.setDuration(1000);
        // 设置重复次数
        scaleAnimation.setRepeatCount(5);
        // 重复模式，正/逆
        scaleAnimation.setRepeatMode(Animation.REVERSE);

        // view 启动动画
        view.startAnimation(alphaAnimation);
```

## rotate 旋转

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
     android:duration="1000"
     android:interpolator="@android:anim/accelerate_decelerate_interpolator">

    <rotate
        android:fromDegrees="0"
        android:pivotX="100"
        android:pivotY="50"
        android:toDegrees="360"/>

</set>
```

**RoatateAnimation 的一些属性**

* fromDegrees —— 旋转开始的角度。例 0
* pivotX —— 旋转的轴心点 x。例如 100
* pivotY —— 旋转的轴心点 y。例如 50
* toDegrees —— 旋转结束的角度。例 360 （就是转了一圈）

```java
		// 旋转 xml加载
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        // 旋转 代码设置
        RotateAnimation rotateAnimation = new RotateAnimation(360, 0);
        rotateAnimation.setDuration(1000);

        // view 启动动画
        view.startAnimation(alphaAnimation);
```

## alpha 透明

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
     android:duration="1000"
     android:interpolator="@android:anim/accelerate_decelerate_interpolator">

    <alpha
        android:fromAlpha="0.0"
        android:toAlpha="1.0"/>

</set>
```

**AlphaAnimation 的一些属性**

* fromAlpha —— 透明度的起始值。例 0.0（透明）
* toAlpha —— 透明度的结束值。例 1.0（不透明）

```java
		// 透明，xml加载
        Animation animation4 = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        // 透明，代码设置
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(1000);

        // view 启动动画
        view.startAnimation(alphaAnimation);
```

### 自定义 View 动画

继承 Animation，重写 initialize() 和 applyTransformation() 方法。

* initialize() —— 进行初始化操作
* applyTransformation() —— 进行矩阵变换操作

### LayoutAnimation 作用于 ViewGroup

LayoutAnimation 为 ViewGroup 指定一个动画后，它的子元素出场时都会具有这种动画。这种效果一般可以作用于 listview。

以为 listview 的 item 设置动画

* 1. 定义 item 入场的动画

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
     android:duration="300"
     android:interpolator="@android:anim/accelerate_interpolator"
     android:shareInterpolator="true">

    <alpha
        android:fromAlpha="0.0"
        android:toAlpha="1.0"/>

    <translate
        android:fromXDelta="500"
        android:toXDelta="0" />

</set>
```

* 2. 定义 LayoutAnimation 

```xml
<?xml version="1.0" encoding="utf-8"?>
<layoutAnimation
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:animation="@anim/anim_item"
    android:animationOrder="normal"
    android:delay="0.5">

</layoutAnimation>
```
> 
* animation —— 入场动画
* animationOrder —— 动画的顺序（normal：依次显示，reverse：逆序显示，random：随机显示）
* delay —— 开始动画的延迟时间

* 3. 为 listview 指定 LayoutAnimation

xml 方式

```xml
    <ListView
        android:id="@+id/listview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layoutAnimation="@anim/layout_anim"/>
```

代码中指定

```java
        // listview item 动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_item);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        listView.setLayoutAnimation(controller);
```

### Activity 切换动画效果

Activity 自定义切换动画效果主要用到 `overridePendingTransition(enterAnim, exitAnim)` 这个方法。这个歌方法必须在 `startActivity` 和 `finish()` 之后调用才能生效。

```java
// activity 跳转动画
Intent intent = new Intent(MainActivity.this, SecondActivity.class);
startActivity(intent);
// 必须在startActivity之后调用
overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);

// 退出
public void finish() {
    super.finish();
    overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
}
```


## 帧动画

> 顺序播放一组预定义的动画，容易引起 OOM，尽量避免大图片

在 XML 文件中使用 `<animation-list` 定义一组图片。存放在 `res/drawable/`

```xml
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:drawable="@android:drawable/ic_menu_share"
        android:duration="500"/>
    <item
        android:drawable="@android:drawable/ic_menu_today"
        android:duration="500"/>
    <item
        android:drawable="@android:drawable/ic_menu_add"
        android:duration="500"/>
    <item
        android:drawable="@android:drawable/ic_menu_agenda"
        android:duration="500"/>
    <item
        android:drawable="@android:drawable/ic_menu_always_landscape_portrait"
        android:duration="500"/>
</animation-list>
```

在代码中调用使用，得到一个 AnimationDrawable 对象，然后 start

```java
        // 设置背景
        view.setBackgroundResource(R.drawable.frame_animation);
        AnimationDrawable drawable = (AnimationDrawable) view.getBackground();
        drawable.start();
```

## 属性动画

属性动画 Property Animation 是 API 11（3.0）新加入的特性，属性动画可以对任何对象做动画（一般还是对 View）。

> 与 View 动画不同，属性动画改变其属性，view 的属性真正改变，而不是属性动画的假象那样。在3.0 以下，可以采用 [nineoldandroids](http://nineoldandroids.com) 库进行兼容低版本。在低版本本质任然是 View 动画，看起来为属性动画。

```java
// view 向下平移 500
ObjectAnimator.ofFloat(view, "translationY", 500).start();
```

> **特别注意：**ofFloat、ofInt... 依据属性的类型，属性是 int 型就用 ofInt，属性是 float 型就用 ofFloat **必须这么用**，否则动画没效果。

### 属性动画使用条件

* 该对象必须具有某属性的 get 、set 方法。因为内部实现就是通过反射获取属性的 get 、set 方法调用进行设置的。

### View一般属性

* 坐标：x，y
* 透明度：alpha
* 背景颜色：backgroundColor
* 旋转：rotationX, rotationY
* 平移：translationX、translationY
* 缩放：scaleX、scaleY

### View特殊属性

* 宽：width
* 高：height

**View不存在setWidth()、setHeight()方法，所以不能直接对width进行懂动画**，所以需要添加 setXXX 方法

#### 方法1（推荐）

对 view 进行装饰，提供 setWidth、setHeight 方法。提供 ViewWrapper 类（装饰者模式）

```java
import android.view.View;

/**
 * ViewWrapper
 * 
 * 装饰 view，给 view 赋予setWidth、getWidth、setHeight、getHeight等方法，
 * 供属性动画中使用改变 view 的 width、height
 *
 * @author: onlylemi
 */
public class ViewWrapper {

    private View view;

    private ViewWrapper(View view) {
        this.view = view;
    }

    /**
     * 装饰 view
     *
     * @param view
     * @return
     */
    public static ViewWrapper decorator(View view) {
        return new ViewWrapper(view);
    }

    public int getHeight() {
        return view.getLayoutParams().height;
    }

    public void setHeight(int height) {
        view.getLayoutParams().height = height;
        view.requestLayout();
    }

    public int getWidth() {
        return view.getLayoutParams().width;
    }

    public void setWidth(int width) {
        view.getLayoutParams().width = width;
        view.requestLayout();
    }
}

```

具体使用

```java
ObjectAnimator.ofInt(ViewWrapper.decorator(view), "width", 800).start();
```

#### 方法2

采用 ValueAnimator 的 addUpdateListener 监听器方法动态更新

```java
    ValueAnimator valueAnimator = ValueAnimator.ofInt(view.getWidth(), 800);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animator) {
            int value = (Integer) animator.getAnimatedValue();
            // 拿到值以后自己处理
            view.getLayoutParams().width = value;
            view.requestLayout();
        }
    });

```

#### 方法3

既然 width、height 无法改变，那就可以改变top、left、bottom、right

```java
    AnimatorSet animatorSet = new AnimatorSet();
    int widthDelta = 800 - view.getWidth();
    animatorSet.playTogether(
        ObjectAnimator.ofInt(view, "left", view.getLeft() - widthDelta / 2),
        ObjectAnimator.ofInt(view, "right", view.getRight() - widthDelta / 2)
    );
    animatorSet.start();
```

可以实现效果，不过 view 中的内容不会改变（例一个button中设置文本后，改变width后它的内容还应该居中显示，但是这种方法还保留原先的内容位置）

### 执行多个动画

#### ValueAnimation

```java
    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
    valueAnimator.setInterpolator(new DecelerateInterpolator());
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

        // 持有一个IntEvaluator对象，方便下面估值的时候使用
        private IntEvaluator mEvaluator = new IntEvaluator();

        @Override
        public void onAnimationUpdate(ValueAnimator animator) {
            // 获得当前动画的进度值，整型，1-100之间
            int currentValue = (Integer) animator.getAnimatedValue();
            // 获得当前进度占整个动画过程的比例，浮点型，0-1之间
            float fraction = animator.getAnimatedFraction();

            // 直接调用整型估值器通过比例计算出宽度，然后再设给 view
            target.getLayoutParams().width = mEvaluator.evaluate(fraction, view.getWidth(), 800);
            target.getLayoutParams().height = mEvaluator.evaluate(fraction, view.getHeight(), 500);
            target.requestLayout();
        }
    });
    valueAnimator.start();
```

#### AnimatorSet

```java
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(
        ObjectAnimator.ofInt(ViewWrapper.decorator(view), "width", 800),
        ObjectAnimator.ofInt(ViewWrapper.decorator(view), "height", 500)
    );
    animatorSet.start();
```

动画的执行顺序

* 同时执行：playTogether(Animator... anims)
* a1在a2后执行：play(a1).after(a2)
* a1在a2前执行：play(a1).before(a2)
* 依次执行：playSequentially(Animator... Anims)

#### PropertyValuesHolder

```java
    ObjectAnimator.ofPropertyValuesHolder(ViewWrapper.decorator(view),
            PropertyValuesHolder.ofInt("width", 800),
            PropertyValuesHolder.ofInt("height", 500)
    ).start();
```

### 动画的监听

* AnimatorUpdateListener —— 数值变化的监听
* AnimatorListener —— 生命周期的监听
* AnimatorPauseListener —— 暂停与恢复的监听
* AnimatorListenerAdapter —— 生命周期的监听

## 插值器和估值器

> 其实这两个东西没啥区别，都是做一些数学运算计算值  
>> **可参考**  
* [@hujiaweibujidao](https://github.com/hujiaweibujidao) 的 [当数学遇上动画](http://hujiaweibujidao.github.io/blog/2016/05/26/when-math-meets-android-animation/) 三部曲中对插值器和估值器的介绍
* [@代码家](https://github.com/daimajia) 的 [AnimationEasingFunctions](https://github.com/daimajia/AnimationEasingFunctions)
* [easings.net](http://easings.net/)
* [EaseInterpolator](https://github.com/cimi-chen/EaseInterpolator)

### 插值器

根据时间流逝的百分比来计算出当前属性改变的百分比。LinearInterpolator、AccelerateDecelerateInterpolator...等。描述动画改变的一种变换状态

```java
// 线性插值器（y=x）的类实现，getInterpolation 方法，不同的插值器返回不同的结果
public class LinearInterpolator extends BaseInterpolator implements NativeInterpolatorFactory {

    public LinearInterpolator() {
    }

    public LinearInterpolator(Context context, AttributeSet attrs) {
    }

    public float getInterpolation(float input) {
        return input;
    }

    /** @hide */
    @Override
    public long createNativeInterpolator() {
        return NativeInterpolatorFactoryHelper.createLinearInterpolator();
    }
}
```

### 估值器

根据当前属性改变的百分比来计算改变后的属性值。IntEvaluator、FloatEvaluator、ArgbEvaluator

```java
// int 属性的估值器
public class IntEvaluator implements TypeEvaluator<Integer> {

    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction * (endValue - startInt));
    }
}
```