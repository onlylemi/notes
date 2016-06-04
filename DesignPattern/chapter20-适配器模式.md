# chapter20-适配器模式

适配器模式把一个类的接口变换成客户端所期待的另一种接口，从而使原本因接口不匹配而无法在一起工作的两个类能够在一起工作。

> 将两个不兼容的类联系在一起。  
> Android 中最典型的就是 ListView了。

两种适配器：类适配器、对象适配器

* Target（FiveVolt） —— 目标角色，所期待得到的接口。
* Adaptee（Volt220） —— 现在需要适配的接口
* Adapter（VoltAdapter） —— 适配器接口，把源接口转换成目标接口

## 类适配器

### UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_adapter_class_uml.png)

### 示例

#### FiveVolt Target目标角色

```java
public interface FiveVolt {
    int getVolt5();
}
```

#### Volt220 Adaptee角色

```java
public class Volt220 {

    public int getVolt220() {
        return 220;
    }
}
```

#### VoltAdapter Adapter角色

```java
public class VoltAdapter extends Volt220 implements FiveVolt {

    @Override
    public int getVolt5() {
        return 5;
    }
}
```

#### 测试

```java
public class AdapterTest {

    public static void main(String[] args) {
        VoltAdapter adapter = new VoltAdapter();
        System.out.println("输出电压：" + adapter.getVolt5() + "v");

        // 输出：输出电压：5v
    }
}
```

## 对象适配器

与类适配器一样，对象的适配器模式把被适配的类的API转换成为目标类的API，与类的适配器模式不同的是，对象适配器模式不是使用继承关系连接到Adaptee类，而是使用代理关系连接到Adaptee类。

### UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_adapter_obj_uml.png)

### 示例

Target目标角色、Adaptee角色与上面一样，适配器方式不同

#### VoltAdapter2

```java
public class VoltAdapter2 implements FiveVolt {

    Volt220 volt220;

    public VoltAdapter2(Volt220 volt220) {
        this.volt220 = volt220;
    }

    public int getVolt220() {
        return volt220.getVolt220();
    }

    @Override
    public int getVolt5() {
        return 5;
    }
}
```

#### 测试

```java
public class AdapterTest {

    public static void main(String[] args) {
        VoltAdapter2 adapter2 = new VoltAdapter2(new Volt220());
        System.out.println("输出电压：" + adapter2.getVolt5() + "v");

        // 输出：输出电压：5v
    }
}
```

