# chapter1-面向对象六大原则

## 单一职责原则（SRP）

一个类应该有一组相关项很高的函数、数据的封装。一个类只负责一个功能，完全不相关的两个功能不应该放在一个类中。

> 不能为图代码量少，把牛头马嘴一起往一个类塞。

## 开闭原则（OCP）

对扩展开发，对修改封闭。

> 程序一旦开发完成，程序中的一个类的实现只应该因错误而被修改，新的或者改变的特性应该通过新建不同的类实现。

## 里氏替换原则（LSP）

所有引用基类的地方必须透明地使用其子类的对象。

> 只要父类能出现的地方，子类也可以出现，同时替换成子类也不会产生任何错误或异常，使用者可能根本不知道其实父类还是子类，但是反过来就不行了，有子类出现的额地方，由子类出现的地方，父类未必就能适应。

核心原理是抽象，抽象又依赖继承。

> 继承
* 优点
	* 代码重用，减少创建成本，每个子类都用于父类的方法和属性
	* 提高代码可用性
* 缺点
	* 继承是侵入性的，只要继承就必须拥有父类的属性和方法
	* 造成子类代码冗余、灵活性降低，因为子类必须拥有父类的属性和方法

## 依赖倒置原则（DIP）

解耦，面向接口或抽象编程

> 模块之间的依赖通过后续发生，实现类之间不发生直接的依赖关系，其依赖关系是通过接口或抽象类产生的。

* 高层次模块不应该依赖低层次模块，两者都应该依赖其抽象
* 抽象不应该依赖细节
* 细节应该依赖抽象

## 接口隔离原则（ISP）

客户端不应该依赖它不需要的接口，其目的是系统解开耦合，从而更容易重构、更改、重新部署。

```java
public final class CloseUtils {

	private CloseUtils() {}

	public static void closeQuietly(Closeable closeable) {
		if (null != closeable) {
			try {
				closeable.close();
			} catch (EXception e) {
				e.printStackTrace();
			}
		}
	}
}

public void put (String url, Bitmap bmp) {
	FileOutputStream fileOutputStream = null;
	try {
		//......
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} finally {
		CloseUtils.closeQuietly(fileOutputStream);
	}
}
```

## 迪米特原则（LOD）

一个对象应该对其他对象有最少的了解。

> 一个类应该对自己需要耦合或者调用的类知道的最少，累的内部如何实现的与调用者或者依赖者没有关系，调用者或者依赖者只需知道它的需要的方法即可。