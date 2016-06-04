# chapter14-迭代器模式

提供一种方法顺序访问一个容器对象中的各个元素，而不需要暴露该对象的内部表示。

> Java 中的 List、Set、Map 都包含迭代器，Android 中最为典型的就是数据库查询使用的 Cursor 都是迭代器的应用。

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_iterator_uml.png)

* Iterator —— 迭代器接口。负责定义、访问和遍历元素的接口
* ConcreteIterator —— 具体的迭代器类。具体实现迭代器接口
* Aggregate —— 容器接口。负责提供具体迭代器角色的接口
* ConcreteAggregate —— 具体容器类。具体迭代器角色与该容器相关联。

## 示例

具体可以看 Java 中集合的实现方式。

## 模板

#### Iterator

```java
public interface Iterator {
    Object next();

    boolean hasNext();
}
```

#### ConcreteIterator

```java
public class ConcreteIterator implements Iterator {
    @Override
    public Object next() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }
}
```

#### Aggregate

```java
public interface Aggregate {
    void add(Object obj);

    void remove(Object obj);

    Iterator iterator();
}
```

#### ConcreteAggregate

```java
public class ConcreteAggregate implements Aggregate {

    @Override
    public void add(Object obj) {

    }

    @Override
    public void remove(Object obj) {

    }

    @Override
    public Iterator iterator() {
        return new ConcreteIterator();
    }
}
```