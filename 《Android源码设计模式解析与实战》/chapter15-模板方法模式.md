# chapter15-模板方法模式

定义一个操作中的算法的框架，而降一些步骤延迟到子类中，使得子类可以不改变一个算法的结构即可重新定义该算法的某些特定步骤。

> 流程封装。把固定的流程封装到 final 函数中，一些具体算法实现在子类中实现

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_template_uml.png)

* AbsTemplate（AbsSort） —— 抽象类，定义一套算法框架
* ConcreteImpl（SelectSort、QuickSort） —— 具体实现类

## 示例

> 以排序业务为例。先排序后打印

#### AbsSort

```java
public abstract class AbsSort {

    public abstract void sort(int[] arr);

    public final void display(int[] arr) {
        sort(arr);
        for (int i = 0; i < arr.length; i++) {
            System.out.printf("%3s", arr[i]);
        }
        System.out.println();
    }
}
```

#### SelectSort 选择排序方法

```java
public class SelectSort extends AbsSort {

    @Override
    public void sort(int[] arr) {
        int min, k;
        for (int i = 0; i < arr.length - 1; i++) {
            min = arr[i];
            k = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < min) {
                    k = j;
                    min = arr[j];
                }
            }
            int temp = arr[i];
            arr[i] = arr[k];
            arr[k] = temp;
        }
    }
}
```

#### QuickSort 快速排序方法

```java
public class QuickSort extends AbsSort {

    @Override
    public void sort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    /**
     * 快速排序法
     *
     * @param arr
     * @param low
     * @param high
     */
    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int first = low;
            int last = high;
            int key = arr[low];
            while (first < last) {
                while (first < last && key <= arr[last]) {
                    last--;
                }
                arr[first] = arr[last];

                while (first < last && key >= arr[first]) {
                    first++;
                }
                arr[last] = arr[first];
            }
            arr[first] = key;
            quickSort(arr, low, first - 1);
            quickSort(arr, first + 1, high);
        }
    }
}
```

#### 测试

```java
public class TemplateTest {

    public static void main(String[] args) {
        int[] arr = {2, 5, 6, 3, 4, 1, 9};

        AbsSort sort = new SelectSort();
        sort.display(arr);

        AbsSort sort1 = new QuickSort();
        sort1.display(arr);
    }
}
```
