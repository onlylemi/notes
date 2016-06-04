# chapter5-工厂方法模式

定义一个用于创建对象的接口，让子类决定实例化哪一个类

> 使类的实例化延迟到其子类

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_factory_uml.png)

* 工厂（Factory）接口。与调用者直接交互用来提供产品
* 工厂（MacFactory）实现。实例化产品
* 产品（Computer）接口。定义产品规范
* 产品（MacComputer）实现。产品的具体实现行为

## 示例

#### Computer 产品接口

```java
public interface Computer {
    void display();
}

```

#### MacComputer 具体的产品Mac

```java
public class MacComputer implements Computer {

    @Override
    public void display() {
        System.out.println("MacComputer");
    }
}
```

#### WindowsComputer 具体的产品Windows

```java
public class WindowsComputer implements Computer {

    @Override
    public void display() {
        System.out.println("WindowsComputer");
    }
}
```

#### Factory 工厂接口

```java
public interface Factory {
    Computer getComputer();
}
```

#### MacFactory 工厂具体的实现Mac

```java
public class MacFactory implements Factory {

    @Override
    public Computer getComputer() {
        return new MacComputer();
    }
}
```

#### WindowsFactory 工厂具体的实现Windows

```java
public class WindowsFactory implements Factory {

    @Override
    public Computer getComputer() {
        return new WindowsComputer();
    }
}
```

#### 测试

```java
public class FactoryTest {

    public static void main(String[] args) {
        Factory factory = new MacFactory();
        Computer computer = factory.getComputer();
        computer.display();

        // 输出：MacComputer
    }
}
```

> 有时我们可以通过反射的方式更简洁的来生产具体的产品对象，工厂的实现类仅需写一次

#### ComputerFactory 

```java
public class ComputerFactory implements Factory2 {

    @Override
    public <T extends Computer> T getComputer(Class<T> t) {
        Computer computer = null;
        try {
        	// 根据类，加载具体的类
            computer = (Computer) Class.forName(t.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) computer;
    }
}
```

#### 测试

```java
public class FactoryTest {

    public static void main(String[] args) {
        Factory2 factory2 = new ComputerFactory();
        Computer computer2 = factory2.getComputer(WindowsComputer.class);
        computer2.display();

        // 输出：WindowsComputer
    }
}
```