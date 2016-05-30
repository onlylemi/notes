# chapter23-外观模式

要求定义一个子系统的外部与其内部的通信必须通过一个统一的对象进行。外观模式提供了一个高层次的接口，是的子系统更易于使用。

> 通过高层次接口为用户提供统一API，使得用户可以通过一个类型就基本能够操作整个系统，减少了用户使用的成本。但是当外观的API接口较多时，在一定程度上又增加了用户的成本。同时没有遵循开闭原则，当业务变更时，可能需要直接修改外观类。

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_facade_uml.png)

* Facade（MobilePhone） —— 系统对外的统一接口
* System...（Phone、Camera） —— 子系统接口

## 示例

> 以手机为例，手机有电话、照相等多个功能，单只为我们留下一个接口，就是MobilePhone手机，功能通过这个公共接口来调用

#### Phone 打电话的功能接口

```java
public interface Phone {

    /**
     * 打电话
     */
    void dail();

    /**
     * 挂断
     */
    void hangUp();
}
```

#### PhoneImp 打电话接口的实现

```java
public class PhoneImp implements Phone {
    @Override
    public void dail() {
        System.out.println("打电话");
    }

    @Override
    public void hangUp() {
        System.out.println("挂电话");
    }
}
```

#### Camera 照相机接口

```java
public interface Camera {

    /**
     * 打开相机
     */
    void open();

    /**
     * 照相
     */
    void takePicture();

    /**
     * 关闭相机
     */
    void close();
}
```

#### CameraImpl 照相机接口的具体实现

```java
public class CameraImp implements Camera {
    @Override
    public void open() {
        System.out.println("打开相机");
    }

    @Override
    public void takePicture() {
        System.out.println("照相");
    }

    @Override
    public void close() {
        System.out.println("关闭相机");
    }
}
```

#### MobilePhone 统一对外的总接口

```java
public class MobilePhone {
    private Phone phone;
    private Camera camera;

    public MobilePhone() {
        phone = new PhoneImp();
        camera = new CameraImp();
    }

    public void dail() {
        phone.dail();
    }

    public void videoChat() {
        System.out.println("--> 视频聊天中");
        camera.open();
        phone.dail();
    }

    public void hangUp() {
        phone.hangUp();
    }

    public void takePicture() {
        camera.open();
        camera.takePicture();
    }

    public void closeCamera() {
        camera.close();
    }
}
```

#### 测试

```java
public class FacadeTest {
    public static void main(String[] args) {
        MobilePhone mb = new MobilePhone();
        mb.takePicture();
        mb.closeCamera();

        mb.videoChat();

        // 输出：
        // 打开相机
		// 照相
		// 关闭相机
		// --> 视频聊天中
		// 打开相机
		// 打电话
    }
}
```