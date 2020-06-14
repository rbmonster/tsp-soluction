/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsp.soluction.demo.ga;

import lombok.Getter;
import lombok.Setter;
import tsp.soluction.demo.util.DistanceUtil;

import java.util.*;

/**
 * 遗传算法
 *
 * @author weangdan
 */
@Getter
@Setter
public class Ga {

    //种群规模
    private int chromosomeSize = 100;
    //迭代次数
    private int p = 300;
    //变异概率
    private double mutationProb = 0.10;
    private double mutationNum;
    //交配概率
    private double matingProb = 0.90;
    private int matingNum;
    //城市数量
    private int cityNum = 30;
    // 所有染色体
    private Chromosome[] chromosomes;
    // 新染色体
    private List<Chromosome> newChromosomes;
    // 所有染色体的自然选择概率
    private Double[] survivalProb;
    // 所有染色体个体适应度函数汇总
    private double allAbility;
    private Integer[] cityIndex;
    private double bestRoadLen = Double.MAX_VALUE;
    private Integer[] bestRoad;

    private ArrayList<ArrayList> result = new ArrayList<ArrayList>();

    public Ga() {
    }

    public Ga(int chromosomeSize, int cityNum) {
        this.chromosomeSize = chromosomeSize;
        this.cityNum = cityNum;
    }

    /**
     * 主流程
     */
    public void run() {
        initDistance();
        initChromosome();
        for (int i = 0; i < p; i++) {
            System.out.println("第" + i + "次迭代：");
            calAdaptabilityAndLucky(i);
            chooseBestSolution();
            List<Chromosome> superList = copyBestGeneration();
            expMating();
            reverseMutation();
            copyGeneration(superList);
            // 记录本轮最优解
            ArrayList ablity = new ArrayList();
            ablity.add(i);
//            ablity.add(bestRoadLen);
            ablity.add(Double.valueOf(String.format("%.2f",bestRoadLen)));
            result.add(ablity);
        }
        System.out.println("最低消耗：" + bestRoadLen);
    }

    /**
     * 获取优秀的染色体
     *
     * @return
     */
    private List<Chromosome> copyBestGeneration() {
        int superNum = chromosomeSize - matingNum;
        List<Chromosome> superList = GaUtil.findBestNIndex(survivalProb, chromosomes, superNum);
        return superList;
    }


    /**
     * 复制染色体新生代
     */
    private void copyGeneration(List<Chromosome> superList) {
        newChromosomes.addAll(superList);
        if (newChromosomes.size() != chromosomeSize) {
            throw new RuntimeException("数量error ~~~~~~~~~");
        }
        for (int i = 0; i < chromosomeSize; i++) {
            chromosomes[i] = newChromosomes.get(i);
        }
        newChromosomes.clear();
    }


    /**
     * 初始化城市
     * 随机生成城市的长度距离
     */
    private void initDistance() {
        DistanceUtil.initDistance(cityNum);
        cityIndex = new Integer[cityNum];
        for (int i = 0; i < cityNum; i++) {
            cityIndex[i] = i;
        }
    }

    /**
     * 初始化染色体
     * 初始化一些变量
     */
    private void initChromosome() {
        chromosomes = new Chromosome[chromosomeSize];
        newChromosomes = new ArrayList<>(chromosomeSize);
        survivalProb = new Double[chromosomeSize];
        for (int i = 0; i < chromosomeSize; i++) {
            chromosomes[i] = new Chromosome(cityNum, cityIndex);
            System.out.println("初始种群" + i + ":" + chromosomes[i].printRoad());
        }
        // 求出交配的数量
        matingNum = (int) Math.round(chromosomeSize * matingProb);
        // 取整交配数量
        if (matingNum % 2 != 0) {
            matingNum--;
        }
        mutationNum = (int) Math.round(chromosomeSize * mutationProb);
    }

    /**
     * 1.计算染色体的适应度
     * 2.计算染色体的自然选择概率
     */
    //计算染色体适应度值(每个染色体的路径总和）和幸存程度
    private void calAdaptabilityAndLucky(int time) {
        Double allAbility = 0.0;
        // 计算适应度
        for (int i = 0; i < chromosomeSize; i++) {
            double ability = chromosomes[i].calculateAdaptability();
            allAbility += ability;
        }
        //幸存程度，路径越短幸存程度越高，注意归一化,为轮盘赌做准备
        for (int i = 0; i < chromosomeSize; i++) {
            survivalProb[i] = chromosomes[i].calculateSurvivalProb(allAbility);
        }
    }

    /**
     * 迭代结束
     */
    private void chooseBestSolution() {
        double iterationBestLen = Double.MAX_VALUE;
        for (int i = 0; i < chromosomeSize; i++) {
            double currentLen = chromosomes[i].getRoadLength();
            if (iterationBestLen > currentLen) {
                iterationBestLen = currentLen;
            }
            if (bestRoadLen > currentLen) {
                bestRoadLen = currentLen;
                bestRoad = Arrays.copyOf(chromosomes[i].getRoad(), cityNum);
            }
        }
        System.out.println("该次迭代最低消耗：" + iterationBestLen);
        System.out.println("全局最好的路径为：" + Arrays.toString(bestRoad));
        System.out.println("全局最好的路径为：" + bestRoadLen);
    }

    /**
     * 染色体交配
     * 部分匹配法交叉
     *
     *
     */
    private void expMating() {
        for (int i = 0; i < matingNum; i += 2) {
            // 取两个不同的父 染色体
            int papaIndex = rouletteSelectionMethod();
            int mamaIndex = rouletteSelectionMethod();
            while (mamaIndex == papaIndex) {
                mamaIndex = rouletteSelectionMethod();
            }
            Chromosome child1 = copyNewSingleChromosome(chromosomes[papaIndex]);
            Chromosome child2 = copyNewSingleChromosome(chromosomes[mamaIndex]);
            // 取两个不同的切点
            Random random = new Random();
            int cutPointLow = 0;
            int cutPointHigh = random.nextInt(cityNum - 1);
            while (cutPointHigh == cutPointLow) {
                cutPointHigh = random.nextInt(cityNum - 1);
            }
            GaUtil.crossMapping(child1.getRoad(), child2.getRoad(), cutPointLow, cutPointHigh);
            newChromosomes.add(child1);
            newChromosomes.add(child2);
        }
    }

    /**
     * 常见的变异操作方法有替换变异、交换变异、插入变异、简单倒位变异、倒位变异、争夺变异等。
     * 本方法选择交换变异（Exchange Mutation， EM）
     */
    private void mutation() {
        Random random = new Random();
        int currentSize = newChromosomes.size();
        for (int i = 0; i < mutationNum; i++) {
            // 随机找两条染色体
            int chromosomeIndex1 = random.nextInt(currentSize);
            int chromosomeIndex2 = random.nextInt(currentSize);
            while (chromosomeIndex1 == chromosomeIndex2) {
                chromosomeIndex2 = random.nextInt(currentSize);
            }
            Chromosome mutationSample1 = newChromosomes.get(chromosomeIndex1);
            Chromosome mutationSample2 = newChromosomes.get(chromosomeIndex2);
            int mutationIndex1 = random.nextInt(cityNum - 1);
            int mutationIndex2 = random.nextInt(cityNum - 1);
            while (mutationIndex1 == mutationIndex2) {
                mutationIndex2 = random.nextInt(cityNum - 1);
            }
            Integer[] road = mutationSample1.getRoad();
            int tmp = road[mutationIndex1];
            road[mutationIndex1] = road[mutationIndex2];
            road[mutationIndex2] = tmp;
        }
    }

    /**
     * 倒位变异
     */
    private void reverseMutation() {
        Random random = new Random();
        int currentSize = newChromosomes.size();
        for (int i = 0; i < mutationNum; i++) {
            // 随机找两条染色体
            int chromosomeIndex1 = random.nextInt(currentSize);
            Chromosome mutationSample1 = chromosomes[chromosomeIndex1];
            int tryNum = 0;
            while (tryNum <= 10) {
                tryNum++;
                int mutationIndex1 = random.nextInt(cityNum - 1);
                int mutationIndex2 = random.nextInt(cityNum - 1);
                while (mutationIndex1 == mutationIndex2) {
                    mutationIndex2 = random.nextInt(cityNum - 1);
                }
                if (mutationIndex1 > mutationIndex2) {
                    int tmp = mutationIndex1;
                    mutationIndex1 = mutationIndex2;
                    mutationIndex2 = tmp;
                }
                Integer[] road = mutationSample1.getRoad();
                Integer[] subRoad = Arrays.copyOfRange(road, mutationIndex1, mutationIndex2);
                double oriLen = DistanceUtil.getDistance(subRoad);
                List<Integer> oriList = new ArrayList<>(Arrays.asList(subRoad));
                Collections.reverse(oriList);
                double newLen = DistanceUtil.getDistance(oriList);
                if (oriLen > newLen) {
                    for (int j = mutationIndex1, k = mutationIndex2; j<k; j++, k--) {
                        int tmp = road[j];
                        road[j] = road[k];
                        road[k] = tmp;
                    }
                }
            }
        }
    }

    /**
     * 找到染色体路径中，相同的城市的索引，进行交换
     *
     * @param road
     * @param index
     * @param newValue
     */
    private void changeRoadIndex(Integer[] road, Integer index, Integer newValue) {
        if (road[index] == newValue) {
            return;
        }
        for (int i = 0; i < road.length; i++) {
            if (road[i] == newValue) {
                int tmp = road[index];
                road[index] = road[i];
                road[i] = tmp;
                break;
            }
        }
    }

    /**
     * 根据旧的染色体 创建新的染色体
     *
     * @param old
     */
    private Chromosome copyNewSingleChromosome(Chromosome old) {
        Chromosome chromosome = new Chromosome();
        Integer[] road = Arrays.copyOf(old.getRoad(), cityNum);
        chromosome.setRoad(road);
        chromosome.setCityNum(cityNum);
        return chromosome;
    }


    /**
     * 轮盘赌选择法 选择交配的父母下标
     *
     * @return
     */
    private int rouletteSelectionMethod() {
        Random random = new Random();
        double tmp = random.nextDouble();
        double sum = 0.0;
        int returnIndex = -1;
        for (int i = 0; i < chromosomeSize; i++) {
            sum += survivalProb[i];
            if (sum >= tmp) {
                return i;
            }
        }
        return returnIndex == -1 ? rouletteSelectionMethod() : returnIndex;
    }

    public static void main(String[] args) {
        Ga gaaa = new Ga();
        gaaa.run();
    }
}
