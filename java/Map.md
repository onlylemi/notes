# 从源码角度了解 Map

先给大家介绍 `Map`，之后再介绍 `Set`，在网上看到好多大神写的都非常详细，我就不做更多的说明，结合他们的源码解析，我做个总结。

## 类图

* Map
    * HashMap
        * LinkedHashMap
    * Hashtable
    * SortedMap
        * TreeMap

## 源码解析

* HashMap：[http://zhangshixi.iteye.com/blog/672697](http://zhangshixi.iteye.com/blog/672697)
* Hashtable：[http://www.cnblogs.com/skywang12345/p/3310887.html](http://www.cnblogs.com/skywang12345/p/3310887.html)
* LinkedHashMap：[http://zhangshixi.iteye.com/blog/673789](http://zhangshixi.iteye.com/blog/673789)
* 红黑树：[http://blog.csdn.net/coslay/article/details/47083897](http://blog.csdn.net/coslay/article/details/47083897)
* TreeMap：[http://blog.csdn.net/chenssy/article/details/26668941](http://blog.csdn.net/chenssy/article/details/26668941)

## 总结

### HashMap

* 继承自 AbstractMap 类
* 数组 + 链表
* put时，根据 key 的 keyCode 重新计算 hash 值 `hash = hash(key.hashCode())`，找到元素在数组中的位置，然储存在该位置，如果已经存在，则在该位置以链表的形式进行存放，新加入的放在链头，最先加入的在链尾
* 初始容量为 **16**（而且一定是 **2** 的指数），负载因子为 **0.75**
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

### LinkedHashMap

* 继承自 HashMap 类
* 维护着一个运行于所有条目的双重链接列表
* 默认保存了记录的插入顺序 `accessOrder`
    * 插入顺序，FIFO
    * 访问顺序，类似于LRU
* 允许使用 null 值和 null 键
* 线程不安全

### Iterator 和 Enumeration 区别

* **Enumeration** 只有两个函数接口，只可以读取数据，而不能对数据进行修改；而 Iterator 除了可以读取数据外，也可进行删除
* **Iterator** 具有 fail-fast 机制

### fail-fast 机制

* 在创建迭代器之后，除非通过迭代器自身的 **remove** 或 **add** 方法从结构上对列表进行修改，否则在任何时间以任何方式对列表进行结构上的修改，迭代器都会抛出 ConcurrentModificationException。因此，面对并发的修改，迭代器很快就会完全失败，而不是冒着在将来某个不确定时间发生任意不确定行为的风险。
* 迭代器的快速失败行为无法得到保证，因为一般来说，不可能对是否出现不同步并发修改做出任何硬性保证。快速失败迭代器会尽最大努力抛出 ConcurrentModificationException。因此，为提高这类迭代器的正确性而编写一个依赖于此异常的程序是错误的做法：**迭代器的快速失败行为应该仅用于检测 bug**。
* fail-fast 是根据 `modCount`，来记录该对象进行结构性修改的次数。迭代器在创建的时候，expectedModCount 等于对象的 modCount。