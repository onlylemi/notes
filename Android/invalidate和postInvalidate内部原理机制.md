# invalidate 和 postInvalidate 内部原理机制（API：23）

`invalidate()` 和 `postInvalidate()` 都是进行 UI 更新，`invalidate()` 在主线程中调用，而 `postInvalidate` 是在子线程中进行调用。

## invalidate

当我们调用调用 `invalidate()` 方法时，在内部会调用到 `invalidateInternal()` 方法。

```java
	public void invalidate() {
        invalidate(true);
    }

    void invalidate(boolean invalidateCache) {
        invalidateInternal(0, 0, mRight - mLeft, mBottom - mTop, invalidateCache, true);
    }

    void invalidateInternal(int l, int t, int r, int b, boolean invalidateCache,
            boolean fullInvalidate) {
        
        // .....忽略其他代码....

            // Propagate the damage rectangle to the parent view.
            final AttachInfo ai = mAttachInfo;
            final ViewParent p = mParent;
            if (p != null && ai != null && l < r && t < b) {
                final Rect damage = ai.mTmpInvalRect;
                damage.set(l, t, r, b);
                p.invalidateChild(this, damage);
            }

        //  .....忽略其他代码....
    }
```

当执行到 `invalidateInternal()` 方法时，忽略其他多余的代码，看主要代码，`p` 是一个 `ViewParent` 接口，将会执行 `p.invalidateChild(this, damage);` 这个方法，所以我们看它的实现类的这个方法，也就是 `ViewRootImpl` 的 `invalidateChild()` 方法，`invalidateChild()` 方法中调用了 `invalidateRectOnScreen()` 方法，然后在 `invalidateRectOnScreen()` 中最终会调用 `scheduleTraversals()` 方法

```java
	void scheduleTraversals() {
        if (!mTraversalScheduled) {
            mTraversalScheduled = true;
            mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();
            mChoreographer.postCallback(
                    Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
            if (!mUnbufferedInputDispatch) {
                scheduleConsumeBatchedInput();
            }
            notifyRendererOfFramePending();
            pokeDrawLockIfNeeded();
        }
    }
```

在 `scheduleTraversals()` 方法中，mTraversalRunnable 是一个 Runnable 对象，它的类如下

```java
	final class TraversalRunnable implements Runnable {
        @Override
        public void run() {
            doTraversal();
        }
    }
```

同时，`mChoreographer` 是一个 `Choreographer` 对象，会调用 `postCallback()` 方法最终会调用到 `scheduleFrameLocked()` 方法，在其内部，通过 handler 发送一个 message 消息。

```java
	private void scheduleFrameLocked(long now) {
        if (!mFrameScheduled) {
            mFrameScheduled = true;
            if (USE_VSYNC) {
                if (DEBUG_FRAMES) {
                    Log.d(TAG, "Scheduling next frame on vsync.");
                }

                // If running on the Looper thread, then schedule the vsync immediately,
                // otherwise post a message to schedule the vsync from the UI thread
                // as soon as possible.
                if (isRunningOnLooperThreadLocked()) {
                    scheduleVsyncLocked();
                } else {
                    Message msg = mHandler.obtainMessage(MSG_DO_SCHEDULE_VSYNC);
                    msg.setAsynchronous(true);
                    mHandler.sendMessageAtFrontOfQueue(msg);
                }
            } else {
                final long nextFrameTime = Math.max(
                        mLastFrameTimeNanos / TimeUtils.NANOS_PER_MS + sFrameDelay, now);
                if (DEBUG_FRAMES) {
                    Log.d(TAG, "Scheduling next frame in " + (nextFrameTime - now) + " ms.");
                }
                // 发送 message 消息
                Message msg = mHandler.obtainMessage(MSG_DO_FRAME);
                msg.setAsynchronous(true);
                mHandler.sendMessageAtTime(msg, nextFrameTime);
            }
        }
    }
```

所以接下来我们就看它对消息的处理方法了。mHandler 是一个 handler 对象，

```java
	private final class FrameHandler extends Handler {
        public FrameHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DO_FRAME:
                    doFrame(System.nanoTime(), 0);
                    break;
                case MSG_DO_SCHEDULE_VSYNC:
                    doScheduleVsync();
                    break;
                case MSG_DO_SCHEDULE_CALLBACK:
                    doScheduleCallback(msg.arg1);
                    break;
            }
        }
    }
```

在 `doFrame()` 中将会调用 `doCallbacks()` 方法，最终会执行消息对象 `mTraversalRunnable` 的 `run()` 方法。在上面中我们已经看到在 `run()` 中会执行 `doTraversal()` 方法，

```java
	void doTraversal() {
        if (mTraversalScheduled) {
            mTraversalScheduled = false;
            mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);

            if (mProfile) {
                Debug.startMethodTracing("ViewAncestor");
            }

            performTraversals();

            if (mProfile) {
                Debug.stopMethodTracing();
                mProfile = false;
            }
        }
    }
```

在 `doTraversal()` 方法中又会执行 `performTraversals()` 方法（这个方法应该很熟悉，view 绘图中第一个执行的方法），在这个方法中又会调用 `performMeasure()` 、`performLayout()` 、`performDraw()` 方法进行界面重绘。这样 `invalidate()` 方法的目的也就达到了。

### 总结

调用 `invalidate()` 方法之后，会让 `ViewRootImpl` 调用 `scheduleTraversals()` 发起一个重绘请求，通过 `Choreographer` 发送一个异步消息，同时在 `Choreographer` 中处理消息，最终回调到 `performTraversals()` 执行重绘。
 
## postInvalidate

其实 `postInvalidate()` 就比较简单，它的原理最终也是调用 `invalidate()` 方法来进行重绘，只不过因为是在子线程中，所以内部是通过消息机制，回调到主线程中调用 `invalidate()` 方法的。

```java
	public void postInvalidate() {
        postInvalidateDelayed(0);
    }

    public void postInvalidateDelayed(long delayMilliseconds) {
        // We try only with the AttachInfo because there's no point in invalidating
        // if we are not attached to our window
        final AttachInfo attachInfo = mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.dispatchInvalidateDelayed(this, delayMilliseconds);
        }
    }
```

可以看到最终会调用 ViewRootImpl 的 dispatchInvalidateDelayed() 方法

```java
	public void dispatchInvalidateDelayed(View view, long delayMilliseconds) {
        Message msg = mHandler.obtainMessage(MSG_INVALIDATE, view);
        mHandler.sendMessageDelayed(msg, delayMilliseconds);
    }
```

通过 handler 发送一个 MSG_INVALIDATE 重绘的消息，接下看它的消息处理部分

```java
		final class ViewRootHandler extends Handler {
        
		// ................代码比较多，忽略多余代码.................

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_INVALIDATE:
                ((View) msg.obj).invalidate();
                break;
            
            // ...............................

            }
        }
    }
```

可以很明显的看到，调用了 `invalidate()` 方法，然后继续上面分析的，最终达到异步重绘的目的。