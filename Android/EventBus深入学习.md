# EventBus 深入学习

EventBus是一款针对Android优化的发布/订阅事件总线。主要功能是替代Intent,Handler,BroadCast在Fragment，Activity，Service，线程之间传递消息.优点是开销小，代码更优雅。以及将发送者和接收者解耦。

> 项目地址：[https://github.com/greenrobot/EventBus](https://github.com/greenrobot/EventBus)  
官方介绍：[http://greenrobot.org/eventbus/documentation/](http://greenrobot.org/eventbus/documentation/)  
版本：`3.0.0`

![](https://github.com/greenrobot/EventBus/blob/master/EventBus-Publish-Subscribe.png)

## 使用

* 自定义一个类 Event，主要用于消息传送

```java
public class MessageEvent {
    public final String message;

    public MessageEvent(String message) {
        this.message = message;
    }
}
```

* 设置订阅者接受消息函数，通过注解（`@Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true, priority = 100)`）方式（3 最新方式），注解支持设置ThreadMode、sticky事件、优先级。不在是 2 中的根据各个函数进行判断。

> **2 中订阅函数使用方式，是通过函数进行判断** `不再使用`  
* onEvent(MessageEvent event) —— 该事件在哪个线程发布出来的，onEvent就会在这个线程中运行，也就是说发布事件和接收事件线程在同一个线程。使用这个方法时，在onEvent方法中不能执行耗时操作，如果执行耗时操作容易导致事件分发延迟。
* onEventMainThread(MessageEvent event) —— 如果使用onEventMainThread作为订阅函数，那么不论事件是在哪个线程中发布出来的，onEventMainThread都会在UI线程中执行，接收事件就会在UI线程中运行，这个在Android中是非常有用的，因为在Android中只能在UI线程中跟新UI，所以在onEvnetMainThread方法中是不能执行耗时操作的。
* onEventBackgroundThread(MessageEvent event) —— 如果使用onEventBackgrond作为订阅函数，那么如果事件是在UI线程中发布出来的，那么onEventBackground就会在子线程中运行，如果事件本来就是子线程中发布出来的，那么onEventBackground函数直接在该子线程中执行。
* onEventAsync(MessageEvent event) —— 使用这个函数作为订阅函数，那么无论事件在哪个线程发布，都会创建新的子线程在执行 onEventAsync.

最新使用方式，不再使用函数，而是通过 `@Subscribe(threadMode = ThreadMode.MAIN)` 方式，不过其内在思想还是一样

```java
@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(MessageEvent event) {
    doSomethingWith(event);
}
```

> `threadMode` 有四种，也就是对应原先的 4 个函数
* ThreadMode.POSTING
* ThreadMode.MAIN
* ThreadMode.BACKGROUND
* ThreadMode.ASYNC

* 在要接收消息的页面注册，例如在 activity 中注册，

```java
@Override
public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
}

@Override
public void onStop() {
   EventBus.getDefault().unregister(this);
    super.onStop();
}
```

* 在需要发送消息的地方，进行消息发送

```java
EventBus.getDefault().post(new MessageEvent("Hello everyone!"));
```

## 原理

> 参考：[http://www.cnblogs.com/all88/archive/2016/03/30/5338412.html](http://www.cnblogs.com/all88/archive/2016/03/30/5338412.html)

### register

![](http://images2015.cnblogs.com/blog/511825/201603/511825-20160330180220410-1560519931.png)

### post

![](http://images2015.cnblogs.com/blog/511825/201603/511825-20160330182550879-762466444.png)

### 订阅逻辑

* 1、首先用register（）方法注册一个订阅者

* 2、获取该订阅者的所有订阅的方法

* 3、根据该订阅者的所有订阅的事件类型，将订阅者存入到每个以 事件类型为key 以所有订阅者为values的map集合中

* 4、然后将订阅事件添加到以订阅者为key 以订阅者所有订阅事件为values的map集合中

* 4.1、如果是订阅了粘滞事件的订阅者，从粘滞事件缓存区获取之前发送过的粘滞事件，响应这些粘滞事件。

### 事件发送逻辑

* 1、首先获取当前线程的事件队列

* 2、将要发送的事件添加到事件队列中

* 3、根据发送事件类型获取所有的订阅者

* 4、根据响应方法的执行模式，在相应线程通过反射执行订阅者的订阅方法

### 取消逻辑

* 1、首先通过unregister方法拿到要取消的订阅者

* 2、得到该订阅者的所有订阅事件类型

* 3、遍历事件类型，根据每个事件类型获取到所有的订阅者集合，并从集合中删除该订阅者

* 4、将订阅者从步骤2的集合中移除
