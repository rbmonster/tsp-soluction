/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsp.soluction.demo.GAAA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 遗传算法
 *
 * @author weangdan
 */
public class Gaaa {

    //确定种群规模、迭代次数、个体选择方式、交叉概率、变异概率等
    private int chromosomeSize = 100;//种群规模
    private int p = 100;//迭代次数
    private double p_bianyi = 0.05;//变异概率
    private double p_jiaopei = 0.8;//交配概率
    private int cityNum = 30;//城市数量
    private DistanceUtil weight_distance;
    private Chromosome[] chromosome;
    private Chromosome[] tempEntity;
    private double all_ability;
    private MatchTable matchTable;
    private Chromosome bestEntity;
    private double shortestRoad;

    private ArrayList<ArrayList> result = new ArrayList<ArrayList>();

    public ArrayList getResult(){
        return result;
    }

    public Gaaa() {
    }

    public Gaaa(int chromosomeSize, int cityNum) {
        this.chromosomeSize = chromosomeSize;
        this.cityNum = cityNum;
    }

    void run(Integer[] initRoad){
        Init_Distance();
        Init_Chromosome(initRoad);
        boolean initBest = true;
        for (int i = 0; i < p; i++) {
            System.out.println("第" + i + "次迭代：");
            Cal_AdaptabilityAndLucky(i);
            ChooseBestSolution(initBest);
            initBest = false;
            chooseSample();
            Mating();
            Variating();
        }
        Cal_AdaptabilityAndLucky(p);
        ChooseBestSolution(false);
        System.out.println("最好的路径：" + bestEntity.printRoad());
        System.out.println("最低消耗：" + shortestRoad);
    }

    public void run() {
        Init_Distance();
        Init_Chromosome();
        boolean initBest = true;
        for (int i = 0; i < p; i++) {
            System.out.println("第" + i + "次迭代：");
            Cal_AdaptabilityAndLucky(i);
            ChooseBestSolution(initBest);
            initBest = false;
            chooseSample();
            Mating();
            Variating();
        }
        Cal_AdaptabilityAndLucky(p);
        ChooseBestSolution(false);
        System.out.println("最好的路径：" + bestEntity.printRoad());
        System.out.println("最低消耗：" + shortestRoad);
    }


    //初始化城市（随机产生城市坐标）
    private void Init_Distance() {
        weight_distance = new DistanceUtil(cityNum, true);
    }

    //初始化种群（随机产生100种的路径），暂不检测路径间的重复问题,因为重复的可能太小,
    private void Init_Chromosome(Integer[] initRoad) {
        chromosome = new Chromosome[chromosomeSize];
        tempEntity = new Chromosome[chromosomeSize];
        for (int i = 0; i < chromosomeSize; i++) {
            chromosome[i] = new Chromosome(cityNum, initRoad);
            System.out.println("初始种群" + i + ":" + chromosome[i].printRoad());
        }
    }

    private void Init_Chromosome() {
        chromosome = new Chromosome[chromosomeSize];
        tempEntity = new Chromosome[chromosomeSize];
        for (int i = 0; i < chromosomeSize; i++) {
            chromosome[i] = new Chromosome(cityNum, "");
            System.out.println("初始种群" + i + ":" + chromosome[i].printRoad());
        }
    }

    /**
     * 当P<迭代次数时
     */
    //计算染色体适应度值(每个染色体的路径总和）和幸存程度
    private void Cal_AdaptabilityAndLucky(int time) {
        all_ability = 0.0;
        double all_lucky = 0.0;
        for (int i = 0; i < chromosomeSize; i++) {
            double tmp = chromosome[i].cal_Adaptability();
            ArrayList ablity = new ArrayList();
            ablity.add(time);
            ablity.add(tmp);
            result.add(ablity);
            all_ability += tmp;
        }
        for (int i = 0; i < chromosomeSize; i++) {//幸存程度，路径越短幸存程度越高，注意归一化,为轮盘赌做准备
            all_lucky += chromosome[i].cal_preLucky(all_ability);
        }
        for (int i = 0; i < chromosomeSize; i++) {//幸存程度，路径越短幸存程度越高，注意归一化,为轮盘赌做准备
            chromosome[i].cal_Lucky(all_lucky);
        }
    }

    //按某个选择概率选择样本,使用轮盘赌选择法，根据幸存程度选择,本质是重构解空间，解空间样本可重复
    private void chooseSample() {
        double p = 0.0;
        double all_prelucky = 0.0;
        for (int i = 0; i < chromosomeSize; i++) {
            p = Math.random();//产生0到1之间的随机数
            all_prelucky = 0.0;
            tempEntity[i] = chromosome[chromosomeSize - 1];//提高精确度
            for (int j = 0; j < chromosomeSize; j++) {
                all_prelucky += chromosome[j].getP_lucky();
                if (p <= all_prelucky) {
                    tempEntity[i] = chromosome[j];
                    break;
                }
            }
        }
        //更新解空间
        for (int i = 0; i < chromosomeSize; i++) {
            chromosome[i] = null;
            chromosome[i] = tempEntity[i];
        }

    }

    //个体交叉,采用部分匹配法PMX
    private void Mating() {
        double mating[] = new double[chromosomeSize];//染色体的交配概率
        boolean matingFlag[] = new boolean[100];//染色体的可交配情况
        boolean findMating1 = false;
        Random random = new Random();
        matchTable = new MatchTable(cityNum);
        int mating1 = 0;
        int mating2 = -1;//指示当前交配的两个对象
        int position1, position2;//指示交换位置
        int matingnum = 0;
        //随机产生交配概率,确定可交配的染色体
        for (int i = 0; i < chromosomeSize; i++) {
            mating[i] = Math.random();
            if (mating[i] < p_jiaopei) {
                matingFlag[i] = true;
                matingnum++;
            } else {
                matingFlag[i] = false;
            }
        }
        matingnum = matingnum / 2 * 2;//参与交配的染色体数应该是偶数
        for (int i = 0; i < matingnum / 2; i++) {
            findMating1 = false;
            position1 = random.nextInt(cityNum);
            position2 = random.nextInt(cityNum);
            if (position1 <= position2) {

            } else {
                int t = position1;
                position1 = position2;
                position2 = t;
            }
            //寻找两个可交配的染色体
            for (mating2++; mating2 < chromosomeSize; mating2++) {
//                System.out.println("开始寻找两个可交配的染色体");
                if (matingFlag[mating2]) {
                    if (findMating1) {
                        break;//已经找到mating1和mating2
                    } else {
                        mating1 = mating2;
                        findMating1 = true;
                    }
                }
            }
            //这两个染色体进行交配（部分匹配法）
            //先构建匹配表
            matchTable.setTable(chromosome[mating1], chromosome[mating2], position1, position2);
            //进行交叉操作
            Chromosome tempChromosome1 = new Chromosome(cityNum);//子代1
            Chromosome tempChromosome2 = new Chromosome(cityNum);//子代2
            //首先插入交叉部分的值
            if (!chromosome[mating1].checkdifference(chromosome[mating2])) {
                tempChromosome1 = chromosome[mating1];
                tempChromosome2 = chromosome[mating2];
            } else {
                tempChromosome1.setRoad(chromosome[mating2], position1, position2);
                tempChromosome2.setRoad(chromosome[mating1], position1, position2);
                tempChromosome1.modifyRoad(chromosome[mating1], position1, position2, matchTable, true);
                tempChromosome2.modifyRoad(chromosome[mating2], position1, position2, matchTable, false);
            }

//            System.out.println("结束插入首尾值");
            chromosome[mating1] = tempChromosome1;
            chromosome[mating2] = tempChromosome2;
        }

    }

    //个体变异,采用简单的交换变异
    private void Variating() {
//        System.out.println("进入变异");
        double rating[] = new double[chromosomeSize];//染色体的变异概率
        boolean ratingFlag[] = new boolean[chromosomeSize];//染色体的可变异情况
        Random random = new Random();
        int position1, position2;//指示交换位置
        //随机产生变异概率,确定可变异的染色体
        for (int i = 0; i < chromosomeSize; i++) {
            rating[i] = Math.random();
            if (rating[i] < p_bianyi) {
                ratingFlag[i] = true;
            } else {
                ratingFlag[i] = false;
            }
        }
        //开始变异
        for (int i = 0; i < chromosomeSize; i++) {
            if (ratingFlag[i]) {
                position1 = 0;
                position2 = 0;
                while (position1 == position2) {
                    position1 = random.nextInt(cityNum);
                    position2 = random.nextInt(cityNum);
                }
                chromosome[i].exchange(position1, position2);
            }
        }
    }

    /**
     * 迭代结束
     */
    private void ChooseBestSolution(Boolean initBest) {
//        System.out.println("进入路径选择");
        Double roadLength = Double.MAX_VALUE;
        int bestRoad = 0;
        for (int i = 0; i < chromosomeSize; i++) {
            if (roadLength > chromosome[i].getAdaptability()) {
                roadLength = chromosome[i].getAdaptability();
                bestRoad = i;
            }
        }
        System.out.println("该次迭代最好的路径：" + chromosome[bestRoad].printRoad());
        System.out.println("该次迭代最低消耗：" + roadLength);
        if (initBest) {
            shortestRoad = roadLength;
            bestEntity = chromosome[bestRoad];
        } else if (shortestRoad > roadLength) {
            shortestRoad = roadLength;
            bestEntity = chromosome[bestRoad];
        }
    }
}
