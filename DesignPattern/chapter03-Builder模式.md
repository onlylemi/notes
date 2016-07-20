# chapter3-Builder模式

将一个复杂对象的构建与它分离，使得同样的构建过程可以创建不同的表示

## UML类图

### 角色

* Product（Car）产品类 —— 产品的抽象类
* Builder —— 抽象 Builder 类或接口，规范产品的组建，一般由子类具体实现
* ConcreateBuilder —— 具体的 Builder 类
* Director —— 统一组装过程

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_build_uml.png)

> 在现实开发中， Director 角色经常会被忽略，直接使用一个 Builder 去组装，这个 Builder 通常为链式调用，通过 setter 返回 this  
`Android 中的 Dialog 就是采用此种方式进行创建的。`

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_build_uml2.png)

## 代码

#### 具体的某个产品

```java
public class Car {

    private String color;
    private String license;
    private int seat;

    public void setColor(String color) {
        this.color = color;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        return "Car{" +
                "color=" + color +
                ", license='" + license + '\'' +
                ", seat=" + seat +
                '}';
    }
}
```

#### Builder 接口

```java
public interface Builder {

    void buildColor(String color);

    void buildLicense(String license);

    void buildSeat(int seat);

    Car create();
}
```

#### 具体的 Builder 实现

```java
public class CarBuilder implements Builder {

    private Car car = new Car();

    @Override
    public void buildColor(String color) {
        car.setColor(color);
    }

    @Override
    public void buildLicense(String license) {
        car.setLicense(license);
    }

    @Override
    public void buildSeat(int seat) {
        car.setSeat(seat);
    }

    @Override
    public Car create() {
        return car;
    }
}
```

#### Director 统一组装

```java
public class Director {

    Builder builder = null;

    public Director(Builder builder){
        this.builder = builder;
    }

    public void construct(String color, String license, int seat){
        builder.buildColor(color);
        builder.buildLicense(license);
        builder.buildSeat(seat);
    }
}
```

#### 测试

```java
public class BuilderTest {

    public static void main(String[] args) {
        Builder builder = new CarBuilder();
        Director director = new Director(builder);
        director.construct("黑", "A88888", 5);
        System.out.println(builder.create().toString());
        // 输出
        // Car{color=黑, license='A88888', seat=5}
    }
}
```

## 常用写法

```java
public class Car2 {

    private String color;
    private String license;
    private int seat;

    public void setColor(String color) {
        this.color = color;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        return "Car2{" +
                "color=" + color +
                ", license='" + license + '\'' +
                ", seat=" + seat +
                '}';
    }

    public static class Builder {

        private String color;
        private String license;
        private int seat;

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public Builder setLicense(String license) {
            this.license = license;
            return this;
        }

        public Builder setSeat(int seat) {
            this.seat = seat;
            return this;
        }

        public Car2 create() {
            final Car2 car = new Car2();
            car.setColor(color);
            car.setLicense(license);
            car.setSeat(seat);

            return car;
        }
    }
}
```

#### 测试

```java
public class BuilderTest {

    public static void main(String[] args) {
        Car2.Builder builder1 = new Car2.Builder();
        builder1.setColor("红").setLicense("A88888").setSeat(5);
        System.out.println(builder1.create());

        // 输出
        // Car2{color=红, license='A88888', seat=5}
    }

}
```