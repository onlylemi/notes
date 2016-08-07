# 从源码角度了解 List

![集合类图](https://raw.githubusercontent.com/onlylemi/res/master/java_collection.png)

## Collection

* Collection 是集合的一个接口，该接口没有具体的实现，但是提供了更为具体的子接口，List、Set、Queue。
* 继承自 Iterable，Iterable
 中的 `iterator()` 方法会返回一个迭代器对象

## Iterator

Iterator 是迭代器，此接口中定义了三个方法

* `hasNext()` —— 判断下一个是否存在元素
* `next()` —— 指向下一个元素
* `remove()` —— 删除元素，remove 前必须调用一次 next()

> ### Iterator 与 Iterable 区别
> 因为 Iterator 接口的核心方法 `next()` 或者 `hasNext()` 是依赖于迭代器的当前迭代位置的。如果 Collection 直接实现 Iterator 接口，势必导致集合对象中包含当前迭代位置的数据(指针)。当集合在不同方法间被传递时，由于当前迭代位置不可预置，那么 `next()` 方法的结果会变成不可预知。除非再为 Iterator 接口添加一个 `reset()` 方法，用来重置当前迭代位置。但即时这样，Collection 也只能同时存在一个当前迭代位置。而 Iterable 则不然，每次调用都会返回一个从头开始计数的迭代器。多个迭代器是互不干扰的。

### ListIterator

* 实现了逆序遍历，在 Iterator 基础上多了三个方法
    * `set()` —— 重写设置元素
    * `hashPrevious()` —— 判断前面是否存在元素
    * `previous()` —— 指向上一个元素

```java
// 正序遍历
ListIterator it = list.listIterator()
while(it.hasNext()){
    E e = it.next();
}

// 逆序遍历
ListIterator it = list.listIterator(list.size())
while(it.hasPrevious()){
    E e = it.previous();
}
```

## ArrayList

ArrayList 是 List 的一个实现类

* 它是一个顺序表，底层是数组 `elementData[]`， 初始化是默认为空，也可以根据需求设置容量大小 initialCapacity。

```java
transient Object[] elementData;

// ArrayList 构造函数
public ArrayList(int initialCapacity) {
    if (initialCapacity > 0) {
        this.elementData = new Object[initialCapacity];
    } else if (initialCapacity == 0) {
        this.elementData = EMPTY_ELEMENTDATA;
    } else {
        throw new IllegalArgumentException("Illegal Capacity: "+
                                           initialCapacity);
    }
}

public ArrayList() {
    // 默认设置为 null
    this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
}
```

* 第一次定义的时候没有指定数组的长度则长度是 **0**，在第一次添加的时候是判断如果是空则追加 **10**。同时 ArrayList 每次扩大容量时，增加原来的 **0.5** 倍

```java
// ArrayList add() 函数
public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}

private void ensureCapacityInternal(int minCapacity) {
    // 如何 elementData 为空，则会设置为 DEFAULT_CAPACITY 也就是10
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    ensureExplicitCapacity(minCapacity);
}


private void ensureExplicitCapacity(int minCapacity) {
    modCount++;
    // 如果所需的容量 > 当前实际容量，那就加
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}

private void grow(int minCapacity) {
    ...
    // 如果容量不足够时，每次增加原来的 0.5 倍
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    ...
}
```

* size 不是容量，ArrayList 对象占用的内存不是由 size 决定的，而由 elementData.length 决定。size 为当前 list 所存储的元素的数量。
* 由于其方法前没有添加 synchronized 同步锁，所以 ArrayList 不是线程安全的。

## LinkedList

LinkedList 实现了 List、Deque，LinkedList 具有两重性质。

> ### Queue
>
> * 单向队列
> * 插入、查找、移除
>   * 失败时 `add()`、`element()`、`remove()` 抛出异常
>   * 失败时 `offer()`、`peek()`、`poll()` 返回 null
> 
> ### Deque
> 
> * 双向队列

* LinkedList 底层是链表。每个结点都是一个 Node 对象，Node 是它的一个私有静态内部类，具体如下。LinkedList 中定义了一个头节点、一个尾节点，方便遍历与查找。

```java
transient Node<E> first;
transient Node<E> last;

private static class Node<E> {
    E item; // 节点上的元素
    Node<E> next; // 下一个节点
    Node<E> prev; // 上一个节点

    Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
```

* 从上一个特点中，我们就可以知道它是一个双端队列，查找是两个方向进行
* 和 ArrayList 一样，也是线程不安全的

```java
// LinkedList add() 方法
public boolean add(E e) {
    linkLast(e);
    return true;
}

// 默认在链表尾段插入
void linkLast(E e) {
    final Node<E> l = last;
    final Node<E> newNode = new Node<>(l, e, null);
    last = newNode;
    if (l == null)
        first = newNode;
    else
        l.next = newNode;
    size++;
    modCount++;
}
```

```java
// LinkedList get() 方法
public E get(int index) {
    checkElementIndex(index);
    return node(index).item;
}

Node<E> node(int index) {
    if (index < (size >> 1)) {
        Node<E> x = first;
        for (int i = 0; i < index; i++)
            x = x.next;
        return x;
    } else {
        Node<E> x = last;
        for (int i = size - 1; i > index; i--)
            x = x.prev;
        return x;
    }
}
```

### 迭代器

* `iterator()` —— 普通迭代器
* `descendingIterator()` —— 逆向顺序的迭代器，ArrayList 没有这个方法
* `listIterator()` —— ListIterator 的迭代器
* `listIterator(index)` —— 从某个位置开始的 ListIterator 迭代器

## Vector

Vector 线程安全，因为它的实现方法中加了 **synchronized** 同步锁。

从下面的 Vector 的 `grow()` 函数中可以看出

* Vector 默认初始容量为 10。当你在 Vector 初始化的时候设置了增量 capacityIncrement，Vector的容量增长将按照你设置的进行增长
* 如果没有设置，newCapacity 默认增长原来的 1 倍

```java
// Vector 默认初始容量为 10
public Vector() {
    this(10);
}

private void ensureCapacityHelper(int minCapacity) {
    // 如果所需的容量 > 当前实际容量，那就加
    if (minCapacity - elementData.length > 0)
        grow(minCapacity);
}

// Vector 容量增加函数
private void grow(int minCapacity) {
    ...
    // 每次增加原来的 1 倍
    int newCapacity = oldCapacity + ((capacityIncrement > 0) ?
                                     capacityIncrement : oldCapacity);
    ...
}
```

* Vector 中的 `setSize()` 设置的是当前 Vector 的实际存储数量 elementCount，而不是 Vector 的容量 capacity，因为其调用的是 `ensureCapacityHelper()` 这个方法

```java
// Vector setSize()方法
public synchronized void setSize(int newSize) {
    modCount++;
    if (newSize > elementCount) {
        ensureCapacityHelper(newSize);
    } else {
        for (int i = newSize ; i < elementCount ; i++) {
            elementData[i] = null;
        }
    }
    elementCount = newSize;
}

// 举例说明
Vector<String> v = new Vector<String>();
v.add("a");
v.add("b");
v.add("c");
v.setSize(4);  // v.size() = 4; v.capacity() = 10;
v.setSize(11);  // v.size() = 11; v.capacity() = 20;
```

### Stack 

Stack（栈） 是 Vector 的子类，其内部实现基本上都是调用父类的方法，封装成出栈、入栈等操作

```java
// 入栈
public E push(E item) {
    addElement(item);
    return item;
}

// 出栈
public synchronized E pop() {
    E obj;
    int len = size();
    obj = peek(); // 先查到栈顶元素，再移除，返回栈顶元素
    removeElementAt(len - 1);
    return obj;
}
// 查询栈顶元素
public synchronized E peek() {
    int len = size();
    if (len == 0)
        throw new EmptyStackException();
    return elementAt(len - 1);
}
// 查询对象的索引
public synchronized int search(Object o) {
    int i = lastIndexOf(o);
    if (i >= 0) {
        return size() - i;
    }
    return -1;
}
```

## 总结

通过以上的说明，ArrayList、LinkedList、Vector 区别也就很明显了。

* ArrayList 和 Vector 底层是采用数组方式存储数据，LinkedList 采用链表
* Vector 线程安全，ArrayList、LinkedList 线程不安全
* ArrayList 默认容量为 **0**，第一次 add 时，变为 10，之后每次增加原来的 **0.5** 倍；Vector 默认容量为 **10**，之后每次增加原来的 **1** 倍
