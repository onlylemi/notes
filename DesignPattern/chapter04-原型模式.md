# chapter4-原型模式

用原型实例指定创建对象的种类，并通过拷贝这些原型创建新的对象。

> 对象的复制，浅拷贝与深拷贝 `clone()`

* 实现 Cloneable 接口，只有实现这个接口的类才可以被拷贝，否则在运行时会抛出CloneNotSupportedException异常。
* 重写 Object 的 `clone()` 方法，作用域修改为 public
* clone 方法只会拷贝对象中的基本的数据类型，对于引用类型无法被拷贝，而是指向该引用的地址，刺猬浅拷贝。如果需要实现深拷贝，必须对引用类型另行拷贝
* 拷贝对象时不会调用类的构造函数。因为使用 object 的 clone() 方法时，直接在内存中进行二进制流的复制

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_prototype_uml.png)

## 示例

#### User

通过实现 Cloneable 接口、重写 clone() 方法实现拷贝

```java
public class User implements Cloneable {

    private int age;
    private String name;
    private Address address;

    public User(int age, String name, Address address) {
        this.age = age;
        this.name = name;
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", address=" + address +
                '}';
    }

    @Override
    public User clone() {
        User user = null;
        try {
            user = (User) super.clone();
            // 引用类型不可以拷贝，需要另行拷贝
            user.address = this.address.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return user;
    }
}
```

#### Address

Address 为 User 的一个属性地址（主要为了测试引用不可拷贝）

```java
public class Address implements Cloneable {

    private String city;

    public Address(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Address{" +
                "city='" + city + '\'' +
                '}';
    }

    @Override
    public Address clone() {
        Address address = null;
        try {
            address = (Address) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return address;
    }
}
```

#### PrototypeTest

```java
public class PrototypeTest {

    public static void main(String[] args) {
        User user = new User(20, "Jack", new Address("Beijing"));
        System.out.println(user);

        //输出：User{age=20, name='Jack', address=Address{city='Beijing'}}

        System.out.println("-----------基本类型可以拷贝----------");

        User user1 = (User) user.clone();
        user1.setName("Bob");
        System.out.println(user);
        System.out.println(user1);

        //输出：User{age=20, name='Jack', address=Address{city='Beijing'}}
        //输出：User{age=20, name='Bob', address=Address{city='Beijing'}}

        user1.setAge(10);
        System.out.println(user);
        System.out.println(user1);

        //输出：User{age=20, name='Jack', address=Address{city='Beijing'}}
        //输出：User{age=10, name='Bob', address=Address{city='Beijing'}}

        System.out.println("----------引用类型不可以拷贝（浅拷贝）-----------");

        user1.getAddress().setCity("Shanghai");
        System.out.println(user);
        System.out.println(user1);

        // 引用类型不可以拷贝，指向同一地址，修改时都会改变
        //输出：User{age=20, name='Jack', address=Address{city='Shanghai'}}
		//输出：User{age=10, name='Bob', address=Address{city='Shanghai'}}

        System.out.println("----------（深拷贝）-----------");

        // 在 User 类中 clone() 方法中，对 address 进行再次拷贝
        // 加入 user.address = this.address.clone();

        user1.getAddress().setCity("Shanghai");
        System.out.println(user);
        System.out.println(user1);

        // 进行深拷贝之后，引用类型也同时被拷贝，修改时不会引起原数据变化
        //输出：User{age=20, name='Jack', address=Address{city='Beijing'}}
		//输出：User{age=10, name='Bob', address=Address{city='Shanghai'}}
    }
}
```



