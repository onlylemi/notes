# chapter8-状态模式

当一个对象的内在状态改变时，允许改变其行为，对象看起来好像修改了它的类。

> 对于不同的状态，去执行不同的行为  

> 策略模式与状态模式的结构几乎一样，单目的、本质完全不一样  
**策略模式**：行为之间批次独立，可相互交替（解决同一功能的不停方法）  
**状态模式**：行为是平行的，不可替换（有他没我，解决不同功能）

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_state_uml.png)

* Context（LoginContext） —— 定义客户感兴趣的接口，维护一个 state 子类的实例
* State（UserState） —— 状态接口，定义一组接口，便是该状态的行为
* LoginState、LogoutState —— 具体的状态类，实现不同的状态行为

## 示例

#### UserState 状态接口

```java
public interface UserState {
    void forward();

    void comment();
}
```

#### LoginState 具体状态实现1

```java
public class LoginState implements UserState {
    @Override
    public void forward() {
        System.out.println("execute forward");
    }

    @Override
    public void comment() {
        System.out.println("execute comment");
    }
}
```

#### LogoutState 具体状态实现2

```java
public class LogoutState implements UserState {
    @Override
    public void forward() {
        System.out.println("please login, execute UserLogin()");
        // userLogin();
    }

    @Override
    public void comment() {
        System.out.println("please login, execute UserLogin()");
        // userLogin();
    }
}
```

#### LoginContext 为客户留的接口

```java
public class LoginContext {

    private UserState state;

    public void setState(UserState state) {
        this.state = state;
    }

    public void forward(){
        state.forward();
    }

    public void comment() {
        state.comment();
    }
}
```

#### StateTest 测试

```java
public class StateTest {
    public static void main(String[] args) {
        LoginContext context = new LoginContext();
        context.setState(new LogoutState());
        context.forward();

        // 输出：please login, execute UserLogin()

        context.setState(new LoginState());
        context.forward();

        // 输出：execute forward
    }
}
```

