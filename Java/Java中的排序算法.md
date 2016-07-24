# Java 中的排序算法

> 可是模拟对比：[http://www.cs.usfca.edu/~galles/visualization/ComparisonSort.html](http://www.cs.usfca.edu/~galles/visualization/ComparisonSort.html)

![](http://blog.chinaunix.net/attachment/201201/18/21457204_1326898064RUxx.jpg)

## 插入排序

```java
	public static void insertSort(int[] a) {
        for (int i = 0; i < a.length; i++) {
            // 待插入元素
            int temp = a[i];
            int j = i - 1;
            while (j >= 0 && temp < a[j]) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = temp;
        }
    }
```

## 希尔排序

```java
	public static void shellSort(int[] a) {
        int d = a.length;
        while (true) {
            // 分块
            d = d / 2;
            for (int i = 0; i < d; i++) {
                // 每部分进行插入排序
                for (int j = i; j < a.length; j += d) {
                    int temp = a[j];
                    int k = j - d;
                    while (k >= 0 && temp < a[k]) {
                        a[k + d] = a[k];
                        k -= d;
                    }
                    a[k + d] = temp;
                }
            }

            if (d == 1) {
                break;
            }
        }
    }
```

## 堆排序

```java
	public static void heapSort(int[] a) {
        int i = (a.length - 1) / 2;
        while (i >= 0) {
            maxHeap(a, i, a.length);
            i--;
        }

        for (i = a.length - 1; i >= 0; i--) {
            int temp = a[0];
            a[0] = a[i];
            a[i] = temp;

            maxHeap(a, 0, i);
        }
    }

    private static void maxHeap(int[] a, int i, int size) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        int large = i;
        if (left < size && a[large] < a[left]) {
            large = left;
        }
        if (right < size && a[large] < a[right]) {
            large = right;
        }

        if (large != i) {
            int temp = a[i];
            a[i] = a[large];
            a[large] = temp;

            maxHeap(a, large, size);
        }
    }
```

## 选择排序

```java
	public static void selectSort(int[] a) {
        for (int i = 0; i < a.length; i++) {
            int min = i;
            for (int j = i; j < a.length; j++) {
                if (a[min] > a[j]) {
                    min = j;
                }
            }
            // 找到最小元素的下标，与首元素交换
            if (min != i) {
                int temp = a[i];
                a[i] = a[min];
                a[min] = temp;
            }
        }
    }
```

## 冒泡排序

```java
	public static void bubbleSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = 0; j < a.length - 1 - i; j++) {
                // 大的往下沉，小的往上浮
                if (a[j] > a[j + 1]) {
                    int temp = a[j + 1];
                    a[j + 1] = a[j];
                    a[j] = temp;
                }
            }
        }
    }
```

## 快速排序

```java
	public static void quickSort(int[] a) {
        quickSort(a, 0, a.length - 1);
    }

    private static void quickSort(int[] a, int left, int right) {
        if (left > right) {
            return;
        }

        int i = left;
        int j = right;
        int key = a[left]; // 选取第一个元素为基准
        while (i < j) {
            while (i < j && key <= a[j]) {
                j--;
            }
            a[i] = a[j];

            while (i < j && key >= a[i]) {
                i++;
            }
            a[j] = a[i];
        }

        a[i] = key;
        quickSort(a, left, i - 1);
        quickSort(a, i + 1, right);
    }
```

## 归并排序

```java
	public static void mergeSort(int[] a) {
        mergeSort(a, 0, a.length - 1);
    }

    private static void mergeSort(int[] a, int left, int right) {
        if (left >= right) {
            return;
        }

        int middle = (left + right) / 2;
        mergeSort(a, left, middle); // 左
        mergeSort(a, middle + 1, right); // 右
        merge(a, left, middle, right); //合并
    }

    private static void merge(int[] a, int left, int middle, int right) {
        int i = left;
        int j = middle + 1;

        int[] b = new int[right - left + 1];
        int k = 0;
        while (i <= middle && j <= right) {
            if (a[i] <= a[j]) {
                b[k] = a[i];
                i++;
            } else {
                b[k] = a[j];
                j++;
            }
            k++;
        }
        while (i <= middle) {
            b[k] = a[i];
            i++;
            k++;
        }
        while (j <= right) {
            b[k] = a[j];
            j++;
            k++;
        }
        System.arraycopy(b, 0, a, left, k);
    }
```