# Android动画原理深入解析

上一节已经介绍了Android 动画：[Android 动画深入学习](https://github.com/onlylemi/notes/blob/master/Android/Android动画深入学习.md)。这节介绍动画的原理。

* View 动画
* 属性动画

## View 动画

* initialize() —— 动画的初始化  
* applyTransformation() —— 方法中都是进行矩阵操作(绘制过程)

> View 动画的位置一直就没改变，而是在绘制的时候通过矩阵来处理变换。自始至终 View 的 LayoutParams 参数根本没有改变。所以 View 动画其实就是一种（假象）

View 动画的简单使用，以平移动画为例

```java
// 平移动画
TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 200, 200);
translateAnimation.setDuration(1000);
// 开始动画
view.startAnimation(translateAnimation);
```

先看 view 的 `startAnimation` 方法

```java
	public void startAnimation(Animation animation) {
        animation.setStartTime(Animation.START_ON_FIRST_FRAME);
   		// 这个方法，会给 view 自己内部设置一个 Animation 对象（内部变量为 mCurrentAnimation）
        setAnimation(animation);
        invalidateParentCaches();
        // 通知 View 进行刷新界面
        invalidate(true);
    }
```

调用 `invalidate()` 方法之后，会让 `ViewRootImpl` 调用 `scheduleTraversals()` 发起一个重绘请求，通过 `Choreographer` 发送一个异步消息，同时在 `Choreographer` 中处理消息，最终回调到 `performTraversals()` 执行重绘。

> 关于 `invalidate()` 的重绘内部原理可以看 [invalidate 和 postInvalidate 内部原理机制](https://github.com/onlylemi/notes/blob/master/Android/invalidate和postInvalidate内部原理机制.md) 这篇。

当执行 `performTraversals()` 方法后，会进行重绘，最终会调用 view 的 draw() 函数进行绘制，在绘制函数中如果发现 `getAnimation()` 不为 `null`，将进行动画绘制，执行 `applyLegacyAnimation()` 方法

```java
	private boolean applyLegacyAnimation(ViewGroup parent, long drawingTime,
            Animation a, boolean scalingRequired) {
        Transformation invalidationTransform;
        final int flags = parent.mGroupFlags;
        final boolean initialized = a.isInitialized();
        if (!initialized) {
        	// 动画的初始化
            a.initialize(mRight - mLeft, mBottom - mTop, parent.getWidth(), parent.getHeight());
            a.initializeInvalidateRegion(0, 0, mRight - mLeft, mBottom - mTop);
            if (mAttachInfo != null) a.setListenerHandler(mAttachInfo.mHandler);
            onAnimationStart();
        }

        final Transformation t = parent.getChildTransformation();
        boolean more = a.getTransformation(drawingTime, t, 1f);
        if (scalingRequired && mAttachInfo.mApplicationScale != 1f) {
            if (parent.mInvalidationTransformation == null) {
                parent.mInvalidationTransformation = new Transformation();
            }
            invalidationTransform = parent.mInvalidationTransformation;
            // 执行动画操作
            a.getTransformation(drawingTime, invalidationTransform, 1f);
        } else {
            invalidationTransform = t;
        }

        if (more) {
            if (!a.willChangeBounds()) {
                if ((flags & (ViewGroup.FLAG_OPTIMIZE_INVALIDATE | ViewGroup.FLAG_ANIMATION_DONE)) ==
                        ViewGroup.FLAG_OPTIMIZE_INVALIDATE) {
                    parent.mGroupFlags |= ViewGroup.FLAG_INVALIDATE_REQUIRED;
                } else if ((flags & ViewGroup.FLAG_INVALIDATE_REQUIRED) == 0) {
                    // The child need to draw an animation, potentially offscreen, so
                    // make sure we do not cancel invalidate requests
                    parent.mPrivateFlags |= PFLAG_DRAW_ANIMATION;
                    parent.invalidate(mLeft, mTop, mRight, mBottom);
                }
            } else {
                if (parent.mInvalidateRegion == null) {
                    parent.mInvalidateRegion = new RectF();
                }
                final RectF region = parent.mInvalidateRegion;
                a.getInvalidateRegion(0, 0, mRight - mLeft, mBottom - mTop, region,
                        invalidationTransform);

                // The child need to draw an animation, potentially offscreen, so
                // make sure we do not cancel invalidate requests
                parent.mPrivateFlags |= PFLAG_DRAW_ANIMATION;

                final int left = mLeft + (int) region.left;
                final int top = mTop + (int) region.top;
                parent.invalidate(left, top, left + (int) (region.width() + .5f),
                        top + (int) (region.height() + .5f));
            }
        }
        return more;
    }
```

可以在上面的代码中看到，会调用用 Animation 的 `initialize()` 方法以及 `getTransformation()` 方法 ，在 `getTransformation()` 中会调用 `applyTransformation()` 方法，最终动画的最重要的两个方法都调用了，完成动画的操作。

## 属性动画

属性动画的简单使用，以平移动画为例 ObjectAnimator 为例

```java
ObjectAnimator.ofFloat(view, "translationY", 500).start();
```

### ofFloat()

```java
	public static ObjectAnimator ofFloat(Object target, String propertyName, float... values) {
        ObjectAnimator anim = new ObjectAnimator(target, propertyName);
        anim.setFloatValues(values);
        return anim;
    }

    private ObjectAnimator(Object target, String propertyName) {
        setTarget(target);
        setPropertyName(propertyName);
    }

    public void setTarget(@Nullable Object target) {
        final Object oldTarget = getTarget();
        if (oldTarget != target) {
            if (isStarted()) {
                cancel();
            }
            // 保留一个 view 的弱引用对象
            mTarget = target == null ? null : new WeakReference<Object>(target);
            // New target should cause re-initialization prior to starting
            mInitialized = false;
        }
    }

    public void setPropertyName(@NonNull String propertyName) {
        // ..................

        mPropertyName = propertyName;
        // New property/values/target should cause re-initialization prior to starting
        mInitialized = false;
    }
```

首先在 ObjectAnimator 的构造函数中完成了 target 及 propertyName 设置，然后调用 setFloatValues()

```java
	public void setFloatValues(float... values) {
        if (mValues == null || mValues.length == 0) {
            // No values yet - this animator is being constructed piecemeal. Init the values with
            // whatever the current propertyName is
            if (mProperty != null) {
                setValues(PropertyValuesHolder.ofFloat(mProperty, values));
            } else {
                setValues(PropertyValuesHolder.ofFloat(mPropertyName, values));
            }
        } else {
            super.setFloatValues(values);
        }
    }
```

该方法把我们传入的 mPropertyName 和 values 对象构造为一个 PropertyValuesHolder 对象，在 PropertyValuesHolder 的 ofFloat 方法中，返回一个 FloatPropertyValuesHolder 对象，

```java
	public static PropertyValuesHolder ofFloat(Property<?, Float> property, float... values) {
        return new FloatPropertyValuesHolder(property, values);
    }

    	public FloatPropertyValuesHolder(Property property, float... values) {
            super(property);
            setFloatValues(values);
            if (property instanceof  FloatProperty) {
                mFloatProperty = (FloatProperty) mProperty;
            }
        }

        public void setFloatValues(float... values) {
            super.setFloatValues(values);
            mFloatKeyframes = (Keyframes.FloatKeyframes) mKeyframes;
        }
```

在该对象的产生过程中会调用 setFloatValues 方法，存储了我们的 mValueType ，此外还存了一个 mKeyframes

```java
	public void setFloatValues(float... values) {
        mValueType = float.class;
        mKeyframes = KeyframeSet.ofFloat(values);
    }

    // 根据提供的数字序列得到动画的核心帧集合
    public static KeyframeSet ofFloat(float... values) {
        boolean badValue = false;
        int numKeyframes = values.length; //有多少个数字就有多少帧
        FloatKeyframe keyframes[] = new FloatKeyframe[Math.max(numKeyframes,2)];
        if (numKeyframes == 1) { //如果只传入一个数字，那么该数字就是结束帧的值
            keyframes[0] = (FloatKeyframe) Keyframe.ofFloat(0f);
            keyframes[1] = (FloatKeyframe) Keyframe.ofFloat(1f, values[0]);
            if (Float.isNaN(values[0])) {
                badValue = true;
            }
        } else { //如果传入多个数字，那么可以将整个动画时间间隔均分，每个数字按顺序在每个时间比率上占据一个属性值
            keyframes[0] = (FloatKeyframe) Keyframe.ofFloat(0f, values[0]);
            for (int i = 1; i < numKeyframes; ++i) {
                keyframes[i] =
                        (FloatKeyframe) Keyframe.ofFloat((float) i / (numKeyframes - 1), values[i]);
                if (Float.isNaN(values[i])) {
                    badValue = true;
                }
            }
        }
        if (badValue) {
            Log.w("Animator", "Bad value (NaN) in float animator");
        }
        return new FloatKeyframeSet(keyframes);
    }
```

上面得到的帧只是动画的几个核心帧，而不是动画的全部帧，那中间的其他帧是 KeyframeSet 的 getValue 方法中计算

```java
	public Object getValue(float fraction) {
    // Special-case optimization for the common case of only two keyframes
    if (mNumKeyframes == 2) {//1.处理只有2帧的情况
        if (mInterpolator != null) {
            //先调用TimeInterpolator函数
            fraction = mInterpolator.getInterpolation(fraction);
        }
        //再调用TypeEvaluator函数
        return mEvaluator.evaluate(fraction, mFirstKeyframe.getValue(), mLastKeyframe.getValue());
    }
    //2.处理上冲和下冲的情况
    if (fraction <= 0f) {
        final Keyframe nextKeyframe = mKeyframes.get(1);
        final TimeInterpolator interpolator = nextKeyframe.getInterpolator();
        if (interpolator != null) {
            fraction = interpolator.getInterpolation(fraction);
        }
        final float prevFraction = mFirstKeyframe.getFraction();
        float intervalFraction = (fraction - prevFraction) / (nextKeyframe.getFraction() - prevFraction);
        return mEvaluator.evaluate(intervalFraction, mFirstKeyframe.getValue(), nextKeyframe.getValue());
    } else if (fraction >= 1f) {
        final Keyframe prevKeyframe = mKeyframes.get(mNumKeyframes - 2);
        final TimeInterpolator interpolator = mLastKeyframe.getInterpolator();
        if (interpolator != null) {
            fraction = interpolator.getInterpolation(fraction);
        }
        final float prevFraction = prevKeyframe.getFraction();
        float intervalFraction = (fraction - prevFraction) / (mLastKeyframe.getFraction() - prevFraction);
        return mEvaluator.evaluate(intervalFraction, prevKeyframe.getValue(), mLastKeyframe.getValue());
    }
    //3.处理正常的多帧的情况
    Keyframe prevKeyframe = mFirstKeyframe;
    //首先要遍历前面计算出的主要KeyFrame集合，看当前的fraction是处在哪个区间的
    for (int i = 1; i < mNumKeyframes; ++i) {
        Keyframe nextKeyframe = mKeyframes.get(i);
        if (fraction < nextKeyframe.getFraction()) {
            final TimeInterpolator interpolator = nextKeyframe.getInterpolator();
            final float prevFraction = prevKeyframe.getFraction();
            //将当前的fraction折算成在这个区间内的时间比率，这个计算有意思吧
            float intervalFraction = (fraction - prevFraction) / (nextKeyframe.getFraction() - prevFraction);
            // Apply interpolator on the proportional duration.
            if (interpolator != null) {
                //先调用TimeInterpolator函数
                intervalFraction = interpolator.getInterpolation(intervalFraction);
            }
            //再调用TypeEvaluator函数
            return mEvaluator.evaluate(intervalFraction, prevKeyframe.getValue(), nextKeyframe.getValue());
        }
        prevKeyframe = nextKeyframe;
    }
    // shouldn't reach here
    return mLastKeyframe.getValue();
}
```

到此，PropertyValuesHolder.ofFloat 在彻底返回，可以看到这个过程中，我们成功的为 PropertyValuesHolder 对象赋值了 propertyName、valueType、keyframeSet。然后把返回的对象传到 setValues() 中。

```java
	public void setValues(PropertyValuesHolder... values) {
		// 首先记录了mValues，
        int numValues = values.length;
        mValues = values;
        // 然后通过一个mValueMap记录：key为属性的名称，值为PropertyValuesHolder 。 
        mValuesMap = new HashMap<String, PropertyValuesHolder>(numValues);
        for (int i = 0; i < numValues; ++i) {
            PropertyValuesHolder valuesHolder = values[i];
            mValuesMap.put(valuesHolder.getPropertyName(), valuesHolder);
        }
        // New property/values/target should cause re-initialization prior to starting
        mInitialized = false;
    }
```

### start()

当一个动画调用 start() 方法，启动时，

```java
	public void start() {
        // See if any of the current active/pending animators need to be canceled
        AnimationHandler handler = sAnimationHandler.get();
        if (handler != null) {
            int numAnims = handler.mAnimations.size();
            for (int i = numAnims - 1; i >= 0; i--) {
                if (handler.mAnimations.get(i) instanceof ObjectAnimator) {
                    ObjectAnimator anim = (ObjectAnimator) handler.mAnimations.get(i);
                    if (anim.mAutoCancel && hasSameTargetAndProperties(anim)) {
                        anim.cancel();
                    }
                }
            }
            numAnims = handler.mPendingAnimations.size();
            for (int i = numAnims - 1; i >= 0; i--) {
                if (handler.mPendingAnimations.get(i) instanceof ObjectAnimator) {
                    ObjectAnimator anim = (ObjectAnimator) handler.mPendingAnimations.get(i);
                    if (anim.mAutoCancel && hasSameTargetAndProperties(anim)) {
                        anim.cancel();
                    }
                }
            }
            numAnims = handler.mDelayedAnims.size();
            for (int i = numAnims - 1; i >= 0; i--) {
                if (handler.mDelayedAnims.get(i) instanceof ObjectAnimator) {
                    ObjectAnimator anim = (ObjectAnimator) handler.mDelayedAnims.get(i);
                    if (anim.mAutoCancel && hasSameTargetAndProperties(anim)) {
                        anim.cancel();
                    }
                }
            }
        }
        if (DBG) {
            Log.d(LOG_TAG, "Anim target, duration: " + getTarget() + ", " + getDuration());
            for (int i = 0; i < mValues.length; ++i) {
                PropertyValuesHolder pvh = mValues[i];
                Log.d(LOG_TAG, "   Values[" + i + "]: " +
                    pvh.getPropertyName() + ", " + pvh.mKeyframes.getValue(0) + ", " +
                    pvh.mKeyframes.getValue(1));
            }
        }
        super.start();
    }
```

首先会判断，当前动画、等待动画、延迟动画中有和当前动画相同的动画，然后把相同的动画取消掉，然后会调用 super.start()，也就是 ValueAnimator 的 start 方法。

```java
private void start(boolean playBackwards) {
        if (Looper.myLooper() == null) {
            throw new AndroidRuntimeException("Animators may only be run on Looper threads");
        }
        mReversing = playBackwards;
        mPlayingBackwards = playBackwards;
        if (playBackwards && mSeekFraction != -1) {
            if (mSeekFraction == 0 && mCurrentIteration == 0) {
                // special case: reversing from seek-to-0 should act as if not seeked at all
                mSeekFraction = 0;
            } else if (mRepeatCount == INFINITE) {
                mSeekFraction = 1 - (mSeekFraction % 1);
            } else {
                mSeekFraction = 1 + mRepeatCount - (mCurrentIteration + mSeekFraction);
            }
            mCurrentIteration = (int) mSeekFraction;
            mSeekFraction = mSeekFraction % 1;
        }
        if (mCurrentIteration > 0 && mRepeatMode == REVERSE &&
                (mCurrentIteration < (mRepeatCount + 1) || mRepeatCount == INFINITE)) {
            // if we were seeked to some other iteration in a reversing animator,
            // figure out the correct direction to start playing based on the iteration
            if (playBackwards) {
                mPlayingBackwards = (mCurrentIteration % 2) == 0;
            } else {
                mPlayingBackwards = (mCurrentIteration % 2) != 0;
            }
        }
        int prevPlayingState = mPlayingState;
        mPlayingState = STOPPED;
        mStarted = true;
        mStartedDelay = false;
        mPaused = false;
        updateScaledDuration(); // in case the scale factor has changed since creation time
        AnimationHandler animationHandler = getOrCreateAnimationHandler();
        animationHandler.mPendingAnimations.add(this);
        if (mStartDelay == 0) {
            // This sets the initial value of the animation, prior to actually starting it running
            if (prevPlayingState != SEEKED) {
                setCurrentPlayTime(0);
            }
            mPlayingState = STOPPED;
            mRunning = true;
            notifyStartListeners();
        }
        animationHandler.start();
    }
```

`animationHandler.start()` 被调用之后会调用 `scheduleAnimation()` 方法

```java
		private void scheduleAnimation() {
            if (!mAnimationScheduled) {
                mChoreographer.postCallback(Choreographer.CALLBACK_ANIMATION, mAnimate, null);
                mAnimationScheduled = true;
            }
        }

        final Runnable mAnimate = new Runnable() {
            @Override
            public void run() {
                mAnimationScheduled = false;
                doAnimationFrame(mChoreographer.getFrameTime());
            }
        };
```

由上面代码我们可以知道，通过 `Choreographer` 发送一个异步消息，同时在 `Choreographer` 中处理消息，执行到 `mAnimate` 的 `run()` 方法，然后调用 `AnimationHandler` 内的 `doAnimationFrame()` 方法

```java
		void doAnimationFrame(long frameTime) {
            mLastFrameTime = frameTime;

            // mPendingAnimations holds any animations that have requested to be started
            // We're going to clear mPendingAnimations, but starting animation may
            // cause more to be added to the pending list (for example, if one animation
            // starting triggers another starting). So we loop until mPendingAnimations
            // is empty.
            while (mPendingAnimations.size() > 0) {
                ArrayList<ValueAnimator> pendingCopy =
                        (ArrayList<ValueAnimator>) mPendingAnimations.clone();
                mPendingAnimations.clear();
                int count = pendingCopy.size();
                for (int i = 0; i < count; ++i) {
                    ValueAnimator anim = pendingCopy.get(i);
                    // If the animation has a startDelay, place it on the delayed list
                    if (anim.mStartDelay == 0) {
                        anim.startAnimation(this);
                    } else {
                        mDelayedAnims.add(anim);
                    }
                }
            }

            // Next, process animations currently sitting on the delayed queue, adding
            // them to the active animations if they are ready
            int numDelayedAnims = mDelayedAnims.size();
            for (int i = 0; i < numDelayedAnims; ++i) {
                ValueAnimator anim = mDelayedAnims.get(i);
                if (anim.delayedAnimationFrame(frameTime)) {
                    mReadyAnims.add(anim);
                }
            }
            int numReadyAnims = mReadyAnims.size();
            if (numReadyAnims > 0) {
                for (int i = 0; i < numReadyAnims; ++i) {
                    ValueAnimator anim = mReadyAnims.get(i);
                    anim.startAnimation(this);
                    anim.mRunning = true;
                    mDelayedAnims.remove(anim);
                }
                mReadyAnims.clear();
            }

            // Now process all active animations. The return value from animationFrame()
            // tells the handler whether it should now be ended
            int numAnims = mAnimations.size();
            for (int i = 0; i < numAnims; ++i) {
                mTmpAnimations.add(mAnimations.get(i));
            }
            for (int i = 0; i < numAnims; ++i) {
                ValueAnimator anim = mTmpAnimations.get(i);
                if (mAnimations.contains(anim) && anim.doAnimationFrame(frameTime)) {
                    mEndingAnims.add(anim);
                }
            }
            mTmpAnimations.clear();
            if (mEndingAnims.size() > 0) {
                for (int i = 0; i < mEndingAnims.size(); ++i) {
                    mEndingAnims.get(i).endAnimation(this);
                }
                mEndingAnims.clear();
            }

            // Schedule final commit for the frame.
            mChoreographer.postCallback(Choreographer.CALLBACK_COMMIT, mCommit, null);

            // If there are still active or delayed animations, schedule a future call to
            // onAnimate to process the next frame of the animations.
            if (!mAnimations.isEmpty() || !mDelayedAnims.isEmpty()) {
                scheduleAnimation();
            }
        }
```

在 `AnimationHandler` 内的 `doAnimationFrame()` 方法中会调用，`ValueAnimator` 的 `doAnimationFrame()` 方法进行动画每一帧的绘制，同时通过 `mChoreographer` 进行每一帧的提交，然后再次调用 `scheduleAnimation()` 方法绘制下一帧。
