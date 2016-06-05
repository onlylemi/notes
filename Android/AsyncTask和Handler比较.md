# AsyncTask和Handler比较

## 相同

* 两者都可以处理异步任务，不阻塞UI线程

## 对比

AsyncTask处理简单的异步任务，Handler处理异步任务之外还可以进行线程间通信。AsyncTask轻量级。

1. AsyncTask功能相比handler要少。
    * AsyncTask就只是一个异步任务，执行完之后处理一下UI或者其他东西。甚至执行完就完了。一般用于获取网络数据，操作IO等耗时操作。
    * 而一个handler可以随时发送消息。不仅可以用于耗时操作，还可以用于一些需要和主线程进行持续连接的地方，比如一个子线程它会长时间的存在，它和主线程的通信就只能用AsyncTask。
    * 所以AsyncTask适用于轻量级的异步任务。
2. AsyncTask用起来比较简单。Handler用起来比较复杂。
    * AsyncTask的任务是事先定好了的。创建好对象并执行之后就可以不用管了。
    * 而Handler创建好之后，还要传递这个handler到其他线程中，然后在其他线程中发送消息。
    * 所以AsyncTask比Handler容易维护。
3. AsyncTask只能是主线程给子线程一个任务去执行。而handler不仅仅是主线程和子线程之间，它可以是任何两个线程之间。

## AsyncTask缺点

* AsyncTask一旦执行了 doInBackground()，就算调用取消方法，也会将doInBackground里面的代码执行完毕，才会停止。

> 解决方法：一般在 doInBackground() 中，检查 isCancelled() 的值来人为控制执行进程

* AsyncTask在3.0以后，默认的Executor是Serial_Executor,即串行的方式执行所有的异步任务。这就导致有多个异步任务时，最后一个任务要等很长时间才会被执行到。

> 设置默认的Executor为THREADPOOLEXECUTOR。

* Thread_Pool_Executor的缺点：
	* AsyncTask可能存在新开大量线程消耗系统资源和导致应用FC的风险
	* 线程池不经维护，当大量异步发生时，导致线程池满了，会出异常

> 1. 对于加载图片等操作，可以采用分页的方式。使得一次不会执行多个线程；
2. 使用Serial_Executor（同步执行），而不是Thread_Pool_Excutor

---

另外可参考：[AsyncTask隐藏的陷阱](http://blog.csdn.net/findsafety/article/details/10999843)