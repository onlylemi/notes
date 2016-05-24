# chapter2-单例模式

> 确保某一个类只有一个实例。  
避免产生过多的对象消耗过多的资源，如房屋IO、数据库等资源

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

### 2. 懒汉式

```java
public class Singleton {

    private static Singleton1 instance = null;

    private Singleton1() {
    }

    public static Singleton1 getInstance() {
        if (instance == null) {
            instance = new Singleton1();
        }
        return instance;
    }
}
```

### 3. 双判空（推荐）

```java
public class Singleton2 {

    private static Singleton2 instance = null;

    private Singleton2() {
    }

    public static Singleton2 getInstance() {
        if (instance == null) {
            synchronized (Singleton2.class){
                if (instance == null) {
                    instance = new Singleton2();
                }
            }
        }
        return instance;
    }
}
```
### 4. 静态内部类（推荐）

```java
public class Singleton3 {

    private Singleton3() {
    }

    public static Singleton3 getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final Singleton3 instance = new Singleton3();
    }

}
```

### 5. 枚举

```java
public enum  SingletonEnum {
    INSTANCE;

    public void display(){
        System.out.println(SingletonEnum.class.getSimpleName());
    }
}
```

> NOTICE:
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