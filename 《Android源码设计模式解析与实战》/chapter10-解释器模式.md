## chapter10-解释器模式

给定一种语言，定义他的文法的一种表示，并定义一个解释器，该解释器使用该表示来解释语言中句子。

> 扩展性：修改语法规则只需要修改相应的非终结符就可以了，若扩展语法，只需要增加非终结符类就可以了。  
但是，解释器模式会引起类的膨胀，每个语法都需要产生一个非终结符表达式，语法规则比较复杂时，就可能产生大量的类文件，为维护带来非常多的麻烦。

> 具体看下面例子，不太容易解释（比较少用的模式）

## UML 

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_interpret_uml.png)

* ArithmeticExpression —— 抽象解释器类或接口：主要有一个 interpret() 方法，称为解释操作
* NumExpression —— 终结符表达式：实现与文法中的元素相关联的解释操作，通常一个解释器模式中只有一个终结符表达式，但有多个实例
* OperatorExpression —— 非终结符表达式：文法中的每条规则对应于一个非终结符表达式，非终结符表达式一般是文法中的运算符或者其他关键字
* Calculator —— 环境角色：这个角色的任务一般是用来存放文法中各个终结符所对应的具体值

## 示例

> 我们以运算符操作为例，计算 `12+1+2-3` 的值得一个过程

#### ArithmeticExpression 抽象解释器接口

```java
public interface ArithmeticExpression {
    int interpret();
}
```

#### NumExpression 终结符表达式：数字解释器

```java
public class NumExpression implements ArithmeticExpression {

    private int num;

    public NumExpression(int num) {
        this.num = num;
    }

    @Override
    public int interpret() {
        return num;
    }
}
```

#### OperatorExpression 非终结符表达式：运算符解释器

> 在这里也为抽象类，由子类具体实现

```java
public abstract class OperatorExpression implements ArithmeticExpression {

    protected ArithmeticExpression exp1, exp2;

    public OperatorExpression(ArithmeticExpression exp1, ArithmeticExpression exp2) {
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
}
```

#### 加法运算

```java
public class AddExpression extends OperatorExpression {

    public AddExpression(ArithmeticExpression exp1, ArithmeticExpression exp2) {
        super(exp1, exp2);
    }

    @Override
    public int interpret() {
        return exp1.interpret() + exp2.interpret();
    }
}
```

#### SubExpression 减法运算

```java
public class SubExpression extends OperatorExpression {

    public SubExpression(ArithmeticExpression exp1, ArithmeticExpression exp2) {
        super(exp1, exp2);
    }

    @Override
    public int interpret() {
        return exp1.interpret() - exp2.interpret();
    }
}
```

#### Calculator

```java
public class Calculator {

    private Stack<ArithmeticExpression> expStack;

    private String expression;
    private ArithmeticExpression exp1, exp2;

    public Calculator(String expression) {
        this.expression = expression;

        expStack = new Stack<ArithmeticExpression>();
    }

    /**
     * 计算结果
     *
     * @return
     */
    public int calculate() {
        expression = expression.replaceAll("\\+", " + ").replaceAll("-", " - ");
        String[] strs = expression.split(" ");
        System.out.println(Arrays.asList(strs));

        for (int i = 0; i < strs.length; i++) {
            switch (strs[i].charAt(0)) {
                case '+':
                    exp1 = expStack.pop();
                    exp2 = new NumExpression(Integer.parseInt(strs[++i]));
                    expStack.push(new AddExpression(exp1, exp2));
                    break;
                case '-':
                    exp1 = expStack.pop();
                    exp2 = new NumExpression(Integer.parseInt(strs[++i]));
                    expStack.push(new SubExpression(exp1, exp2));
                    break;
                default:
                    expStack.push(new NumExpression(Integer.parseInt(strs[i])));
                    System.out.println("num:" + strs[i]);
                    break;
            }
        }
        return expStack.pop().interpret();
    }
}
```

#### 测试

```java
public class ExpressionTest {

    public static void main(String[] args) {
        Calculator calculator = new Calculator("12+1+2-3");
        int result = calculator.calculate();
        System.out.println(result);

        // 输出：12
    }
}
```