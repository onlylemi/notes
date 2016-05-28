# chapter17-中介者模式

用一个中介者对象封装一系列的对象交互，中介者使各对象不需要显示地相互作用，从而使耦合松散，而且可以独立地改变它们之间的交互。

> 对象间不直接相互交互，中介在其中做双方事件处理

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_mediator_uml.png)

* Mediator —— 抽象中介者角色，定义同事对象到中介者对象的接口
* ConcreteMediator —— 具体的中介者角色
* Colleague —— 抽象同事角色，定义中介者对象接口，只知道中介者而不知道其他的同事
* ConcreteColleague —— 具体的同事类角色

## 示例

> 以房介为例

#### Mediator 抽象中介

```java
public abstract class Mediator {

    protected Colleague colleagueA;
    protected Colleague colleagueB;

    public abstract void handler();
}
```

#### HouseMediator 房介

```java
public class HouseMediator extends Mediator {

    public void setSeller(SellerColleague seller) {
        this.colleagueA = seller;
    }

    public void setBuyers(BuyersColleague buyers) {
        this.colleagueB = buyers;
    }

    @Override
    public void handler() {
        colleagueA.action();
        colleagueB.action();
    }
}
```

#### Colleague 抽象同事

```java
public abstract class Colleague {
    protected Mediator mediator; // 中介者对象

    public Colleague(Mediator mediator) {
        this.mediator = mediator;
    }

    /**
     * 同事角色的具体行为，由子类实现
     */
    public abstract void action();
}
```

#### BuyersColleague 买家

```java
public class BuyersColleague extends Colleague {

    public BuyersColleague(Mediator mediator) {
        super(mediator);
    }

    @Override
    public void action() {
        System.out.println("Buyers 告诉中介者自己买房的一些条件等信息");
    }
}
```

#### SellerColleague 卖家

```java
public class SellerColleague extends Colleague {

    public SellerColleague(Mediator mediator) {
        super(mediator);
    }

    @Override
    public void action() {
        System.out.println("Seller 将房子、期望等信息告诉中介者");
    }
}
```

#### 测试

```java
public class MediatorTest {

    public static void main(String[] args) {
        // 生成 房介对象
        HouseMediator mediator = new HouseMediator();

        // 买家、卖家双方
        BuyersColleague buyers = new BuyersColleague(mediator);
        SellerColleague seller = new SellerColleague(mediator);

        mediator.setBuyers(buyers);
        mediator.setSeller(seller);

        // 房介处理
        mediator.handler();
    }
}
```