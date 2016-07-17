# GeneticTSP（遗传算法解决TSP问题）

采用遗传算法（[GeneticAlgorithm](https://en.wikipedia.org/wiki/Genetic_algorithm)）解决 [TSP](https://en.wikipedia.org/wiki/Travelling_salesman_problem) 旅行商问题。  

> 这是项目我是采用 `JAVA` 语言实现，仅有一个类 `GeneticAlgorithm。java` 来演示这个算法。然后我又通过 [`Processing`](https://processing.org) 进行了可视化的模拟分析。

## 可视化

![](https://raw.githubusercontent.com/onlylemi/res/master/genetic_tsp_visual.gif)

## 使用

提供了两种方式来进行操作

### 方法 1

这种方式会自动繁衍迭代，非常耗时，建议在线程中使用

```java
GeneticAlgorithm ga = GeneticAlgorithm.getInstance();
// 设置繁衍 1000 代
ga.setMaxGeneration(1000);
// 设置自动繁衍
ga.setAutoNextGeneration(true);

// 返回 1000 代后的最优结果
int[] best = ga.tsp(getDist(points));
System.out.print("best path:");
for (int i = 0; i < best.length; i++) {
    System.out.print(best[i] + " ");
}
System.out.println();
```

### 方法 2

这种方式不自动繁衍，所以需要你自己在程序中调用 `ga.nextGeneration()` 来获取下一代，如果需要查看每一代的情况，可采用这种方式。

```java
GeneticAlgorithm ga = new GeneticAlgorithm();
int[] best = ga.tsp(getDist(points));

int n = 0;
while (n++ < 100) {
    best = ga.nextGeneration();

    System.out.println("best distance:" + ga.getBestDist() + " current generation:" + ga.getCurrentGeneration() + " mutation times:" + ga.getMutationTimes());
    System.out.print("best path:");
    for (int i = 0; i < best.length; i++) {
        System.out.print(best[i] + " ");
    }
    System.out.println();
}
```

## END

如果你在使用过程中，有任何问题，欢迎与我[联系](http://onlylemi.github.io/about/)~