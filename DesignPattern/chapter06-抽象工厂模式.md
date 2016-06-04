# chapter6-抽象工厂模式

为创建一组相关或者是相互依赖的对象提供一个接口，而无需指定它们的具体类

> 工厂方法模式针对的是一个产品等级结构；而抽象工厂模式则是针对的多个产品等级结构。  
在抽象工厂模式中，有一个产品族的概念：是指位于不同产品等级结构中功能相关联的产品组成的家族。抽象工厂模式所提供的一系列产品就组成一个产品族；而工厂方法提供的一系列产品称为一个等级结构。

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_abstract_factory_uml.png)

## 示例

#### Keyboard 产品1接口

```java
public interface Keyboard {
    void display();
}
```

#### Keyboard 产品1实现1

```java
public class NormalKeyboard implements Keyboard {
    @Override
    public void display() {
        System.out.println("Normal Keyboard");
    }
}
```

#### Keyboard 产品1实现2

```java
public class MechanicalKeyboard implements Keyboard {
    @Override
    public void display() {
        System.out.println("Mechanical Keyboard");
    }
}
```

#### Screen 产品2接口

```java
public interface Screen {
    void display();
}
```

#### Screen 产品2实现1

```java
public class NormalScreen implements Screen {
    @Override
    public void display() {
        System.out.println("Normal Screen");
    }
}
```

#### Screen 产品2实现2

```java
public class LCScreen implements Screen {
    @Override
    public void display() {
        System.out.println("LC Screen");
    }
}
```

#### PCFactory 工厂接口

```java
public interface PCFactory {

    Keyboard createKeyboard();

    Screen createScreen();
}
```

#### MacFactory 工厂的具体实现1

```java
public class MacFactory implements PCFactory {
    @Override
    public Keyboard createKeyboard() {
        return new MechanicalKeyboard();
    }

    @Override
    public Screen createScreen() {
        return new LCScreen();
    }
}
```

#### 测试

```java
public class PCFactoryTest {

    public static void main(String[] args) {
        PCFactory factory = new MacFactory();
        Keyboard keyboard = factory.createKeyboard();
        Screen screen = factory.createScreen();
        keyboard.display();
        screen.display();

        // 输出：
        // Mechanical Keyboard
		// LC Screen
    }
}
```



