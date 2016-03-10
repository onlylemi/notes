# 集合

![集合类图](https://raw.githubusercontent.com/onlylemi/notes/master/images/collection.png)

### ArrayList 与 Vector 区别

* 同步性
    * ArrayList 线程不安全
    * Vector 线程安全
* 数据增长
    * ArrayList 增加为原来的 0.5 倍
    * Vector 增长为原来一倍

### HashMap 与 Hashtable 区别

* 同步性
    * HashMap 线程不安全
    * Hashtable 线程安全
* 值
    * HashMap 可以将 null 作为一个条目的 key 或 value

### List、Set、Map 各自的特点

* List 和 Set 都是单列元素的集合
    * 都继承 Collection
    * List 有序，Set 无序
    * List 可以有重复的元素，而 Set 不允许存在
* Map 是双列集合。存储一对 key->vlaue，不能储存重复的 key
