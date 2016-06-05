# chapter2-单例模式

> 确保某一个类只有一个实例。  
避免产生过多的对象消耗过多的资源，如访问IO、数据库等资源

## 使用方式

设置构造函数为 `private`，通过 `getInstance()` 方法来获取实例对象

### 1. 饿汉式

```java
/**
 * 饿汉式（idea 自动单例生成形式）
 */
public class Singleton {
    private static Singleton instance = new Singleton();

    public static Singleton getInstance() {
        return instance;
    }

    private Singleton() {
    }
}
```

**优点**
> 线程安全、绝对单例

**缺点**
> 
* 在多实例或者有其他静态方法时，instance在没有使用它的时候就已经加载好了。若该对象很大，则在没使用时就加载很浪费内存。
* 不能防止反序列化、反射产生新的实例。

### 2. 懒汉式

```java
public class Singleton {

    private static Singleton1 instance = null;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

**优点**
> instance在没有使用时不用加载，节省内存。

**缺点**
> 
* 线程不安全：当多个线程执行时，都是发现instance为null，于是都去初始化了。
* 不能防止反序列化、反射产生新的实例。

### 3. 方法锁

```java
public class Singleton {

    private static Singleton instance = null;

    private Singleton() {
    }

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

**优点**
> 线程安全，绝对单例

**缺点**
> 
* 加锁导致程序并发性降低
* 不能防止反序列化、反射产生新的实例

### 4. 双重检查锁DCL

```java
public class Singleton {

    private static Singleton instance = null;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) { // ①
            synchronized (Singleton.class){
                if (instance == null) { // ②
                    instance = new Singleton(); // ③
                }
            }
        }
        return instance;
    }
}
```

**优点**
> 大部分时候线程安全，相比方法锁程序并发性较高。

**缺点**
> 
* 线程并非绝对安全；
* 不能防止反序列化、反射产生新的实例。  

> 线程不安全原因：  
>> Java的乱序执行、初始化对象需要时间。 对于语句③，JVM在执行时大致做了下述三件事：
* a. 在内存中分配一块内存 
* b. 调用构造方法 
* c. 将内存的地址指向instance变量。（执行这一步后，instance != null）    

>> 如果按照abc的顺序执行也不会有什么问题。但由于Java乱序执行的机制，有可能在真实情况下执行顺序为acb。
假设t1、t2是两个线程。t1执行到①时，发现为null，于是执行到语句③，先执行a,再执行c,在还没有执行b时，时间片给了t2。这时，由于instance已经分配了地址空间，instance不为null了。所以t2在执行到语句①后直接return instance，获得了这个还没有被初始化的对象，然后在使用时就报错了。

### 5. 双重检查锁DCL与volatile的联用

```java
public class Singleton {

    private volatile static Singleton instance = null;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) { // ①
            synchronized (Singleton.class){
                if (instance == null) { // ②
                    instance = new Singleton(); // ③
                }
            }
        }
        return instance;
    }
}
```

**优点**
> 线程安全，绝对单例

**缺点**
> 
* 代码看起来有点乱；
* 不能防止反序列化、反射产生新的实例

>> volatile的一个特性：对变量的读取每次都是从主存中读取。volatile还有另外一个特性：volatile屏蔽指令重排序。也就是说，执行语句③时，肯定是按照abc的顺序执行的（参见4）。

### 6. 静态内部类

```java
public class Singleton {

    private Singleton() {
    }

    public static Singleton getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final Singleton instance = new Singleton();
    }

}
```

**优点**
> 线程安全，绝对单例

>> 线程安全的原因：Java的运行机制保证了static修饰的类、对象仅被初始化一次。

**缺点**
> 不能防止反序列化


### 7. 枚举

```java
public enum  SingletonEnum {
    INSTANCE;

    public void display(){
        System.out.println(SingletonEnum.class.getSimpleName());
    }
}
```

**优点**
> 线程安全、绝对单例、可以防止因为反序列化、反射产生新的实例

**缺点**
> 和饿汉模式类似，会造成内存浪费。


## NOTICE:

* 如果单例由不同的类加载器装入，那便有可能存在多个单例类的实例
* 如果 `Singleton` 实现了 `java.io.Serializable` 接口，那么这个类可能被序列化和复原。需要杜绝单例对象在被反序列化的时重新生成对象，需在以上代码后加入一些代码（枚举形式除外）

```java
private Object readResolve() throws ObjectStreamException {
	return instance;
}
```

## 使用容器管理单例

> Android 中需要获取系统级别的服务事，采用的就是此方式 `context.getSystemService(String name)`。

```java
public class SingletonManager {

    private static Map<String, Object> singletonMap = new HashMap<String, Object>();

    private SingletonManager() {
    }

    public static void registerService(String key, Object instance){
        if (singletonMap.containsKey(key)){
            singletonMap.put(key,instance);
        }
    }

    public static Object getService(String key){
        return singletonMap.get(key);
    }
}
```