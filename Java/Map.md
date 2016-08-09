# 从源码角度了解 Map

## 类图

* Map
    * HashMap
        * LinkedHashMap
    * Hashtable
    * SortedMap
        * TreeMap

> 参考解析：  
* HashMap：[http://zhangshixi.iteye.com/blog/672697](http://zhangshixi.iteye.com/blog/672697)
* Hashtable：[http://www.cnblogs.com/skywang12345/p/3310887.html](http://www.cnblogs.com/skywang12345/p/3310887.html)
* LinkedHashMap：[http://zhangshixi.iteye.com/blog/673789](http://zhangshixi.iteye.com/blog/673789)
* 红黑树：[http://blog.csdn.net/coslay/article/details/47083897](http://blog.csdn.net/coslay/article/details/47083897)
* TreeMap：[http://cmsblogs.com/?p=1013](http://cmsblogs.com/?p=1013)

## HashMap

底层就是一个 `Node` 数组结构，数组中的每一项又是一个链表。每个 `Node` 就是一个 `key-value` 对，它持有一个指向下一个元素的引用，这就构成了链表。

```java
	transient Node<K,V>[] table; // 数组

	static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next; // 链表

        // ......
    }
```

## LinkedHashMap

LinkedHashMap 采用的 hash 算法和 HashMap 相同，但是它重新定义了数组中保存的元素 Entry，该 Entry 除了保存当前对象的引用外，还保存了 其上一个元素 `before` 和下一个元素 `after` 的引用，从而在哈希表的基础上又构成了双向链接列表。


```java
	transient LinkedHashMap.Entry<K,V> head; // 头指针
	transient LinkedHashMap.Entry<K,V> tail; // 尾指针

	// 排序模式顺序（默认 false）
	// false 为插入顺序，
	// true 为访问顺序，按从近期访问最少到近期访问最多的顺序（get 方法中操作）
	final boolean accessOrder; 

	static class Entry<K,V> extends HashMap.Node<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
    }
```

该哈希映射的迭代顺序就是最后访问其条目的顺序，这种映射很适合构建 `LRU 缓存`。LinkedHashMap 提供了 `removeEldestEntry(Map.Entry<K,V> eldest)` 方法，在将新条目插入到映射后，put 和 putAll 将调用此方法。该方法可以提供在每次添加新条目时移除最旧条目的实现程序，默认返回 `false` ，这样，此映射的行为将类似于正常映射，即永远不能移除最旧的元素。

```java
	protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return false;
    }

    // 示例，构建 LRU 缓存
    private static final int MAX_ENTRIES = 100;  
	protected boolean removeEldestEntry(Map.Entry eldest) {  
    	return size() > MAX_ENTRIES;  
	} 
```

## 总结

### HashMap

* 继承自 AbstractMap 类
* 数组 + 链表
* put 时，根据 key 的 hashCode 重新计算 hash 值 `hash = hash(key.hashCode())`，找到元素在数组中的位置，然储存在该位置，如果已经存在，则在该位置以链表的形式进行存放，新加入的放在链头，最先加入的在链尾
* 初始容量为 **16**（而且一定是 **2** 的指数），加载因子为 **0.75**（存储数 > 容量 x 0.75，就扩容）
* key 不能重复，value 可以重复
* 允许 key=null，value=null，key=null 时储存在数组的头部，因此不能通过 `get()` 方法来判断 HashMap 中是否存在某个 key，而应该用 `containsKey()` 方法来判断
* 不支持线程的同步，如果需要同步，可以用
Collections 的 synchronizedMap 方法使 HashMap 具有同步的能力，或者使用 **ConcurrentHashMap**
* 调用 `resize()` 方法后，会重新计算每个元素在数组中的位置，而这是一个非常消耗性能的操作。所以如果我们已经预知 HashMap 中元素的个数，那么预设元素的个数能够有效的提高 HashMap 的性能
* 遍历时采用 **Iterator**，快速失败

### Hashtable

* 继承自 Dictionary 类
* 初始容量为 **11**，负载因子为 **0.75** 
* 不允许记录的 key 或者 value 为 null
* 线程安全
* put 时，直接使用 `hash = key.hashCode()`;
* 遍历时采用 **Enumeration**

![](https://raw.githubusercontent.com/onlylemi/res/master/java-hashtable-concurrenthashmap.jpg)

### ConcurrentHashMap 

* ConcurrentHashMap 由多个 segment 组成，每一个 segment 都包含了一个 HashEntry 数组的 hashtable， 每一个 segment 包含了对自己的 hashtable 的操作，比如 get，put，replace 等操作，这些操作发生的时候，对自己的 hashtable 进行锁定。由于每一个 segment 写操作只锁定自己的 hashtable，性能无疑好于只有一个 hashtable 锁定的情况。

### LinkedHashMap

* 继承自 HashMap
* 维护着一个运行于所有条目的双重链接列表（head、tail）
* 默认保存了记录的插入顺序 `accessOrder`
    * 插入顺序，FIFO
    * 访问顺序，类似于LRU（重写 `removeEldestEntry` 方法，可以实现 LRU 缓存）
* 允许使用 null 值和 null 键
* 线程不安全

### Iterator 和 Enumeration 区别

* **Enumeration** 只有两个函数接口，只可以读取数据，而不能对数据进行修改；而 Iterator 除了可以读取数据外，也可进行删除
* **Iterator** 具有 fail-fast 机制

### fail-fast 机制

* 在创建迭代器之后，除非通过迭代器自身的 **remove** 或 **add** 方法从结构上对列表进行修改，否则在任何时间以任何方式对列表进行结构上的修改，迭代器都会抛出 ConcurrentModificationException。因此，面对并发的修改，迭代器很快就会完全失败，而不是冒着在将来某个不确定时间发生任意不确定行为的风险。
* 迭代器的快速失败行为无法得到保证，因为一般来说，不可能对是否出现不同步并发修改做出任何硬性保证。快速失败迭代器会尽最大努力抛出 ConcurrentModificationException。因此，为提高这类迭代器的正确性而编写一个依赖于此异常的程序是错误的做法：**迭代器的快速失败行为应该仅用于检测 bug**。
* fail-fast 是根据 `modCount`，来记录该对象进行结构性修改的次数。迭代器在创建的时候，expectedModCount 等于对象的 modCount。