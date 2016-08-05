# 线程中的 sleep 和 Object 的 wait

![](https://raw.githubusercontent.com/onlylemi/res/master/java-thread-state.png)

* Thread#sleep()
* Object#wait()

## sleep

```java
public class ThreadTest {

    private synchronized void print(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.print(str.charAt(i));

            if (i == str.length() / 2) {
                try {
                    // t1 执行 sleep 方法使当前线程睡眠 2000ms，但不会释放该对象的锁，仅会让出 CPU 执行权，
                    // 如果线程 t2 想要进入该方法则会被阻塞，直到 t1 在该方法中执行完毕后，释放该对象的锁，
                    // t2 才有可能获得
                   Thread.sleep(2 * 1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        final ThreadTest test = new ThreadTest();

        new Thread(new Runnable() {
            @Override
            public void run() {
                test.print("---------------------------------------------------------");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                test.print("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }
        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

**输出：**

```
-----------------------------

线程 t1 休眠 2000ms

---------------------------------------------------------
+++++++++++++++++++++++++++++

线程 t2 休眠 2000ms

---------------------------------------------------------
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
```

## wait

```java
public class ThreadTest {

    private synchronized void print(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.print(str.charAt(i));

            if (i == str.length() / 2) {
                try {
                    // 执行 wait 方法，会使当前线程进入休眠状态，同时释放掉该对象上的锁，进入等待池中等待，直到被 notify
                    // 其他线程可以获得该对象的锁，进入该方法。（在同步块同调用）
                   wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println();
    }

    private synchronized void notifyT() {
        // notify() 和 notifyAll() 调用时，必须持有该对象的上锁，否则抛出 IllegalMonitorStateException 异常
        // notify() 执行之后，如果有多个线程等待，则任意挑选一个 wait() 状态的线程，使它停止休眠，重新进入阻塞状态，
        // 等待获取该对象上的锁
//        notify();

        // notifyAll() 执行后，则会使当前所有 wait() 状态的线程，停止休眠，进入阻塞状态，等待获取该对象上的锁
        notifyAll();
    }

    public static void main(String[] args) {
        final ThreadTest test = new ThreadTest();

        new Thread(new Runnable() {
            @Override
            public void run() {
                test.print("---------------------------------------------------------");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                test.print("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            }
        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        test.notifyT();
    }
}
```

**输出：**

```
-----------------------------

线程 t1 休眠 2000ms

-----------------------------+++++++++++++++++++++++++++++

线程 t2 休眠 2000ms

notifyAll()执行后

-----------------------------+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
----------------------------
```