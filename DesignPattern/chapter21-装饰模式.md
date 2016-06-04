# chapter21-装饰模式

动态地给一个对象添加一些额外的职责。就增加功能来说，装饰模式相比生成子类更为灵活。

> 感觉跟代理模式有点像   
**装饰者模式**是以客户端透明的方式扩展对象的功能，是继承关系的一个替代方案。应该为所装饰的对象增强功能。   
**代理模式**则是给一个对象提供一个对象提供一个代理对象，并有代理对象来控制对原有对象的引用。对代理的对象施加控制，但不对对象本身的功能增强。

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_decorator_uml.png)

* Component（Person） —— 抽象组件，接口或抽象类，充当被修饰的原始对象。
* ConcreteComponent（Boy） —— 具体组件，装饰的具体对象
* Decorator（PersonCloth） —— 抽象装饰者，抽象类。装饰组件对象，其内部一定要有一个纸箱组件对象的引用。
* ConcreteDecorator（ExpensiveCloth、CheapCloth） —— 具体的装饰者类。

## 示例

> 以一个男孩穿贵的还是便宜的衣服为例。

#### Person 抽象的人

```java
public interface Person {

    /**
     * 穿着
     */
    void dressed();
}
```

#### Boy 具体组件 boy

```java
public class Boy implements Person {
    @Override
    public void dressed() {
        System.out.println("穿内衣");
    }
}
```

#### PersonCloth 装饰者，保留一个 Person 的引用

```java
public abstract class PersonCloth implements Person {

    protected Person person;

    public PersonCloth(Person person) {
        this.person = person;
    }

    @Override
    public void dressed() {
        person.dressed();
    }
}
```

#### ExpensiveCloth 装饰者实现1

```java
public class ExpensiveCloth extends PersonCloth {

    public ExpensiveCloth(Person person) {
        super(person);
    }

    @Override
    public void dressed() {
        super.dressed();
        dressShirt();
        dressJean();
        dressShoes();
    }

    private void dressShirt() {
        System.out.println("穿短袖");
    }

    private void dressJean() {
        System.out.println("穿牛仔裤");
    }

    private void dressShoes() {
        System.out.println("穿运动鞋");
    }
}
```

#### CheapCloth 装饰者实现2

```java
public class CheapCloth extends PersonCloth {

    public CheapCloth(Person person) {
        super(person);
    }

    private void dressShorts() {
        System.out.println("穿短裤");
    }

    @Override
    public void dressed() {
        super.dressed();
        dressShorts();
    }
}
```

#### 测试

```java
public class DecoratorTest {
    public static void main(String[] args) {
        Person person = new Boy();

        PersonCloth clothExpensive = new ExpensiveCloth(person);
        clothExpensive.dressed();

        // 输出：
        // 穿内衣
		// 穿短袖
		// 穿牛仔裤
		// 穿运动鞋

        PersonCloth clothCheap = new CheapCloth(person);
        clothCheap.dressed();

        // 输出：
        // 穿内衣
		// 穿短裤
    }
}
```

