# chapter13-备忘录模式

在不破坏封闭的前提下，捕获一个对象的内部状态，并在该对象之外保存这个状态，这样一后可将对象恢复到原先保存的状态。

> 就是保存，重新读取（存档）  
优点：提供一种可以恢复状态的机制；实现信息封装，用户不必关注保存细节。  
缺点：消耗资源，类成员多会耗很大资源的

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_memento_uml.png)

* Originator —— 负责创建一个备忘录，可以记录和恢复自身的内部状态
* Memento —— 备忘录角色，用于储存 Originator 的内部状态
* Caretaker —— 负责储存备忘录，不能对备忘录的内容进行操作和访问，智能将备忘录传递给其他对象

## 示例

> 假设 Originator 是一个游戏，里面有个关卡状态。具体看最后的测试代码说明

#### Originator 假设为游戏

```java
public class Originator {
    private String state = "";

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Memento createMemento() {
        return new Memento(getState());
    }

    public void restoreMemento(Memento memento) {
        this.setState(memento.getState());
    }
}
```

#### Memento 备忘录，存游戏状态

```java
public class Memento {
    private String state;

    public Memento(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
```

#### Caretaker 获取备忘录

```java
public class Caretaker {
    private Memento memento;

    public Memento getMemento() {
        return memento;
    }

    public void setMemento(Memento memento) {
        this.memento = memento;
    }
}
```

#### 测试

```java
public class MementoTest {

    public static void main(String[] args) {
        // 初始化一个游戏
        Originator originator = new Originator();
        // 设置关卡存档
        originator.setState("第二关");
        System.out.println("已存档：" + originator.getState());

        Caretaker caretaker = new Caretaker();
        // 游戏存档
        caretaker.setMemento(originator.createMemento());

        // 再建一个游戏
        Originator originator1 = new Originator();
        // 读取之前的存档
        originator1.restoreMemento(caretaker.getMemento());
        System.out.println("恢复存档：" + originator1.getState());

    }
}
```
