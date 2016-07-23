# 通过 BaseActivity 实现滑动返回

> 原理：  
* 设置 Window 背景为透明
* 移动当前 Activity 的 DecorView（view.setX()）
* 处理滑动冲突


## 设置 Window 为透明

应用透明主题到需要滑动返回的 Activity

```xml
	<!-- 透明主题 -->
	<style name="Transparent" parent="AppTheme">
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:colorBackgroundCacheHint">@null</item>
    </style>
```

## 获取 DecorView

在 Activity 中可以直接获取，

```java
decorView = getWindow().getDecorView();
// window 为透明，需要给 decorview 设置一个背景
decorView.setBackgroundColor(Color.parseColor("#fff9f9f9"));
```

## 滑动冲突

`Activity` 中仅有 `dispatchTouchEvent()`、`onTouchEvent()` 方法，没有 `onInterceptEvent` 方法，默认不拦截消息，直接向下传递，除非该 `Touch` 事件没有被消费，才会执行 `Activity` 的 `onTouchEvent()` 方法。所以既然要实现侧滑退出，我们就需要在 `dispatchTouchEvent()` 中拦截消息，进行分发。

```java
	@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() == startTouchX) {
                isSliding = false;
            }
        }
        if (isSliding) {
            return onTouchEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startTouchX = event.getX();
            if (startTouchX < 50) {
                // 判断为侧滑退出事件
                isSliding = true;
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: x=" + event.getX());

        if (isSliding) {
            float moveDistX = event.getX() - startTouchX;

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE: {
                    // 随着手指移动
                    if (moveDistX > 0) {
                        decorView.setX(moveDistX);
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    // 滑动屏幕一半时处理
                    if (moveDistX > screenW / 2) {
                        finish();
                    } else {
                        decorView.setX(0);
                    }
                    isSliding = false;
                    break;
                }
            }
        }
        return super.onTouchEvent(event);
    }
```

## 添加动画效果

当手指抬起时，为 decorView 的移动添加动画，完整代码

```java
/**
 * 滑动返回
 *
 * @author: onlylemi
 * @time: 2016-07-22 20:04
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private View decorView;

    private float startTouchX;
    private int screenW;

    private boolean isSliding = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        decorView = getWindow().getDecorView();
        decorView.setBackgroundColor(Color.parseColor("#fff9f9f9"));

        screenW = AppUtils.screeWidth(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() == startTouchX) {
                isSliding = false;
            }
        }
        if (isSliding) {
            return onTouchEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startTouchX = event.getX();
            if (startTouchX < 50) {
                // 判断为侧滑退出事件
                isSliding = true;
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: x=" + event.getX());

        if (isSliding) {
            float moveDistX = event.getX() - startTouchX;

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE: {
                    // 随着手指移动
                    if (moveDistX > 0) {
                        decorView.setX(moveDistX);
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    // 滑动屏幕一半时处理
                    if (moveDistX > screenW / 2) {
                        finish(moveDistX);
                    } else {
                        ObjectAnimator.ofFloat(decorView, "x", 0).start();
                    }
                    isSliding = false;
                    break;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 退出的动画
     *
     * @param dist
     */
    public void finish(float dist) {
        ValueAnimator animator = ValueAnimator.ofFloat(dist, screenW);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                decorView.setX(x);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }
        });
    }
}
```