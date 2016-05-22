# Android消息通信Handler源码解析

在 Android 中我们通过 Handler 进行主线程与子线程之间的消息通信，对 UI 视图的更新等。

## 相关类

![](https://raw.githubusercontent.com/onlylemi/img/master/android_handler.png)

* `Handler` —— 发送消息 sendMessage 与处理消息 handleMessage
* `Looper` —— 负责消息的分发
* `MessageQueue` —— 消息队列，handler 发送的消息都会存到这个消息队列中，通过 Looper.loop() 去分发消息
* `Message` —— 消息对象

## 

## 使用

在子线程中，通过 Handler 更新 UI 有四种方式。以更新 TextView 的值为参考，以下部分知识主要代码，如果我们直接在子线程中直接更新 UI 线程，就会下面的异常

* **异常1**

```
Process: com.onlylemi.test1_handler, PID: 1682
	android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
```

* **异常2**


```
Process: com.onlylemi.test1_handler, PID: 1982
	java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
```

> 源代码请看这里：[https://github.com/onlylemi/AndroidDemo/Test1_Handler](https://github.com/onlylemi/AndroidDemo/Test1_Handler)

* 第1种：handler.post()

```java
public void run() {
    try {
    	Thread.sleep(2000);
		handler.post(new Runnable() {
    		@Override
    		public void run() {
       			textView.setText("Text View1");
    		}
		});
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

* 第2种：handler.sendMessage()

```java
private Handler handler = new Handler(){
	@Override
    public void handleMessage(Message msg) {
		textView.setText("Text View2");
    }
};


public void run() {
    try {
    	Thread.sleep(2000);
    	// 子线程中发送一个 handler 消息，通过 handler 去处理这个消息
		handler.sendEmptyMessage(1);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

* 第3种：runOnUiThread()

```java
public void run() {
    try {
    	Thread.sleep(2000);
		runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
       			textView.setText("Text View1");
    		}
		});
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

* 第4种：textView.post()

```java
public void run() {
    try {
    	Thread.sleep(2000);
		textView.post(new Runnable() {
    		@Override
    		public void run() {
       			textView.setText("Text View1");
    		}
		});
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

## 源码解析

当我们在生成一个 Handler 对象前，必须调用 Looper.prepare() 方法，去检查我们当前的线程中是否已经存在一个 Looper 对象，不存在时就去去创建。

```java
	// 当我们在一个 Handler 对象的时候，总会执行到这个构造函数
	public Handler(Callback callback, boolean async) {
        if (FIND_POTENTIAL_LEAKS) {
            final Class<? extends Handler> klass = getClass();
            if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&
                    (klass.getModifiers() & Modifier.STATIC) == 0) {
                Log.w(TAG, "The following Handler class should be static or leaks might occur: " +
                    klass.getCanonicalName());
            }
        }

        // 在这里会 Looper 去获取当前线程中的 looper 对象
        mLooper = Looper.myLooper();
        // 当这个对象为 null 时，就会报【异常2】，所以我们要在 生成 handler 对象之前去生成一个 Looper
        if (mLooper == null) {
            throw new RuntimeException(
                "Can't create handler inside thread that has not called Looper.prepare()");
        }
        mQueue = mLooper.mQueue;
        mCallback = callback;
        mAsynchronous = async;
    }

    // 调用 prepare() 方法，生成一个 Looper 实例
    public static void prepare() {
        prepare(true);
    }

    private static void prepare(boolean quitAllowed) {
    	// 获取当前的线程的 looper 对象，不为 null 时，报异常提示不用重复 prepare()
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        // 若为 null 时，会重新生成一个 Looper 对象，所以 prepare() 就是为了生成一个 looper
        sThreadLocal.set(new Looper(quitAllowed));
    }
```

但是会发现在 UI 线程中我们并没有去生成一个 looper，但是也没有报异常，这是因为，Android 在启动的过程中，就会默认为 UI 线程生成一个 Looper 对象，在 `ActivityThread` 中调用 `Looper.prepareMainLooper()` 方法

```java
	// 应用启动时会调用 ActivityThread 的 main() 入口
	public static void main(String[] args) {
        // 省略之前代码...

		// 调用此方法，生成一个 UI 线程的一个 Looper 对象
        Looper.prepareMainLooper();

        ActivityThread thread = new ActivityThread();
        thread.attach(false);

        if (sMainThreadHandler == null) {
            sMainThreadHandler = thread.getHandler();
        }

        if (false) {
            Looper.myLooper().setMessageLogging(new
                    LogPrinter(Log.DEBUG, "ActivityThread"));
        }

        // End of event ActivityThreadMain.
        Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);

        // 分发消息
        Looper.loop();

        throw new RuntimeException("Main thread loop unexpectedly exited");
    }

    // 再看 Looper.prepareMainLooper() 方法的实现，也是调用了 prepare() 所以就明白，在 UI 线程中，我们不需要再 调用 Looper.prepare() 去生成 Looper 实例
    public static void prepareMainLooper() {
        prepare(false);
        synchronized (Looper.class) {
            if (sMainLooper != null) {
                throw new IllegalStateException("The main Looper has already been prepared.");
            }
            sMainLooper = myLooper();
        }
    }
```

> 从上面我们会发现，当我们自己调用 `prepare()` 生成一个 `Looper` 对象时，内部实现调用的是 `prepare(true)`，而在 `ActivityThread` 中调用 `prepareMainLooper()` 方法时，调用的是 `prepare(false)`。其实这里的参数的意思代表我们当前线程的 `Looper` 是否需要 `quit`，默认认为 UI 线程的 Looper 不可以，所以为 `false`；子线程中的 `Looper` 可以退出，所以为 `true`。

