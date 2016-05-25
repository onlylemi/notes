# chapter12-观察者模式

定义对象间的一种一对多的依赖关系，使得每当一个对象改变状态，则所有依赖于它的对象都会得到通知并自动更新。

> 应用最多的一种设计模式。  

> 优点：  
* 观察者与被观察者之间是抽象耦合，应对业务变化
* 增强系统灵活性、可扩展性
缺点：  
* 当观察者比较多时，会影响整体的执行效率，一般考虑异步方式

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_observer_uml.png)

* Subject —— 抽象的被观察者角色，把所有观察者对象引用保存到一个集合，提供 notify 方法
* ConcreteSubject（INerd） —— 具体的被观察者
* Observer —— 观察者接口，提供观察者被 notif 之后进行 update 操作
* ConcreteObserver —— 具体的观察者

## 示例

这里以订阅 inerd.cc 日报为例

#### Observer 观察者接口，提供更新方法

```java 
public interface Observer {
    
    /**
     * 观察者被 notify 之后的更新操作
     *
     * @param content
     */
    void update(String content);
}
```

#### Subject 抽象类

```java
public abstract class Subject {

    private List<Observer> observers;

    public Subject() {
        observers = new ArrayList<Observer>();
    }

    /**
     * 增加观察者
     *
     * @param observer
     */
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * 删除观察者
     *
     * @param observer
     */
    public void deleteObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * 通知所有的观察者对象
     *
     * @param content
     */
    public void notifyObservers(String content) {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).update(content);
        }
    }
}
```

#### Designer 具体的观察者，设计师

```java
public class Designer implements Observer {
    private String name;

    public Designer(String name) {
        this.name = name;
    }

    @Override
    public void update(String content) {
        System.out.println("Hi, " + name + ", inerd.cc 更新啦，内容：" + content);
    }
}
```

#### INerd 具体的被观察者

```java
public class INerd extends Subject {

	/**
     * 每天发布内容
     *
     * @param content
     */
    public void postNewPublication(String content) {
        notifyObservers(content);
    }
}
```

#### 测试

```java
public class ObserverTest {

    public static void main(String[] args) {
        INerd iNerd = new INerd();
        Designer designer1 = new Designer("designer1");
        Designer designer2 = new Designer("designer2");
        Designer designer3 = new Designer("designer3");

        iNerd.addObserver(designer1);
        iNerd.addObserver(designer2);
        iNerd.addObserver(designer3);
        iNerd.postNewPublication("新的一期创意编程干货资源日报发布啦...");

        // 输出：
        // Hi, designer1, inerd.cc 更新啦，内容：新的一期创意编程干货资源日报发布啦...
		// Hi, designer2, inerd.cc 更新啦，内容：新的一期创意编程干货资源日报发布啦...
		// Hi, designer3, inerd.cc 更新啦，内容：新的一期创意编程干货资源日报发布啦...

        iNerd.addObserver(designer2);
        iNerd.postNewPublication("新的一期创意编程干货资源日报发布啦...");

        // 输出：
        // Hi, designer1, inerd.cc 更新啦，内容：新的一期创意编程干货资源日报发布啦...
		// Hi, designer3, inerd.cc 更新啦，内容：新的一期创意编程干货资源日报发布啦...
    }
}
```