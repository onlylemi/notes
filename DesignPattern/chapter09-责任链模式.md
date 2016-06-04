# chapter9-责任链模式

很多对象都有机会处理请求，从而避免了请求的发送者与接收者之间的耦合关系。将这些对象连成一条链传递该请求，直到有对象处理它为止。

> 对于一个请求，大家都可以处理，只是每个人的牛逼程度不一样，处理不了就给别人去处理。

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_handler_uml.png)

* Client（HandlerTest） —— 调用类
* Leader（Handler） —— 处理请求的抽象类或接口，包含指向下一个处理类的 nextHandler 和 一个处理请求的方法 handRequest()，主要主导思想就是判断是否满足条件，满足处理，不满足由 nextHandler 去处理
* ConcreteHandler（Boss、Manager、Director、GroupLeader） —— 具体的 Handler 处理类

## 示例

在公司报销钱为例

#### Leader 处理请求接口

```java
public abstract class Leader {
    public Leader nextHandler;

    public final void handlerQequest(int money) {
        if (money <= limit()) {
            handler(money);
        } else {
            if (nextHandler != null) {
                nextHandler.handlerQequest(money);
            }
        }
    }

    public abstract int limit();

    public abstract void handler(int money);
}
```

#### Boss 具体的处理类1

```java
public class Boss extends Leader {
    @Override
    public int limit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void handler(int money) {
        System.out.println("Boss 报销 " + money + "元");
    }
}
```

#### Manager 具体的处理类2

```java
public class Manager extends Leader {
    @Override
    public int limit() {
        return 10000;
    }

    @Override
    public void handler(int money) {
        System.out.println("Manager 报销 " + money + "元");
    }
}
```

#### Director 具体的处理类3

```java
public class Director extends Leader {
    @Override
    public int limit() {
        return 5000;
    }

    @Override
    public void handler(int money) {
        System.out.println("Director 报销 " + money + "元");
    }
}
```

#### GroupLeader 具体的处理类4

```java
public class GroupLeader extends Leader {
    @Override
    public int limit() {
        return 1000;
    }

    @Override
    public void handler(int money) {
        System.out.println("GroupLeader 报销 " + money + "元");
    }
}
```

#### HandlerTest 测试类

```java
public class HandlerTest {

    public static void main(String[] args) {
        Leader groupLeader = new GroupLeader();
        Leader director = new Director();
        Leader manager = new Manager();
        Leader boss = new Boss();

        groupLeader.nextHandler = director;
        director.nextHandler = manager;
        manager.nextHandler = boss;

        // 报销 5000
        groupLeader.handlerQequest(5000);
        // 报销 50000
        groupLeader.handlerQequest(50000);


        // 输出：
        // Director 报销 5000元
        // Boss 报销 50000元
    }
}
```