# Java 集合

![集合类图](http://images.cnitblog.com/blog/682514/201411/161043454007612.png)

# Iterable

* 接口。实现此接口可以调用 iterator() 方法，返回一个迭代器对象

# Iterator

* 迭代器接口。
* hashNext() 
* netx()
* remove()

> **Iterator 和 Iterable 接口的区别**
>
> * 因为 Iterator 接口的核心方法 next() 或者 hasNext() 是依赖于迭代器的当前迭代位置的。如果 Collection 直接实现 Iterator 接口，势必导致集合对象中包含当前迭代位置的数据(指针)。当集合在不同方法间被传递时，由于当前迭代位置不可预置，那么 next() 方法的结果会变成不可预知。除非再为 Iterator 接口添加一个 reset() 方法，用来重置当前迭代位置。但即时这样，Collection 也只能同时存在一个当前迭代位置。而 Iterable 则不然，每次调用都会返回一个从头开始计数的迭代器。多个迭代器是互不干扰的。

## ListIterator

* 实现了逆序遍历，在 Iterator 基础上多了三个方法
    * set()
    * hashPrevious()
    * previous()

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

# Collection

* 集合超级接口，继承 Iterable
* 该接口没有具体的直接实现，但提供了更具体的子接口，如 Set、List、Queue 等。

## List

* List 是有序的 Collection
    * 可以对每个元素的插入位置精确控制
    * 可以根据索引访问元素
* 允许重复元素
* 有自己的迭代器 ListIterator

### ArrayList、LinkedList、Vector 区别

* ArrayList 和Vector底层是采用数组方式存储数据
* Vector 线程安全
* LinkedList使用双向链表实现存储
* ArrayList（默认为10）增加为原来的 1.5 倍，Vector 增长为原来一倍

### HashMap 原理

* 数组 + 链表
* 当我们往HashMap中put元素的时候，先根据key的hashCode重新计算hash值，根据hash值得到这个元素在数组中的位置（即下标），如果数组该位置上已经存放有其他元素了，那么在这个位置上的元素将以链表的形式存放，新加入的放在链头，最先加入的放在链尾。如果数组该位置上没有元素，就直接将该元素放到此数组中的该位置上。

### HashMap 与 Hashtable 区别

* HashTable比较老，是基于Dictionary 类实现的，HashTable 则是基于 Map接口实现的
* HashTable 是线程安全的， HashMap 则是线程不安全的
* HashMap可以让你将空值作为一个表的条目的key或value

### List、Set、Map 各自的特点

* List 和 Set 都是单列元素的集合
    * 都继承 Collection
    * List 有序，Set 无序
    * List 可以有重复的元素，而 Set 不允许存在
* Map 是双列集合。存储一对 key->vlaue，不能储存重复的 key

### Fail-Fast 机制

在使用**迭代器**的过程中有其他线程修改了map，那么将抛出ConcurrentModificationException，这就是所谓fail-fast机制。这一机制在源码中的实现是通过**modCount**域，modCount顾名思义就是修改次数，对HashMap内容的修改都将增加这个值，那么在迭代器初始化过程中会将这个值赋给迭代器的expectedModCount。在迭代过程中，判断modCount跟expectedModCount是否相等，如果不相等就表示已经有其他线程修改了Map
