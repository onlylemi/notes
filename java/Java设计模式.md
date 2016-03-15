# Java 设计模式

## 高内聚低耦合

面向对象的基本原则:多聚合，少继承。低耦合，高内聚.

* 【高内聚、低耦合】
  * 内聚：每个模块尽可能独立完成自己的功能，不依赖于模块外部的代码。
  * 耦合：模块与模块之间接口的复杂程度，模块之间联系越复杂耦合度越高，牵一发而动全身。
* 目的：使得模块的“可重用性”、“移植性”大大增强
* 通常程序结构中各模块的内聚程度越高，模块间的耦合程度就越低
  * 模块粒度：
     * 『函数』
          * 高内聚：尽可能类的每个成员方法只完成一件事（最大限度的聚合）
          * 低耦合：减少类内部，一个成员方法调用另一个成员方法
     * 『类』
          * 高内聚低耦合：减少类内部，对其他类的调用
     * 『功能块』
         * 高内聚低耦合：减少模块之间的交互复杂度（接口数量，参数数据）
         * 横向：类与类之间、模块与模块之间
         * 纵向：层次之间
         * 尽可能，内容内聚，数据耦合
     * 【多聚合、少继承】
         * 聚合：事物A由若干个事物B组成，体现在类与类之间的关系就是：“类B的实例”作为“类A”的“成员对象”出现。
         * 继承：顾名思义，体现在类与类之间的关系就是：“类B”被类A所继承
         * 显然，当观察类B所具有的行为能力时，“聚合”方式更加清晰。
         * 典型应用：java适配器模式中，优选“对象适配器”，而不是“类适配器”
         

## Java 设计原则(6)

http://blog.csdn.net/cq361106306/article/details/38708967

### 单一职责原则

一个类只负责一项职责

### 里氏替换原则

1. 子类可以实现父类的抽象方法，但不能覆盖父类的非抽象方法。
2. 子类中可以增加自己特有的方法。
3. 当子类的方法重载父类的方法时，方法的前置条件（即方法的形参）要比父类方法的输入参数更宽松。
4. 当子类的方法实现父类的抽象方法时，方法的后置条件（即方法的返回值）要比父类更严格。

**一句话总结**：尽量不要重写父类的已经实现了的方法，可以用接口等其他方法绕过

### 依赖倒置原则

### 接口隔离原则

### 迪米特法则

### 开闭原则

## Java 设计模式(23)

### 工厂模式

http://blog.csdn.net/zhangerqing/article/details/8194653

#### 静态工程模式

```java
public class SendFactory {
	
	public static Sender produceMail(){
		return new MailSender();
	}
	
	public static Sender produceSms(){
		return new SmsSender();
	}
}
```

#### 抽象工程模式

添加业务也就添加一个实现 Provider 的实现类就OK
```java
public interface Provider {
	public Sender produce();
}

public class SendSmsFactory implements Provider{

	@Override
	public Sender produce() {
		return new SmsSender();
	}
}


public class SendMailFactory implements Provider {
	
	@Override
	public Sender produce(){
		return new MailSender();
	}
}
```

### 单例模式

保证在一个JVM中，该对象只有一个实例存在
```java
public class SingletonTest {

	private static SingletonTest instance = null;

	private SingletonTest() {
	}

	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new SingletonTest();
		}
	}

	public static SingletonTest getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}
}
```

### 建造者模式

工厂类模式提供的是创建单个类的模式，而建造者模式则是将各种产品集中起来进行管理，用来创建复合对象

```java
public class Builder {
	
	private List<Sender> list = new ArrayList<Sender>();
	
	public void produceMailSender(int count){
		for(int i=0; i<count; i++){
			list.add(new MailSender());
		}
	}
	
	public void produceSmsSender(int count){
		for(int i=0; i<count; i++){
			list.add(new SmsSender());
		}
	}
}
```

### 策略模式

策略模式属于对象的行为模式。其用意是针对一组算法，将每一个算法封装到具有共同接口的独立的类中，从而使得它们可以相互替换。策略模式使得算法可以在不影响到客户端的情况下发生变化。