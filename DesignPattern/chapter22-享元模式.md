# chapter22-享元模式

使用共享对象那个可有效的支持大量的细粒度的对象。

> 优点在于大幅度降低内存中对象的数量  
**Android中做Bitmap的缓存有类似使用。**

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_flyweight_uml.png)

* Flyweight（Ticket） —— 享元对象抽象类或接口
* ConcreteFlyweight（TrainTicket） —— 具体的享元对象
* FlyweightFactory（TicketFactory） —— 享元工程，负责管理享元对象池和创建享元对象

## 示例

> 以火车票的查询为例

#### Ticket 享元接口 票

```java
public interface Ticket {
    void showTicketInfo(String bunk);
}
```

#### TrainTicket 火车票

```java
public class TrainTicket implements Ticket {

    private String from; // 始发地
    private String to; // 目的地
    private String bunk; // 铺位
    public int price; // 价钱

    public TrainTicket(String to, String from) {
        this.to = to;
        this.from = from;
    }

    @Override
    public void showTicketInfo(String bunk) {
        price = new Random().nextInt(300);
        System.out.println("购买从" + from + "到" + to + "的 " + bunk + "火车票，价格 " + price);
    }
}
```

#### TicketFactory 享元工厂，map缓存

```java
public class TicketFactory {
    private static Map<String, Ticket> ticketMap = new ConcurrentHashMap<String, Ticket>();

    public static Ticket getTicket(String from, String to) {
        String key = from + "-" + to;
        Ticket ticket;
        if (!ticketMap.containsKey(key)) {
            System.out.println("创建对象==>" + key);
            ticket = new TrainTicket(from, to);
            ticketMap.put(key, ticket);
        } else {
            System.out.println("使用缓存==>" + key);
            ticket = ticketMap.get(key);
        }
        return ticket;
    }
}
```

#### 测试

```java
public class FlyweightTest {
    public static void main(String[] args) {
        Ticket ticket = TicketFactory.getTicket("北京", "上海");
        ticket.showTicketInfo("上铺");

        Ticket ticket1 = TicketFactory.getTicket("北京", "上海");
        ticket1.showTicketInfo("下铺");

        Ticket ticket2 = TicketFactory.getTicket("北京", "上海");
        ticket2.showTicketInfo("座位");

        // 输出：
        // 创建对象==>北京-上海
		// 购买从上海到北京的 上铺火车票，价格 183
		// 使用缓存==>北京-上海
		// 购买从上海到北京的 下铺火车票，价格 99
		// 使用缓存==>北京-上海
		// 购买从上海到北京的 座位火车票，价格 87
	}
}
```