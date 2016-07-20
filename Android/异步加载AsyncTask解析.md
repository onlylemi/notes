# 异步加载AsyncTask解析（API:23）

轻量级的异步加载框架。内部实现是以线程池的形式进行。

> **AsyncTask**  
官方文档：[https://developer.android.com/reference/android/os/AsyncTask.html](https://developer.android.com/reference/android/os/AsyncTask.html)  
翻译：[https://github.com/onlylemi/android-source-code-translate/blob/master/android/os/AsyncTask.java](https://github.com/onlylemi/android-source-code-translate/blob/master/android/os/AsyncTask.java)

## 基本用法

它是一个抽象类，需要继承它，同时定义三个参数

### 声明

```java
public abstract class AsyncTask<Params, Progress, Result>{
}
```

* Params —— 执行时要传入的参数类型（例如：下载是传个url地址）
* Progress —— 后台任务执行中进度的类型（例如：传个Integer作为进度值）
* Result —— 异步任务执行结束后的返回值类型（例如：下载图片时返回Bitmap）

### 基本方法

* **onPreExecute()** —— 在执行异步任务之前运行在主线程，在 `executeOnExecutor()` 方法中被调用
* **doInBackground(Params... p)**  —— 异步任务执行的子线程中运行，在 `mWorker` 对象的 `call()` 中被调用
* **onProgressUpdate(Progress... p)** —— `这个方法默认不会调用` 一般在 `doInBackground()` 方法中调用 `publishProgress(Progress... p)` 方法，通过消息机制回调到该方法。

以下这两个方法只会被调用一个，任务正常完成调用第一个，如果中途被取消，调用第二个

* **onPostExecute(Result... r)** —— 该方法在异步任务执行完之后调用，运行在主线程中
* **onCancelled()** —— 当任务被 cancale(true) 的时候

以上四个方法在代码中都不要自己去调用。

### 示例

下载一张网络图片为例，进行说明。源代码可查看 [https://github.com/onlylemi/AndroidTest/tree/master/Test2_AsyncTask](https://github.com/onlylemi/AndroidTest/tree/master/Test2_AsyncTask)，这里展示主要部分。

#### 主Activity

```java
public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;

    private MyAsyncTask asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 显示下载后的图片
        imageView = (ImageView) findViewById(R.id.inage_view);
        // 显示图片下载的进度
        textView = (TextView) findViewById(R.id.text_view);

        String urlStr = "http://ww2.sinaimg.cn/large/610dc034gw1f4hvgpjjapj20ia0ur0vr.jpg";
        asyncTask = new MyAsyncTask(imageView, textView);
        asyncTask.execute(urlStr);
    }
}
```

#### MyAsyncTask类

```java
public class MyAsyncTask extends AsyncTask<String, Integer, Bitmap> {

    private static final String TAG = MyAsyncTask.class.getSimpleName();

    private ImageView imageView;
    private TextView textView;

    public MyAsyncTask(ImageView imageView, TextView textView) {
        this.imageView = imageView;
        this.textView = textView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        textView.setText("0");
        Log.i(TAG, "onPreExecute");
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Log.i(TAG, "doInBackground");

        String urlStr = params[0];
        Bitmap bitmap = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        HttpURLConnection conn = null;
        try {
            out = new ByteArrayOutputStream();

            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = conn.getInputStream();
                //bitmap = BitmapFactory.decodeStream(in);

                int allLength = conn.getContentLength();
                int currentLength = 0;

                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);

                    currentLength += len;
                    // 设置进度，这个方法执行后会执行 onProgressUpdate(Integer... values)
                    publishProgress(currentLength / allLength * 100);
                }
                bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, allLength);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
                if (null != conn) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.i(TAG, "onProgressUpdate");

        // 更新进度
        if (null != textView) {
            textView.setText(values[0].toString());
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.i(TAG, "onPostExecute");

        // 设置图片
        if (null != imageView && null != bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onCancelled(Bitmap bitmap) {
        Log.i(TAG, "onCancelled");

    }
}
```

以上就是 AsyncTask 的一个简单用法，接下来看他的内部实现。

## 源码解析

当我们在使用时，先 new 一个 `MyAsyncTask` 的引用时，会调用 `AsyncTask` 的构造函数

```java
	public AsyncTask() {
        mWorker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                mTaskInvoked.set(true);

                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                //noinspection unchecked
                Result result = doInBackground(mParams);
                Binder.flushPendingCommands();
                return postResult(result);
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
                try {
                    postResultIfNotInvoked(get());
                } catch (InterruptedException e) {
                    android.util.Log.w(LOG_TAG, e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("An error occurred while executing doInBackground()",
                            e.getCause());
                } catch (CancellationException e) {
                    postResultIfNotInvoked(null);
                }
            }
        };
    }
```

在构造函数中就初始化了一个 mWorker(WorkerRunnable)，其实 WorkerRunnable 就是一个 Callable，但是 mParams 并没有被初始化，同时初始化了一个 mFuture(FutureTask) 对象，并把 mWorker 作为参数。

```java
	// 主线程中就是通过这个方法启动异步任务进行执行，
	public final AsyncTask<Params, Progress, Result> execute(Params... params) {
		// 默认传递 sDefaultExecutor，它是一个 SERIAL_EXECUTOR，表示串行执行
        return executeOnExecutor(sDefaultExecutor, params);
    }

	public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec,
            Params... params) {
        if (mStatus != Status.PENDING) {
            switch (mStatus) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task has already been executed "
                            + "(a task can be executed only once)");
            }
        }

        // 设置为运行状态
        mStatus = Status.RUNNING;

        // 调用了第一个方法，初始准备
        onPreExecute();

        // 这是才把参数设置给 mWorker 对象（初始化时没有设置）
        mWorker.mParams = params;

        // 将 FutureTask 交给 SerialExecutor 的 execute 方法
        exec.execute(mFuture);

        return this;
    }
```

> 以上可以看出，有个枚举类型的 Status，表示当前任务的一个执行状态，execute() 只能被调用一次，被再次调用回报异常
* PENDING —— 待定状态。默认为此状态，`mStatus = Status.PENDING`
* RUNNING —— 运行状态。调用了 `execute` 方法之后，在 `executeOnExecutor` 方法中设置为此状态
* FINISHED —— 完成状态。在任务完成之后，handler发送消息，调用 `finish` 方法中设置

程序中调用 `exec.execute(mFuture);` 之后，就开始执行任务，由于 exec 是SERIAL_EXECUTOR，也就是一个 `SerialExecutor` 类对象，看这个类实现

```java
	private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }
```

> 

可以看到，先把 FutureTask 插入到任务队列 tasks 中执行，如果这个时候没有正在活动的 AsyncTask 任务，那么就会执行下一个 AsyncTask 任务，同时当一个 AsyncTask 任务执行完毕之后，AsyncTask 会继续执行其他任务直到所有任务都被执行为止。**从这里就可以看出，默认情况下，AsyncTask是串行执行的**。这样就会调用 Runable 的 run() 方法，然后其中又会调用 mWork 的 call() 方法，也就是在构造函数中的实现，call() 方法中就会调用 doInBackground() 并把结果给了 postResult(result)。

> 关于 SerialExecutor，是串行执行的，按加入的顺序依次执行。容量没有限制
* 里面有一个Runnable队列：mTask
* 两个方法execute(runnable) 和 scheduleNext()，都是加了同步锁的
* 每执行完execute()后会调用scheduleNext()方法
* scheduleNext()方法如果发现mTask还不为null，则调用execute()方法执行。
* SerialExecutor类在执行的时候仍然是在调用ThreadPoolExecutor来执行Runnable 

```java
	private Result postResult(Result result) {
        Message message = getHandler().obtainMessage(MESSAGE_POST_RESULT,
                new AsyncTaskResult<Result>(this, result));
        message.sendToTarget();
        return result;
    }

    private static Handler getHandler() {
        synchronized (AsyncTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult<?> result = (AsyncTaskResult<?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        mStatus = Status.FINISHED;
    }
```

可以看到这个方法通过获取一个 handler 去发送消息 MESSAGE_POST_RESULT。InternalHandler 类中重写了 handleMessage() 方法来处理消息，调用到 finish() 方法，判断当前任务是否被取消，没有取消就会调用 onPostExecute(result) 方法了；如果被取消，则可以通过重写 onCancelled() 方法，执行相关的操作。这样整个任务也就执行结束了。

```java
	protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            getHandler().obtainMessage(MESSAGE_POST_PROGRESS,
                    new AsyncTaskResult<Progress>(this, values)).sendToTarget();
        }
    }
```

而 MESSAGE_POST_PROGRESS 消息是在，用户调用了 publishProgress() 之后才调用的，这样也就调用了 onProgressUpdate() 方法，来更新任务进度。

--- 

```java
	public final boolean cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
        return mFuture.cancel(mayInterruptIfRunning);
    }
```

### 关于调用cancel()方法

* 它会调用内部FutureTask对象的cancel()方法
* 并不会立即终止异步任务的执行
* 异步任务会在doInBackground()方法执行完后结束，不去执行postExecute()，而执行onCancle()

### FutureTask

可以取消的异步运算。

```java
public class FutureTask<V> implements RunnableFuture<V>{}
public interface RunnableFuture<V> extends Runnable, Future<V>{}
public interface Future<V>{}
public interface Runnable<V>{}
```

* 它实现了两个接口：Runnable 和 Future
* 两个构造方法：
	* FutureTask(Callable<V> callable)
	* FutureTask(Runnable runnable, V result)
* 既可以作为Runnable，被线程执行
* 又可以作为Future， 得到Callable的值

由于FutureTask也实现了Runnable接口所以它可以提交给Executor来执行，或者用它来作为new Thread(Runnable)的参数。

```java
public interface Runnable {
    public void run();
}

public interface Callable<V> {
    V call() throws Exception;
}
```

> Callable 和 Runnable 
* Callable 有返回值，抛出异常
* Runnable 无返回值，不抛出异常

### THREAD_POOL_EXECUTOR

```java
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                    TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
```

* 继承了Executor
* 每次并发执行5个线程
* 线程池中最多允许128个线程，多了抛出非法状态异常。

> Thread_Pool_Executor是并行执行的，每次执行5个线程，超过五个的放在等待队列中。队列长度不能超过128个。




