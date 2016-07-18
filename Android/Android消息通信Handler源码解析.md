# Android消息通信Handler源码解析

在 Android 中我们通过 Handler 进行主线程与子线程之间的消息通信，对 UI 视图的更新等。

## 相关类

![](https://raw.githubusercontent.com/onlylemi/res/master/android_handler.png)

* `Handler` —— 发送消息 sendMessage 与处理消息 handleMessage
* `Looper` —— 负责消息的分发
* `MessageQueue` —— 消息队列，handler 发送的消息都会存到这个消息队列中，通过 Looper.loop() 去分发消息
* `Message` —— 消息对象

## 

## 使用

在子线程中，通过 Handler 更新 UI 有四种方式。以更新 TextView 的值为参考，以下部分知识主要代码，如果我们直接在子线程中直接更新 UI 线程，就会下面的异常

### 异常

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

### 子线程更新 UI 方式

> 项目源代码请看这里：[https://github.com/onlylemi/AndroidTest/tree/master/Test1_Handler](https://github.com/onlylemi/AndroidTest/tree/master/Test1_Handler)

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

## 子线程中生成一个 Handler

```java
	class MyThread extends Thread {

        public Handler handler1;
        public Looper looper;

        @Override
        public void run() {
            Looper.prepare();
            looper = Looper.myLooper();

            handler1 = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Log.i(TAG + " thread:", Thread.currentThread().getName());

                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            };
            Looper.loop();
            looper.quit();
        }
    }
```

## 源码解析

> **关于Looper类是如何利用ThreadLocal的**  
* sThreadLocal 是一个 ThreadLooper 静态对象
* 每个线程在创建looper对象时，sThreadLocal都会将新建的looper对象set()一遍。
* 每个线程在调用Looper.myLooper()时，就用调用sThreadLocal的get()方法，获得当前线程的looper对象。

### new Handler()

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

但是会发现在 UI 线程中我们并没有去生成一个 looper，但是也没有报异常，这是因为，Android 在启动的过程中，就会默认为 UI 线程生成一个 Looper 对象，在 `ActivityThread` 中会发现在 `main()` 中调用 `Looper.prepareMainLooper()` 方法

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

当我们在子线程中更新 UI 的时候，Handler内部发生了什么，我们在主线程中是这样使用，通过 sendMessage() 发送消息，然后在 handlerMessage() 中处理消息

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
    	// 子线程中发送一个 handler 消息，通过 handler 去处理这个消息，通过 handlerMessage() 方法
		handler.sendEmptyMessage(1);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

### sendMessage()

在这里我们采用，`handler.sendEmptyMessage(1)` 去发送一个空消息

```java
	// 发送一个空的消息
	public final boolean sendEmptyMessage(int what) {
        return sendEmptyMessageDelayed(what, 0);
    }

    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
    	// 内部也会把这个消息 封装成一个消息对象
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageDelayed(msg, delayMillis);
    }

    // 而对于 sendMessage 直接发送传递的消息就可以
    public final boolean sendMessage(Message msg){
        return sendMessageDelayed(msg, 0);
    }

    // 消息发送的延迟时间 delayMillis
    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
    	// 对于 handler 发送的消息，都会存放在一个 MessageQueue 消息队列中
        MessageQueue queue = mQueue;
        if (queue == null) {
            RuntimeException e = new RuntimeException(
                    this + " sendMessageAtTime() called with no mQueue");
            Log.w("Looper", e.getMessage(), e);
            return false;
        }
        return enqueueMessage(queue, msg, uptimeMillis);
    }

    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
    	// 每个消息都会有一个 target，相当于这个消息将来会发给谁，默认为 this，也就是 每个 handler 自己发送的消息自己将来去处理
        msg.target = this;
        if (mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }

    // MessageQueue 的 enqueueMessage 方法在 ide 下不能查看，我们通过 Source Insight 可以看到，
    // 将我们发送的每一个消息通过 Message 的 next 连接成链表，维护在消息队列中
    boolean enqueueMessage(Message msg, long when) {
    	// 这儿就可以看出，每个 msg 必须有它的 target
        if (msg.target == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        if (msg.isInUse()) {
            throw new IllegalStateException(msg + " This message is already in use.");
        }

        synchronized (this) {
            if (mQuitting) {
                IllegalStateException e = new IllegalStateException(
                        msg.target + " sending message to a Handler on a dead thread");
                Log.w(TAG, e.getMessage(), e);
                msg.recycle();
                return false;
            }

            msg.markInUse();
            msg.when = when;
            Message p = mMessages;
            boolean needWake;
            if (p == null || when == 0 || when < p.when) {
                // New head, wake up the event queue if blocked.
                msg.next = p;
                mMessages = msg;
                needWake = mBlocked;
            } else {
                // Inserted within the middle of the queue.  Usually we don't have to wake
                // up the event queue unless there is a barrier at the head of the queue
                // and the message is the earliest asynchronous message in the queue.
                needWake = mBlocked && p.target == null && msg.isAsynchronous();
                Message prev;
                for (;;) {
                    prev = p;
                    p = p.next;
                    if (p == null || when < p.when) {
                        break;
                    }
                    if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                msg.next = p; // invariant: p == prev.next
                prev.next = msg;
            }

            // We can assume mPtr != 0 because mQuitting is false.
            if (needWake) {
                nativeWake(mPtr);
            }
        }
        return true;
    }
```

这样我们发送的消息，全都存到了 MessageQueue 中，我们先了解下 Message

### Message

handler 在发送消息的时候，sendMessage() 方法会传递一个 Message 对象

* `what` —— 消息的标识
* `arg1`、`arg1`  —— 提供的两个传递简单 int 型参数
* `obj` —— 传递的一个 Object 对象
* `date` —— Bundle 对象。把需要传递的数据封装到 Bundle 中进行传递
* `target` —— Handler对象。消息最终传递的目标 Handler，在 Looper.loop() 使用该 target 来回调 handler 的 dispatchMessage() 方法
* `callback` —— Runnable 对象。当我们在子线程中采用 post 方式更新 UI 时，需要用它来回调

```java
// 生成一个 msg 对象时，建议使用 obtain() 方法，也就是单例模式
// 也可以使用 new Message()
Message msg = Message.obtain();
msg.arg1 = 1;
// 发送消息
handler.sendMessage(msg);
```

### Looper.loop()

我们 handler 发送的所有消息都储存在 MessageQueue 消息队列中，再通过 `Looper.loop()` 去分发这些消息

```java
	public static void loop() {
		// 首先获取当前线程的 looper 对象
        final Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        // 通过，looper 对象获取该线程的消息队列
        final MessageQueue queue = me.mQueue;

        // Make sure the identity of this thread is that of the local process,
        // and keep track of what that identity token actually is.
        Binder.clearCallingIdentity();
        final long ident = Binder.clearCallingIdentity();

        // 消息处理的时候是个死循环，知道所有消息都处理完毕
        for (;;) {
            Message msg = queue.next(); // might block
            if (msg == null) {
                // 没有消息时，直接退出
                return;
            }

            // This must be in a local variable, in case a UI event sets the logger
            Printer logging = me.mLogging;
            if (logging != null) {
                logging.println(">>>>> Dispatching to " + msg.target + " " +
                        msg.callback + ": " + msg.what);
            }

            // 通过 msg 的 target 去回调该消息 handler 的 dispatchMessage() 方法
            msg.target.dispatchMessage(msg);

            if (logging != null) {
                logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
            }

            // Make sure that during the course of dispatching the
            // identity of the thread wasn't corrupted.
            final long newIdent = Binder.clearCallingIdentity();
            if (ident != newIdent) {
                Log.wtf(TAG, "Thread identity changed from 0x"
                        + Long.toHexString(ident) + " to 0x"
                        + Long.toHexString(newIdent) + " while dispatching to "
                        + msg.target.getClass().getName() + " "
                        + msg.callback + " what=" + msg.what);
            }

            msg.recycleUnchecked();
        }
    }

    // handler 的 dispatchMessage 方法
    public void dispatchMessage(Message msg) {
    	// 首先判断 msg 的 callback 是否为 null，为空时直接调用 handleCallback(msg) 方法
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            // 会回调 handleMessage() 方法，我们在 new Handler() 的时候必须重写这个方法
            handleMessage(msg);
        }
    }

    // 在 handleCallback 方法中直接调用 run() 方法
    private static void handleCallback(Message message) {
        message.callback.run();
    }
```

在开头更新 UI 的其他几种 post 方式，查看源码你会发现，最终都会调用到 handler.post() 这个方法，

```java
	// post 方法也会调用 sendMessageDelayed() 去发送消息，
	// 不过先需要 getPostMessage(r) 去封装这个消息对象
	public final boolean post(Runnable r) {
       return  sendMessageDelayed(getPostMessage(r), 0);
    }

    private static Message getPostMessage(Runnable r) {
        Message m = Message.obtain();
        // 会把你传递的 runnable 对象，赋给 msg 的 callback
        // 这样在 dispatchMessage 中判断的时候就会回调 handleCallback(msg) 方法
        m.callback = r;
        return m;
    }
```

> 从以上我们会发现 `callback` 直接去调用 `run()` 方法，所以在这里 `post(new Runnable())` 其实就是一个普通的回调函数，千万不要当成是新开一个线程

### HandlerThread

Activity 中我们 new handler() 对象的消息处理都是在主线程中，当我们需要让 handler 的消息处理在子线程中时，我们需要在生成 handler 对象时传递子线程中的一个 looper 对象，如果我们自己在子线程中生成一个 looper，然后给 hander 使用时，因为线程的不同步，我们并不能保证在 new handler 是，looper 已经在子线程中生成了，我们就需要用到 `HandlerThread`，他帮我们解决这个问题

**不使用 HandlerThread**

```java
    private MyThread thread;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("SecondActivity");
        setContentView(textView);

        thread = new MyThread();
        thread.start();

        // looper 你确定真的已经生成了吗？（线程不同问题，可能会报空指针异常）
        handler = new Handler(thread.looper) {
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG + " thread:", Thread.currentThread().getName());
            }
        };
        handler.sendEmptyMessage(1);
    }

    class MyThread extends Thread {

        public Looper looper;

        @Override
        public void run() {
            // 生成 looper 对象
            Looper.prepare();
            // 获取 looper 对象
            looper = Looper.myLooper();
            // 分发消息
            Looper.loop();
        }
    }
```

可能就像上面一样，你会这样去写，这样的结果就是可能会报空指针异常，看这里代码

```java
    public Handler(Looper looper) {
        this(looper, null, false);
    }

    public Handler(Looper looper, Callback callback, boolean async) {
        mLooper = looper;
        // looper 如果为 null 就会报空指针啊
        mQueue = looper.mQueue;
        mCallback = callback;
        mAsynchronous = async;
    }
```

**使用 HandlerThread**

```java
    private HandlerThread thread;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("ThirdActivity");
        setContentView(textView);

        thread = new HandlerThread("Handler Thread");
        thread.start();

        // handler = new Handler() {
        handler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG, "thread: " + Thread.currentThread().getName());

                // 空参数时，输出：main （UI 主线程）
                // 有参数时，输出：Handler Thread （子线程）
            }
        };

        handler.sendEmptyMessageDelayed(1, 1000);
    }
```

如果采用 `HandlerThread` 去获取 looper 一定会获取到，因为加了同步锁

```java
    public Looper getLooper() {
        if (!isAlive()) {
            return null;
        }        
        
        synchronized (this) {
            while (isAlive() && mLooper == null) {
                try {
                    // 如果 looper 为空，进入 wait 状态，直到被调用 notify()或notifyAll()
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return mLooper;
    }

    @Override
    public void run() {
        mTid = Process.myTid();
        Looper.prepare();
        synchronized (this) {
            mLooper = Looper.myLooper();
            // 当获取到 looper 对象时，调用 notifyAll，保证了 getLooper() 获取到的 looper 一定不为 null
            notifyAll();
        }
        Process.setThreadPriority(mPriority);
        onLooperPrepared();
        Looper.loop();
        mTid = -1;
    }
```