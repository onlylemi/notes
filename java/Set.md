# 从源码角度了解 Set

## 类图

* Set
    * HashSet
        * LinkedHashSet
    * SortedSet
        * TreeSet
        
## 源码解析

* HashSet：http://zhangshixi.iteye.com/blog/673143
* LinkedHashSet：http://zhangshixi.iteye.com/blog/673319
* TreeSet：http://blog.csdn.net/speedme/article/details/22661671

## 总结

## HashSet

* 实现 Set 接口，内部由一个 HashMap 实例来支持，HashSet 中的元素都存放在 HashMap 的 key上面，而 value 中的值都是统一的一个 `private static final Object PRESENT = new Object();`，所以 HashSet 中不允许有重复元素（key 不能重复）
* HashSet 跟 HashMap一样，都是一个存放链表的数组
* 和 HashMap 一样，默认初始容量 **16**，加载因子 **0.75**
* 线程不安全

## LinkedHashSet

* 继承自 HashSet
* 内部是由一个 LinkedHashMap 实例支持
* LinkedHashSet 的构造方法时依托于 HashSet 的，耦合性极高。HashSet 专门为 LinkedHashSet 保留了一个构造方法

```java
// LinkedHashSet 构造方法，调用父类
super(initialCapacity, loadFactor, true);

// HashSet 的构造方法
HashSet(int initialCapacity, float loadFactor, boolean dummy) {
    map = new LinkedHashMap<>(initialCapacity, loadFactor);
}
```

## 推荐阅读

[从源码角度了解 Map](https://github.com/onlylemi/notes/blob/master/java/Map.md)