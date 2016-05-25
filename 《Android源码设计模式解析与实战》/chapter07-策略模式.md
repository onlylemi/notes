# chapter7-策略模式

定义一系列的算法，并将每个算法封装起来，并且使它们之间可以交换

> **优点**：  
* 结构清晰
* 耦合度降低  

> **缺点**：  
* 随着策略的增加，子类也会变得繁多，带来额外开销
* 对客户端暴露所有的策略，有悖于[迪米特法则](https://github.com/onlylemi/notes/blob/master/%E3%80%8AAndroid%E6%BA%90%E7%A0%81%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F%E8%A7%A3%E6%9E%90%E4%B8%8E%E5%AE%9E%E6%88%98%E3%80%8B/chapter1-%E9%9D%A2%E5%90%91%E5%AF%B9%E8%B1%A1%E5%85%AD%E5%A4%A7%E5%8E%9F%E5%88%99.md#迪米特原则lod)

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_strategy_uml.png)

* Context —— 用来操作策略的上下文
* Strategy（SortStrategy） —— 策略的抽象
* BubbleSort、QuickSort、SelectSort —— 具体的策略实现

## 示例

#### SortStrategy 策略的抽象

```java
public interface SortStrategy {
    void sort();
}
```

#### BubbleSort 策略的具体实现1

```java
public class BubbleSort implements SortStrategy {
    @Override
    public void sort() {
        System.out.println("Bubble Sort");
    }
}
```

#### QuickSort 策略的具体实现2

```java
public class QuickSort implements SortStrategy {
    @Override
    public void sort() {
        System.out.println("Quick Sort");
    }
}
```

#### SelectSort 策略的具体实现3

```java
public class SelectSort implements SortStrategy {
    @Override
    public void sort() {
        System.out.println("Select Sort");
    }
}
```

#### Context 操作策略

```java
public class Context {

    private SortStrategy strategy;

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void execute() {
        strategy.sort();
    }
}
```

#### StrategyTest 测试

```java
public class StrategyTest {

    public static void main(String[] args) {
        Context context = new Context();
        context.setStrategy(new BubbleSort());
        context.execute();

        // 输出：Bubble Sort
    }
}
```

