# chapter18-代理模式

为其他对象提供一种代理以控制对这个对象的访问。

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_proxy_uml.png)

* Subject（ILawsuit） —— 抽象的主题类（接口、抽象类）。该类主要是声明真是主题与代理的共同接口方法。
* RealSubject（Person） —— 真是的主题类（被委托类、代理类）。定义代理所表示的真实对象及方法，
* ProxySubject（Lawyer） —— 代理类（委托类）。持有一个对真是主题类的引用，在所实现的接口方法中调用真是主题类中相应的接口方法执行，以此起到代理的作用。

## 示例

> 以申请诉讼为例

### 静态代理

#### ILawsuit 抽象接口

```java
public interface ILawsuit {

    /**
     * 打官司
     */
    void goToLaw();
}
```

#### Person

```java
public class Person implements ILawsuit {

    private String name;

    public Person(String name) {
        this.name = name;
    }

    @Override
    public void goToLaw() {
        System.out.println(name + "打官司");
    }
}
```

#### Lawyer 律师

```java
public class Lawyer implements ILawsuit {

    private Person person;

    public Lawyer(Person person) {
        this.person = person;
    }

    @Override
    public void goToLaw() {
        person.goToLaw();
    }
}
```

#### 测试

```java
public class ProxyTest {
    public static void main(String[] args) {
        // 静态代理
        Person person = new Person("小明");

        Lawyer lawyer = new Lawyer(person);
        lawyer.goToLaw();
    }
}
```

> 以上这个例子是 `静态代理`，下面说下 `动态代理`

### 动态代理

静态代理如上述实例那样，代理者的代码由程序员自己通过一些自动化工具生成固定呆吗再对其进行编译，也就是说在我们的代码运行前代理类的 class 编译文件就已存在了；二动态代码则与静态代理相反，通过反射机制动态的生成代理者的对象，也就是说我们在 code 阶段压根不知道代理谁，代理谁我们将会在执行阶段决定，而 java 也给我们提供了一个便捷的动态代理接口 InvocationHandler，实现该接口需要重写其调用方法 invoke。

#### 动态代理类

```java
public class DynamicProxy implements InvocationHandler {

    private Object obj;

    public DynamicProxy(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(obj, args);
        return result;
    }
}
```

#### 测试

```java
public class ProxyTest {
    public static void main(String[] args) {
        // 动态代理
        DynamicProxy proxy = new DynamicProxy(person);
        ClassLoader loader = person.getClass().getClassLoader();

        ILawsuit iLawsuit = (ILawsuit) Proxy.newProxyInstance(loader, new Class[]{ILawsuit.class}, proxy);
        iLawsuit.goToLaw();
    }
}
```