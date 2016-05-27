# chapter16-访问者模式

封装一些作用域某种数据结构中个元素的操作，他可以在不改变这个数据结构的前提下定义作用于这些元素的新的操作。

> 大多数情况下，并不需要使用。  
优点：各角色职责分离；可扩展、灵活  
缺点：  
* 具体元素对访问者公布细节，违反了迪米特原则
* 具体元素变更导致修改成本大
* 违反了依赖倒置原则，为了达到“区别对待”而依赖了具体类，没有依赖抽象。

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_visitor_uml.png)

* Visitor —— 接口或抽象类，定义对每个元素的访问行为，参数为可以访问的元素，方法个数理论上来讲与元素个数是一样的。
* ConcreteVisitor（CEO、CTO） —— 具体的访问者，他需要给出对每一个元素访问时所产生的具体行为。
* Element（Staff） —— 元素接口或抽象类，定义一个接受访问的 accept 方法
* ElementA...（Engineer、Manager） —— 具体的元素类，提供接受访问方法的具体实现。
* ObjectStructure（BusinessReport） —— 定义当中所提到的对象结构，内部管理元素集合。

## 测试

> 以在公司内 CEO、CTO 考核员工所关注的东西

#### Staff 员工抽象接口

```java
public abstract class Staff {
    protected String name;
    protected int kpi;

    public Staff(String name) {
        this.name = name;
        kpi = new Random().nextInt(10);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKpi() {
        return kpi;
    }

    public void setKpi(int kpi) {
        this.kpi = kpi;
    }

    public abstract void accept(Visitor visitor);
}
```

#### Engineer 工程师

```java
public class Engineer extends Staff {

    private int codeLines;

    public Engineer(String name) {
        super(name);
        codeLines = new Random().nextInt(10 * 10000);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * 工程师一年写的代码数量
     *
     * @return
     */
    public int getCodeLines() {
        return codeLines;
    }
}
```

#### Manager 经理

```java
public class Manager extends Staff {

    private int products;

    public Manager(String name) {
        super(name);
        products = new Random().nextInt(10);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * 一年内做的产品数量
     *
     * @return
     */
    public int getProducts() {
        return products;
    }
}
```

#### Visitor

```java
public interface Visitor {
    void visit(Engineer engineer);

    void visit(Manager manager);
}
```

#### CEOVisitor

```java
public class CEOVisitor implements Visitor {

    @Override
    public void visit(Engineer engineer) {
        System.out.println("Engineer: " + engineer.getName() + ", KPI: " + engineer.getKpi());
    }

    @Override
    public void visit(Manager manager) {
        System.out.println("Manager: " + manager.getName() + ", KPI: " + manager.getKpi() + ", 新产品数量: " + manager
                .getProducts());
    }
}
```

#### CTOVisitor

```java
public class CTOVisitor implements Visitor {

    @Override
    public void visit(Engineer engineer) {
        System.out.println("Engineer: " + engineer.getName() + ", 代码数: " + engineer.getCodeLines());
    }

    @Override
    public void visit(Manager manager) {
        System.out.println("Manager: " + manager.getName() + ", 新产品数量: " + manager.getProducts());
    }
}
```

#### BusinessReport 员工业务报表

```java
public class BusinessReport {

    List<Staff> staffs = new ArrayList<Staff>();

    public BusinessReport() {
        staffs.add(new Manager("王经理"));
        staffs.add(new Engineer("工程师-小王"));
        staffs.add(new Engineer("工程师-小张"));
        staffs.add(new Engineer("工程师-小李"));
    }

    public void showReport(Visitor visitor) {
        for (Staff staff : staffs) {
            staff.accept(visitor);
        }
    }
}
```

#### 测试

```java
public class VisitorTest {

    public static void main(String[] args) {
        BusinessReport report = new BusinessReport();

        System.out.println("----------给CEO看表---------");
        report.showReport(new CEOVisitor());
        System.out.println("----------给CEO看表---------");
        report.showReport(new CTOVisitor());

        // ----------给CEO看表---------
		// Manager: 王经理, KPI: 7, 新产品数量: 1
		// Engineer: 工程师-小王, KPI: 6
		// Engineer: 工程师-小张, KPI: 2
		// Engineer: 工程师-小李, KPI: 8
		// ----------给CEO看表---------
		// Manager: 王经理, 新产品数量: 1
		// Engineer: 工程师-小王, 代码数: 21763
		// Engineer: 工程师-小张, 代码数: 60627
		// Engineer: 工程师-小李, 代码数: 27655
    }
}
```