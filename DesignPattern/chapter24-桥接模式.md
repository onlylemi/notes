# chapter24-桥接模式

将抽象部分与实现部分分离，使它们都可以独立地进行变化。

> 连接**抽象部分**和**实现部分**

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_bridge_uml.png)

* Abstraction（Coffee） —— 抽象部分。该类保持一个对
* RefinedAbstration（LargeCoffee、SmallCoffee） —— 优化的抽象部分。抽象部分的具体实现，一般是对抽象部分的方法进行完善和扩展。
* Implementor（CoffeeAdditives） —— 实现部分，接口或抽象类。对抽象部分的方法进行完善和扩展。
* ConcreteImplement...（Sugar、Ordinary） —— 完善实现部分的具体实现。

## 示例

> 以生活中去咖啡店点咖啡，大杯加糖、大杯不加糖、小杯加糖、小杯不加糖

#### Coffee 抽象的咖啡接口

```java
public abstract class Coffee {
    protected CoffeeAdditives additives;

    public Coffee(CoffeeAdditives additives) {
        this.additives = additives;
    }

    public abstract void makeCoffee();
}

```

#### LargeCoffee 大杯咖啡

```java
public class LargeCoffee extends Coffee {

    public LargeCoffee(CoffeeAdditives additives) {
        super(additives);
    }

    @Override
    public void makeCoffee() {
        System.out.println("大杯的" + additives.addThing() + "咖啡");
    }
}
```

#### SmallCoffee 小杯咖啡

```java
public class SmallCoffee extends Coffee {

    public SmallCoffee(CoffeeAdditives additives) {
        super(additives);
    }

    @Override
    public void makeCoffee() {
        System.out.println("小杯的" + additives.addThing() + "咖啡");
    }
}
```

#### CoffeeAdditives 添加剂

```java
public interface CoffeeAdditives {
    /**
     * 具体加什么，由子类控制
     */
    String addThing();
}
```

#### Sugar 加糖

```java
public class Sugar implements CoffeeAdditives {
    @Override
    public String addThing() {
        return "加糖";
    }
}
```

#### Ordinary 原味

```java
public class Ordinary implements CoffeeAdditives {
    @Override
    public String addThing() {
        return "原味";
    }
}

```

#### 测试

```java
public abstract class BridgeTest {

    public static void main(String[] args) {
        CoffeeAdditives additives = new Sugar();
        Coffee coffee = new LargeCoffee(additives);
        coffee.makeCoffee();

        CoffeeAdditives additives1 = new Ordinary();
        Coffee coffee1 = new SmallCoffee(additives1);
        coffee1.makeCoffee();

        // 输出：
        // 大杯的加糖咖啡
		// 小杯的原味咖啡
    }
}
```