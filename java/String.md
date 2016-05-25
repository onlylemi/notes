# 从源码角度了解 String

String、StringBuilder，StringBuffer 的到底有什么样的区别？

## 类图

首先先看下类图（下图）

![String类图](https://raw.githubusercontent.com/onlylemi/res/master/java_string.png)

## 类定义

我们再看下每个类具体是怎么定义的（JDK1.8）

```java
// CharSequence 类定义
public interface CharSequence {
...
}

// AbstractStringBuilder 类定义
abstract class AbstractStringBuilder implements Appendable, CharSequence {
...
}

// String 类定义
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
...
}

// StringBuffer 类定义
public final class StringBuffer
    extends AbstractStringBuilder
    implements java.io.Serializable, CharSequence{
...
}

// StringBuilder 类定义
public final class StringBuilder
    extends AbstractStringBuilder
    implements java.io.Serializable, CharSequence {
...
}
```


从以上各个类的定义中，我们可以发现 CharSequence 是一个定义字符串操作的接口，AbstractStringBuilder、String、StringBuilder、StringBuffer 都实现了它，而 StringBuilder、StringBuffer 继承了 AbstractStringBuilder 这个抽象类。那他们之间到底有什么不一样呢？

## 区别

我相信大家应该都知道 String、StringBuilder、StringBuffer 的区别了。

> * String 字符串常量，不可变得对象。对其进行改变的同时会在串池中新生成一个 String 对象，进而指向这个对象。耗内存
> * StringBuffer 字符串变量（线程安全）
> * StringBulder 字符串变量（线程不安全）

可是这是怎么得到的呢（反正我之前就不是很清楚）？下面我就给大家从源码角度说说，如有错误，请批评指导！

## String

String 的底层是一个数组，我们的操作都是基于这个数组来进行的

```java
private final char value[];
```

再看 String 的部分构造函数，当新生成一个实例时，value 的长度就是你传入字符串的长度

```java
// String 构造函数
public String() {
    this.value = new char[0];
}

public String(String original) {
    this.value = original.value;
    this.hash = original.hash;
}

public String(char value[]) {
    this.value = Arrays.copyOf(value, value.length);
}
```

同时可以再看看 String 的拼接函数 `concat()`，我们可以很明显的看出返回的是一个新的 String 对象，所以当你改变一个 String 时，它就指向一个新的地址，而不再之前的地址。

```java
// String 拼接函数
public String concat(String str) {
    int otherLen = str.length();
    if (otherLen == 0) {
        return this;
    }
    int len = value.length;
    char buf[] = Arrays.copyOf(value, len + otherLen);
    str.getChars(buf, len);
    return new String(buf, true);
}
```

接下来我们再看看 StringBulder、StringBuffer，不过在介绍之前我们先看看他们的父类 AbstractStringBuilder。

## AbstractStringBuilder

你会发现这个类中定义了两个变量

```java
/**
 * The value is used for character storage.
 * 这个 value 用来存储你的字符串
 */
char[] value;

/**
 * The count is the number of characters used.
 * count 是这个 value 你占用了字符的个数，什么意思呢？比如说，你定义了一个 values[10]，而你只存储了 “abcdefg”，你只占用了 7 个字符，所以你的 count 就是 7，而不是 10
 */
int count;
```

再看看构造函数，需要传入一个**容量**，也就是 value 数组的大小。

```java
// AbstractStringBuilder 构造函数
AbstractStringBuilder(int capacity) {
    value = new char[capacity];
}
```

```java
public int length() {
    return count;
}

public int capacity() {
    return value.length;
}
```

从以上代码，我们会发现我们平常所调用的 `length()` 方法，其实得到都是本身字符串所占用的长度，而 `capacity()` 才是数组空间的容量。

那 StringBulder 是如何做拼接的呢，我们先看它的拼接函数

```java
public AbstractStringBuilder append(String str) {
    if (str == null)
        return appendNull();
    int len = str.length();
    ensureCapacityInternal(count + len);
    str.getChars(0, len, value, count);
    count += len;
    return this;
}

private AbstractStringBuilder appendNull() {
    int c = count;
    ensureCapacityInternal(c + 4);
    final char[] value = this.value;
    value[c++] = 'n';
    value[c++] = 'u';
    value[c++] = 'l';
    value[c++] = 'l';
    count = c;
    return this;
}
```

可以看出

* 在调用 `append()` 的时候，不能传入 null，否则会调用 `appendNull()` 方法，直接给你拼接一个 `“null”` 的字符串，一定要切记，不能传空。
* 拼接前都会调用 `ensureCapacityInternal()` 方法，来确保当前的 value 的容量是否够用，不够就加容量。从下面的方法中我们可以看出，每次增加容量都为原来的 2 倍再 +2

```java
void expandCapacity(int minimumCapacity) {
    // 原来的 2 倍再 +2
    int newCapacity = value.length * 2 + 2;
    if (newCapacity - minimumCapacity < 0)
        newCapacity = minimumCapacity;
    if (newCapacity < 0) {
        if (minimumCapacity < 0) // overflow
            throw new OutOfMemoryError();
        newCapacity = Integer.MAX_VALUE;
    }
    value = Arrays.copyOf(value, newCapacity);
}
```

同时，如果你觉得这样太浪费空间的话，你可以调用 `trimToSize()`  方法，这样你的 `length()` 就和 `capacity()` 一样了。

```java
public void trimToSize() {
    if (count < value.length) {
        value = Arrays.copyOf(value, count);
    }
}
```

从以上介绍你会发现，value 会根据你字符串的大小进行扩充，但是始终都是在操作同一个 value，所以指向的始终是同一个地址。

接下来再看 StringBuilder、StringBuffer。

### StringBuilder

StringBulder 它继承了 AbstractStringBuilder，看他的源码你会发现，其构造函数中指定了容量的大小，其他地方实现基本都是引用父类方法。

```java
// 默认指定容量大小为 16
public StringBuilder() {
    super(16);
}

public StringBuilder(int capacity) {
    super(capacity);
}

// 如果传入字符串，则初始容量就为 str 的长度+16
public StringBuilder(String str) {
    super(str.length() + 16);
    append(str);
}
```

### StringBuffer

而 StringBuffer 它也继承了 AbstractStringBuilder，看他的源码你会发现，其构造函数中也是指定了容量的大小，其他地方实现基本都是引用父类方法，不过都添加了 synchronized 关键字，所以应该知道，为什么 StringBuffer 是线程安全，而 StringBuilder 不是了吧。但是很明显加上线程控制会拖慢程序运行的速度，所以如果不需要线程控制，那么最好就用 StringBuilder。

```java
// 默认指定容量大小为 16
public StringBuffer() {
    super(16);
}
    
public StringBuffer(int capacity) {
    super(capacity);
}

public StringBuffer(String str) {
    super(str.length() + 16);
    append(str);
}
```

## 补充

### String 的 equals() 方法

* 首先是对象判断是否为同一对象
* 判断是否为 String
* 判断每个字符是否相等

```java
public boolean equals(Object anObject) {
    if (this == anObject) {
        return true;
    }
    if (anObject instanceof String) {
        String anotherString = (String)anObject;
        int n = value.length;
        if (n == anotherString.value.length) {
            char v1[] = value;
            char v2[] = anotherString.value;
            int i = 0;
            while (n-- != 0) {
                if (v1[i] != v2[i])
                    return false;
                i++;
            }
            return true;
        }
    }
    return false;
}
```

### String 的 hashCode() 方法

* 31 奇素数

```java
public int hashCode() {
    int h = hash;
    if (h == 0 && value.length > 0) {
        char val[] = value;
            for (int i = 0; i < value.length; i++) {
            h = 31 * h + val[i];
        }
        hash = h;
    }
    return h;
}
```