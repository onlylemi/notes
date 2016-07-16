# Picasso 图片加载框架的深入学习及解析

[Picasso](https://github.com/square/picasso)、[Glide](https://github.com/bumptech/glide)、[Fresco](https://github.com/facebook/fresco) 都是很好异步图片加载框架。官网地址：[http://square.github.io/picasso/](http://square.github.io/picasso/)

> 版本：2.5.2

## 使用

使用很简单，就一句话。

```java
	Picasso.with(this)
			.load(url)
      .into(imageview);
```

## 解析

分开来看这三行代码所做了什么。

### with(this)

这个函数返回的是一个 `Picasso` 对象。

```java
  public static Picasso with(Context context) {
    if (singleton == null) {
      synchronized (Picasso.class) {
        if (singleton == null) {
          singleton = new Builder(context).build();
        }
      }
    }
    return singleton;
  }
```

`with(this)` 其实就是生成一个单例的 `Picasso` 对象。内部通过 `Build` 的方法创建

```java
	public Builder(Context context) {
      if (context == null) {
        throw new IllegalArgumentException("Context must not be null.");
      }
      // context 为 applicationContext 对象
      this.context = context.getApplicationContext();
    }

    public Picasso build() {
      Context context = this.context;

      if (downloader == null) {
      	// 创建 Downloader 时，用反射去检测，如果项目集成了okhttp 包，
      	// 则用 okhttp 包中的类去创建一个OkHttpDownloader 下载器，
      	// 如果项目未集成 okhttp，则使用自带的 UrlConnectionDownloader 创建。
        downloader = Utils.createDefaultDownloader(context);
      }
      if (cache == null) {
      	// lru 内存缓存
        cache = new LruCache(context);
      }
      if (service == null) {
      	// 线程池 默认线程数量3。
        service = new PicassoExecutorService();
      }
      if (transformer == null) {
        transformer = RequestTransformer.IDENTITY;
      }

      Stats stats = new Stats(cache);

      // 调度器，上面参数都会来构造这个对象
      Dispatcher dispatcher = new Dispatcher(context, service, HANDLER, downloader, cache, stats);

      return new Picasso(context, dispatcher, cache, listener, transformer, requestHandlers, stats,
          defaultBitmapConfig, indicatorsEnabled, loggingEnabled);
    }
```

从 Picasso 的创建过程中，下载器、缓存、线程池、调度器都对象都会被创建下载。

### load(url)

这个函数返回的一个 `RequestCreator` 对象。

```java
  public RequestCreator load(String path) {
    if (path == null) {
      return new RequestCreator(this, null, 0);
    }
    if (path.trim().length() == 0) {
      throw new IllegalArgumentException("Path must not be empty.");
    }
    return load(Uri.parse(path));
  }

  public RequestCreator load(Uri uri) {
    return new RequestCreator(this, uri, 0);
  }

  RequestCreator(Picasso picasso, Uri uri, int resourceId) {
    if (picasso.shutdown) {
      throw new IllegalStateException(
          "Picasso instance already shut down. Cannot submit new requests.");
    }
    this.picasso = picasso;
    this.data = new Request.Builder(uri, resourceId, picasso.defaultBitmapConfig);
  }
```

从上面可以看出，`load(url)` 方法最终会返回一个 `RequestCreator` 对象，在 `RequestCreator` 的构建中会构建一个 `Request.Builder` 对象。

### into(imageview)

这个函数是最重要的，涉及到图片的下载、缓存等

```java
  public void into(ImageView target) {
    into(target, null);
  }

  public void into(ImageView target, Callback callback) {
    long started = System.nanoTime();
    // 检查是否在主线程中执行
    checkMain();

    if (target == null) {
      throw new IllegalArgumentException("Target must not be null.");
    }

   	// 其实这儿是判断请求的合法性，判断 uri != null
    if (!data.hasImage()) {
      picasso.cancelRequest(target);
      if (setPlaceholder) {
      	// 设置默认填充图片
        setPlaceholder(target, getPlaceholderDrawable());
      }
      return;
    }

    // 判断是否填充满控件
    if (deferred) {
      if (data.hasSize()) {
        throw new IllegalStateException("Fit cannot be used with resize.");
      }
      // 获取控件的width、height
      int width = target.getWidth();
      int height = target.getHeight();
      if (width == 0 || height == 0) {
        if (setPlaceholder) {
          setPlaceholder(target, getPlaceholderDrawable());
        }
        picasso.defer(target, new DeferredRequestCreator(this, target, callback));
        return;
      }
      // 重新设置图片的尺寸 到 控件的尺寸
      data.resize(width, height);
    }

    // 创建请求对象
    Request request = createRequest(started);
    // 创建key
    String requestKey = createKey(request);

    // 判断是否从缓存中读取
    if (shouldReadFromMemoryCache(memoryPolicy)) {
      Bitmap bitmap = picasso.quickMemoryCacheCheck(requestKey);
      if (bitmap != null) {
      	// 如果内存中存在，则取消请求，直接设置到 imageview 中
        picasso.cancelRequest(target);
        setBitmap(target, picasso.context, bitmap, MEMORY, noFade, picasso.indicatorsEnabled);
        if (picasso.loggingEnabled) {
          log(OWNER_MAIN, VERB_COMPLETED, request.plainId(), "from " + MEMORY);
        }
        if (callback != null) {
          callback.onSuccess();
        }
        return;
      }
    }

    // 如果缓存中不存在，如果设置了默认图片，则先设置默认图片
    if (setPlaceholder) {
      setPlaceholder(target, getPlaceholderDrawable());
    }

    // 构造 action 进行异步请求
    Action action =
        new ImageViewAction(picasso, target, request, memoryPolicy, networkPolicy, errorResId,
            errorDrawable, requestKey, tag, callback, noFade);

    // 提交 action 
    picasso.enqueueAndSubmit(action);
  }
```

接下来去看 `Picasso` 的 `enqueueAndSubmit(action)` 方法

```java
 void enqueueAndSubmit(Action action) {
    Object target = action.getTarget();
    if (target != null && targetToAction.get(target) != action) {
      // 检查 targetToAction map集合中是否存在 当前这个 target
      cancelExistingRequest(target);
      // 重新添加新的 imageview-action 到集合中（targetToAction 是一个 WeakHashMap 对象）
      targetToAction.put(target, action);
    }
    submit(action);
  }

  private void cancelExistingRequest(Object target) {
  	// 检查主线程
    checkMain();
    // 相当于在 targetToAction map集合中看 target 是否存在
    // （如果可以移除，说明存在，返回存在的 action
    // 如果不能移除，说明不存在，则返回 null）
    Action action = targetToAction.remove(target);
    if (action != null) {
      action.cancel();
      // 取消这个 action，相当于取消这个请求
      dispatcher.dispatchCancel(action);
    }
    if (target instanceof ImageView) {
      ImageView targetImageView = (ImageView) target;
      DeferredRequestCreator deferredRequestCreator =
          targetToDeferredRequestCreator.remove(targetImageView);
      if (deferredRequestCreator != null) {
        deferredRequestCreator.cancel();
      }
    }
  }
```

上面这段比较重要，主要思想就是构建 `imageview` 和 `action` 一一对应关系集合，主要是解决 `imageview` 重用时的问题，当在 `listview` 中使用时，`imageview` 会重用，这是取消把关系集合中 `imageview` 对应的 `action` 给移除，同时取消该 action 的请求，重新把新的 `action` 加入到集合中，这样就避免了 `listview` 图片错乱的问题（之前我们是通过 `imageview` 和 `tag` 构建对应关系来处理）。  

接下来我们去看 `submit(action)` 

```java
  void submit(Action action) {
    dispatcher.dispatchSubmit(action);
  }

  void dispatchSubmit(Action action) {
    handler.sendMessage(handler.obtainMessage(REQUEST_SUBMIT, action));
  }
```

提交 `action`，其实就是通过 `handler` 发送一个 `REQUEST_SUBMIT` 消息，处理该消息之后会执行 `dispatcher.performSubmit(action)` 方法

```java
  void performSubmit(Action action) {
    performSubmit(action, true);
  }

  void performSubmit(Action action, boolean dismissFailed) {
    if (pausedTags.contains(action.getTag())) {
      pausedActions.put(action.getTarget(), action);
      if (action.getPicasso().loggingEnabled) {
        log(OWNER_DISPATCHER, VERB_PAUSED, action.request.logId(),
            "because tag '" + action.getTag() + "' is paused");
      }
      return;
    }

    BitmapHunter hunter = hunterMap.get(action.getKey());
    if (hunter != null) {
      hunter.attach(action);
      return;
    }

    if (service.isShutdown()) {
      if (action.getPicasso().loggingEnabled) {
        log(OWNER_DISPATCHER, VERB_IGNORED, action.request.logId(), "because shut down");
      }
      return;
    }

    // 创建一个 BitmapHunter 对象
    hunter = forRequest(action.getPicasso(), this, cache, stats, action);
    // 通过线程池来提交这个 hunter
    hunter.future = service.submit(hunter);
    hunterMap.put(action.getKey(), hunter);
    if (dismissFailed) {
      failedActions.remove(action.getTarget());
    }

    if (action.getPicasso().loggingEnabled) {
      log(OWNER_DISPATCHER, VERB_ENQUEUED, action.request.logId());
    }
  }
```
当通过线程池，提交了这个 `hunter` 对象之后，将会调用它的 `run()` 方法，执行 `hunt()` 方法。

```java
  @Override public void run() {
    try {
      updateThreadName(data);

      if (picasso.loggingEnabled) {
        log(OWNER_HUNTER, VERB_EXECUTING, getLogIdsForHunter(this));
      }

      // 执行 hunt() 方法，获取图片
      result = hunt();

      if (result == null) {
        // 获取失败 
        dispatcher.dispatchFailed(this);
      } else {
        // 获取成功
        dispatcher.dispatchComplete(this);
      }
    } catch (Downloader.ResponseException e) {
      if (!e.localCacheOnly || e.responseCode != 504) {
        exception = e;
      }
      dispatcher.dispatchFailed(this);
    } catch (NetworkRequestHandler.ContentLengthException e) {
      exception = e;
      dispatcher.dispatchRetry(this);
    } catch (IOException e) {
      exception = e;
      dispatcher.dispatchRetry(this);
    } catch (OutOfMemoryError e) {
      StringWriter writer = new StringWriter();
      stats.createSnapshot().dump(new PrintWriter(writer));
      exception = new RuntimeException(writer.toString(), e);
      dispatcher.dispatchFailed(this);
    } catch (Exception e) {
      exception = e;
      dispatcher.dispatchFailed(this);
    } finally {
      Thread.currentThread().setName(Utils.THREAD_IDLE_NAME);
    }
  }

  void dispatchComplete(BitmapHunter hunter) {
    handler.sendMessage(handler.obtainMessage(HUNTER_COMPLETE, hunter));
  }
```

当图片获取成功之后，交给 `dispatcher` 回调 `dispatchComplete(this)` 方法，然后通过 handler 发送一个 `HUNTER_COMPLETE` 的消息，处理消息之后调用 `dispatcher.performComplete(hunter)` 方法

```java
  void performComplete(BitmapHunter hunter) {
    // 缓存图片
    if (shouldWriteToMemoryCache(hunter.getMemoryPolicy())) {
      cache.set(hunter.getKey(), hunter.getResult());
    }
    hunterMap.remove(hunter.getKey());
    batch(hunter);
    if (hunter.getPicasso().loggingEnabled) {
      log(OWNER_DISPATCHER, VERB_BATCHED, getLogIdsForHunter(hunter), "for completion");
    }
  }

  private void batch(BitmapHunter hunter) {
    if (hunter.isCancelled()) {
      return;
    }
    batch.add(hunter);
    if (!handler.hasMessages(HUNTER_DELAY_NEXT_BATCH)) {
      // 起缓冲作用，每隔0.2s执行一次
      handler.sendEmptyMessageDelayed(HUNTER_DELAY_NEXT_BATCH, BATCH_DELAY);
    }
  }
```

处理 `HUNTER_DELAY_NEXT_BATCH` 消息之后，会调用 `dispatcher.performBatchComplete()`

```java
  void performBatchComplete() {
    List<BitmapHunter> copy = new ArrayList<BitmapHunter>(batch);
    batch.clear();
    // 主线程发送消息 HUNTER_BATCH_COMPLETE
    mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(HUNTER_BATCH_COMPLETE, copy));
    logBatch(copy);
  }
```

通过主线程的 `handler` 对象发送 `HUNTER_BATCH_COMPLETE` 消息，处理消息后最终调用到 `action.complete()` 方法，来设置图片。

```java
  public void complete(Bitmap result, Picasso.LoadedFrom from) {
    if (result == null) {
      throw new AssertionError(
          String.format("Attempted to complete action with no result!\n%s", this));
    }

    ImageView target = this.target.get();
    if (target == null) {
      return;
    }

    Context context = picasso.context;
    boolean indicatorsEnabled = picasso.indicatorsEnabled;
    // 通过 PicassoDrawable 来设置
    PicassoDrawable.setBitmap(target, context, result, from, noFade, indicatorsEnabled);

    if (callback != null) {
      callback.onSuccess();
    }
  }

  static void setBitmap(ImageView target, Context context, Bitmap bitmap,
      Picasso.LoadedFrom loadedFrom, boolean noFade, boolean debugging) {
    Drawable placeholder = target.getDrawable();
    if (placeholder instanceof AnimationDrawable) {
      ((AnimationDrawable) placeholder).stop();
    }
    PicassoDrawable drawable =
        new PicassoDrawable(context, bitmap, placeholder, loadedFrom, noFade, debugging);
    target.setImageDrawable(drawable);
  }
```

这样最终就会把图片设置到 `imageview` 中。

## 总结

* `context` 是 `ApplicationContext`
* 创建 `Downloader` 时，用反射去检测，如果项目集成了 `okhttp` 包，则用 `okhttp` 包中的类去创建一个 `OkHttpDownloader` 下载器，如果项目未集成 `okhttp` ，则使用自带的 `UrlConnectionDownloader` 创建。同时 `DiskLruCache` 磁盘缓存。
* `Picasso` 中有内存缓存，磁盘缓存在 `okhttp` 中提供
* 动态判断网络类型，根据网络类型，创建合适的线程池大小。默认-3，wifi-4，4G-3，3G-2，2G-1
* `Dispatcher` 中的 `DispatcherThread` 是一个 `HandlerThread`，其中通过 `handler` 发送的所有有关 `action` 任务处理的消息都是在改线程中执行的（子线程），只有当获取到图片需要更新界面是，才会使用 `mainThreadHandler` 发送消息，在 `Picasso` 中进行处理，给imageviwe设置图片
* 通过 `imageview-action` 的对应关系来解决在 `listview` 中图片错位的问题
* 有关 `action` 的 `map` 集合都是 `WeakHashMap`，同时 `action` 中的 `target`（imageview对象）也是弱引用对象，主要是为了解决 `Activity`、`Fragment`等可以正常回收