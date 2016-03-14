package com.onlylemi.math;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Tsp
 *
 * @author: onlylemi
 * @time: 2016-03-14 8:45
 */
public class Tsp {
    private String cityName[] = {"北京", "上海", "天津", "重庆", "哈尔滨", "长春", "沈阳", "呼和浩特", "石家庄", "太原", "济南", "郑州", "西安",
            "兰州", "银川", "西宁", "乌鲁木齐", "合肥", "南京", "杭州", "长沙", "南昌", "武汉", "成都", "贵州", "福建", "台北", "广州", "海口", "南宁",
            "昆明", "拉萨", "香港", "澳门"};
    //private String cityEnd[]=new String[34];
    private int cityNum = cityName.length;     //城市个数
    private int popSize = 50;               //种群数量
    private int maxgens = 20000;            //迭代次数
    private double pxover = 0.8;            //交叉概率
    private double pmultation = 0.05;       //变异概率
    private long[][] distance = new long[cityNum][cityNum];
    private int range = 2000;               //用于判断何时停止的数组区间

    private class genotype {
        int city[] = new int[cityNum];      //单个基因的城市序列
        long fitness;                      //该基因的适应度
        double selectP;                        //选择概率
        double exceptp;                        //期望概率
        int isSelected;                        //是否被选择
    }

    private genotype[] citys = new genotype[popSize];

    /**
     * 构造函数，初始化种群
     */
    public Tsp() {
        for (int i = 0; i < popSize; i++) {
            citys[i] = new genotype();
            int[] num = new int[cityNum];
            for (int j = 0; j < cityNum; j++)
                num[j] = j;
            int temp = cityNum;
            for (int j = 0; j < cityNum; j++) {
                int r = (int) (Math.random() * temp);
                citys[i].city[j] = num[r];
                num[r] = num[temp - 1];
                temp--;
            }
            citys[i].fitness = 0;
            citys[i].selectP = 0;
            citys[i].exceptp = 0;
            citys[i].isSelected = 0;
        }
        initDistance();
    }

    /**
     * 计算每个种群每个基因个体的适应度，选择概率，期望概率，和是否被选择。
     */
    public void CalAll() {
        for (int i = 0; i < popSize; i++) {
            citys[i].fitness = 0;
            citys[i].selectP = 0;
            citys[i].exceptp = 0;
            citys[i].isSelected = 0;
        }
        CalFitness();
        CalSelectP();
        CalExceptP();
        CalIsSelected();
    }

    /**
     * 填充，将多选的填充到未选的个体当中
     */
    public void pad() {
        int best = 0;
        int bad = 0;
        while (true) {
            while (citys[best].isSelected <= 1 && best < popSize - 1)
                best++;
            while (citys[bad].isSelected != 0 && bad < popSize - 1)
                bad++;
            for (int i = 0; i < cityNum; i++)
                citys[bad].city[i] = citys[best].city[i];
            citys[best].isSelected--;
            citys[bad].isSelected++;
            bad++;
            if (best == popSize || bad == popSize)
                break;
        }
    }

    /**
     * 交叉主体函数
     */
    public void crossover() {
        int x;
        int y;
        int pop = (int) (popSize * pxover / 2);
        while (pop > 0) {
            x = (int) (Math.random() * popSize);
            y = (int) (Math.random() * popSize);
            executeCrossover(x, y);//x y 两个体执行交叉
            pop--;
        }
    }

    /**
     * 执行交叉函数
     *
     * @param x 个体x
     * @param y 个体y
     *          对个体x和个体y执行佳点集的交叉，从而产生下一代城市序列
     */
    private void executeCrossover(int x, int y) {
        int dimension = 0;
        for (int i = 0; i < cityNum; i++)
            if (citys[x].city[i] != citys[y].city[i]) {
                dimension++;
            }
        int diffItem = 0;
        double[] diff = new double[dimension];
        for (int i = 0; i < cityNum; i++) {
            if (citys[x].city[i] != citys[y].city[i]) {
                diff[diffItem] = citys[x].city[i];
                citys[x].city[i] = -1;
                citys[y].city[i] = -1;
                diffItem++;
            }
        }
        Arrays.sort(diff);

        double[] temp = gp(x, dimension);
        for (int k = 0; k < dimension; k++) {
            for (int j = 0; j < dimension; j++) {
                if (temp[j] == k) {
                    double item = temp[k];
                    temp[k] = temp[j];
                    temp[j] = item;
                    item = diff[k];
                    diff[k] = diff[j];
                    diff[j] = item;
                }
            }
        }
        int tempDimension = dimension;
        int tempi = 0;
        while (tempDimension > 0) {
            if (citys[x].city[tempi] == -1) {
                citys[x].city[tempi] = (int) diff[dimension - tempDimension];
                tempDimension--;
            }
            tempi++;
        }
        /*Arrays.sort(diff);
        temp = gp(y, dimension);
        for (int k = 0; k < dimension; k++)
            for (int j = 0; j < dimension; j++)
                if (temp[j] == k) {
                    double item = temp[k];
                    temp[k] = temp[j];
                    temp[j] = item;
                    item = diff[k];
                    diff[k] = diff[j];
                    diff[j] = item;
                }
        tempDimension = dimension;
        tempi = 0;
        while (tempDimension > 0) {
            if (citys[y].city[tempi] == -1) {
                citys[y].city[tempi] = (int) diff[dimension - tempDimension];
                tempDimension--;
            }
            tempi++;
        }*/
    }

    /**
     * @param individual 个体
     * @param dimension  维数
     * @return 佳点集   (用于交叉函数的交叉点）    在executeCrossover()函数中使用
     */
    private double[] gp(int individual, int dimension) {
        double[] temp = new double[dimension];
        double[] temp1 = new double[dimension];
        int p = 2 * dimension + 3;
        while (!isSushu(p))
            p++;
        for (int i = 0; i < dimension; i++) {
            temp[i] = 2 * Math.cos(2 * Math.PI * (i + 1) / p) * (individual + 1);
            temp[i] = temp[i] - (int) temp[i];
            if (temp[i] < 0)
                temp[i] = 1 + temp[i];
        }
        for (int i = 0; i < dimension; i++)
            temp1[i] = temp[i];
        Arrays.sort(temp1);
        //排序
        for (int i = 0; i < dimension; i++)
            for (int j = 0; j < dimension; j++)
                if (temp[j] == temp1[i])
                    temp[j] = i;
        return temp;
    }

    /**
     * 变异
     */
    public void mutate() {
        double random;
        int temp;
        int temp1;
        int temp2;
        for (int i = 0; i < popSize; i++) {
            random = Math.random();
            if (random <= pmultation) {
                temp1 = (int) (Math.random() * (cityNum));
                temp2 = (int) (Math.random() * (cityNum));
                temp = citys[i].city[temp1];
                citys[i].city[temp1] = citys[i].city[temp2];
                citys[i].city[temp2] = temp;
            }
        }
    }

    /**
     * 打印当前代数的所有城市序列，以及其相关的参数
     */
    public void print() {

    }

    /**
     * 初始化各城市之间的距离
     */
    private void initDistance() {
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                distance[i][j] = Math.abs(i - j);
            }
        }
    }

    /**
     * 计算所有城市序列的适应度
     */

    private void CalFitness() {
        for (int i = 0; i < popSize; i++) {
            for (int j = 0; j < cityNum - 1; j++)
                citys[i].fitness += distance[citys[i].city[j]][citys[i].city[j + 1]];
            citys[i].fitness += distance[citys[i].city[0]][citys[i].city[cityNum - 1]];
        }
    }

    /**
     * 计算选择概率
     */
    private void CalSelectP() {
        long sum = 0;
        for (int i = 0; i < popSize; i++)
            sum += citys[i].fitness;
        for (int i = 0; i < popSize; i++)
            citys[i].selectP = (double) citys[i].fitness / sum;
    }

    /**
     * 计算期望概率
     */
    private void CalExceptP() {
        for (int i = 0; i < popSize; i++)
            citys[i].exceptp = (double) citys[i].selectP * popSize;
    }

    /**
     * 计算该城市序列是否较优，较优则被选择，进入下一代
     */
    private void CalIsSelected() {
        int needSelecte = popSize;
        for (int i = 0; i < popSize; i++)
            if (citys[i].exceptp < 1) {
                citys[i].isSelected++;
                needSelecte--;
            }
        double[] temp = new double[popSize];
        for (int i = 0; i < popSize; i++) {
//          temp[i] = citys[i].exceptp - (int) citys[i].exceptp;
//          temp[i] *= 10;
            temp[i] = citys[i].exceptp * 10;
        }
        int j = 0;
        while (needSelecte != 0) {
            for (int i = 0; i < popSize; i++) {
                if ((int) temp[i] == j) {
                    citys[i].isSelected++;
                    needSelecte--;
                    if (needSelecte == 0)
                        break;
                }
            }
            j++;
        }
    }

    /**
     * @param x
     * @return 判断一个数是否是素数的函数
     */
    private boolean isSushu(int x) {
        if (x < 2) return false;
        for (int i = 2; i <= x / 2; i++)
            if (x % i == 0 && x != 2) return false;
        return true;
    }

    /**
     * @param x 数组
     * @return x数组的值是否全部相等，相等则表示x.length代的最优结果相同，则算法结束
     */
    private boolean isSame(long[] x) {
        for (int i = 0; i < x.length - 1; i++)
            if (x[i] != x[i + 1])
                return false;
        return true;
    }

    /**
     * 打印任意代最优的路径序列
     */
    private void printBestRoute() {
        CalAll();
        long temp = citys[0].fitness;
        int index = 0;
        for (int i = 1; i < popSize; i++) {
            if (citys[i].fitness < temp) {
                temp = citys[i].fitness;
                index = i;
            }
        }
        System.out.println();
        System.out.println("最佳路径的序列：");
        for (int j = 0; j < cityNum; j++) {
            String cityEnd[] = {cityName[citys[index].city[j]]};
            for (int m = 0; m < cityEnd.length; m++) {
                System.out.print(cityEnd[m] + " ");
            }
        }
        //System.out.print(citys[index].city[j] + cityName[citys[index].city[j]] + "  ");
        //System.out.print(cityName[citys[index].city[j]]);
        System.out.println();
    }

    /**
     * 算法执行
     */
    public void run() {
        long[] result = new long[range];
        //result初始化为所有的数字都不相等
        for (int i = 0; i < range; i++)
            result[i] = i;
        int index = 0;       //数组中的位置
        int num = 1;     //第num代
        while (maxgens > 0) {
            System.out.println("-----------------  第  " + num + " 代  -------------------------");
            CalAll();
            print();
            pad();
            crossover();
            mutate();
            maxgens--;
            long temp = citys[0].fitness;
            for (int i = 1; i < popSize; i++)
                if (citys[i].fitness < temp) {
                    temp = citys[i].fitness;
                }
            System.out.println("最优的解：" + temp);
            result[index] = temp;
            if (isSame(result))
                break;
            index++;
            if (index == range)
                index = 0;
            num++;
        }
        printBestRoute();
    }

    /**
     * @param a 开始时间
     * @param b 结束时间
     */
    public void CalTime(Calendar a, Calendar b) {
        long x = b.getTimeInMillis() - a.getTimeInMillis();
        long y = x / 1000;
        x = x - 1000 * y;
        System.out.println("算法执行时间：" + y + "." + x + " 秒");
    }

    /**
     * 程序入口
     */
    public static void main(String[] args) {
        Calendar a = Calendar.getInstance(); //开始时间
        Tsp tsp = new Tsp();
        tsp.run();
        Calendar b = Calendar.getInstance(); //结束时间
        tsp.CalTime(a, b);
    }
}
