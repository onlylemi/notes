# chapter11-命令模式

将请求封装成一个对象，从而让用户使用不同的请求把客户端参数化；对请求排队或者记录请求日志，可以提供命令的撤销和恢复。

> 对某一个命令进行封装，然后通过调用者去执行  
优点：扩展性好  
缺点：因为每个命令都需要一个命令类封装，类会繁多

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_command_uml.png)

* Receiver —— 接受者角色
* Command —— 命令接口或抽象类
* ConcreteCommand（OpenCommand、CloseCommand） —— 具体的命令角色
* Invoker —— 请求者角色，请求命令
* Client（CommandTest） —— 客户端角色，发送请求

## 示例

> 以对电脑进行开关机操作

#### Receiver 接收器，真正执行具体的命令逻辑方法

```java
public class Receiver {
    public void openAction() {
        System.out.println("开机");
    }

    public void closeAction() {
        System.out.println("关机");
    }
}
```

#### Command 命令接口

```java
public interface Command {
    void execute();
}
```

#### OpenCommand 开机命令

```java
public class OpenCommand implements Command {
    private Receiver receiver;

    public OpenCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.openAction();
    }
}
```

#### CloseCommand 关机命令

```java
public class CloseCommand implements Command {
    private Receiver receiver;

    public CloseCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.closeAction();
    }
}
```

#### Invoker 请求者，处理命令

```java
public class Invoker {
    private Command command;

    public Invoker(Command command) {
        this.command = command;
    }

    public void action() {
        command.execute();
    }
}
```

#### 测试

```java
public class CommandTest {

    public static void main(String[] args) {
        // 客户端通过调用者来执行命令
        Receiver receiver = new Receiver();
        Invoker invoker = new Invoker(new OpenCommand(receiver));
        invoker.action();

        // 输出：开机

        Invoker invoker1 = new Invoker(new CloseCommand(receiver));
        invoker1.action();

        // 输出：关机

        // 客户端直接执行命令
        Command command = new OpenCommand(receiver);
        command.execute();

        // 输出：关机
    }
}

```
