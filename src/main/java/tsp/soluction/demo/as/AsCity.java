package tsp.soluction.demo.as;

import tsp.soluction.demo.util.DistanceUtil;

import java.util.List;
import java.util.Random;

/**
 * <pre>
 * @Description:
 *
 * </pre>
 *
 * @version v1.0
 * @ClassName: City
 * @Author: sanwu
 * @Date: 2020/5/17 13:19
 */
public class AsCity {
    // 城市距离
    private static double distance[][];
    // 信息素
    private static double pheromone[][];
    // 根据信息素计算的概率函数
    private static double prob[][];
    // 蚂蚁选择函数公式的固定变量，根据距离计算(1/dij)^beta
    private static double distanceBeta[][];
    private static int cityNum;
    Random random = new Random();
    // 信息启发因子
    private double alpha;
    // 信息期望启发因子
    private double beta ;
    // 信息挥发因子
    private double rho;
    // 信息素大小
    private double initPheromone;

    public AsCity(int cityNum,  double alpha, double beta, double rho, double initPheromone) {
        this.alpha = alpha;
        this.beta = beta;
        this.rho = rho;
        this.initPheromone = initPheromone;
        this.cityNum = cityNum;
        initDistance();
        initPheromone();
    }

    /**
     * AS 蚁群算法更新信息素
     * 1.信息素rho挥发衰减
     * 2.完成一次循环后对全局信息素进行更新，更新策略为蚁周模型
     * 3.遍历所有蚂蚁的路径，增加经过的每条路径的信息素。增加的信息素的量为: (信息素)/(路径)
     * @param ants
     */
    public void updatePheromone(AsAnt[] ants) {
        // 信息素挥发
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                pheromone[i][j] = pheromone[i][j] * rho;
            }
        }

        for (int i = 0; i < ants.length; i++) {
            List<Integer> path = ants[i].getPath();
            // 信息素增量
            double pheAdd = initPheromone/ants[i].getRoadLength();
            for (int j = 1; j < path.size(); j++) {
                int start = path.get(j-1);
                int end = path.get(j);
                pheromone[start][end] += pheAdd;
            }
            // 最后城市到第一个城市
            int start = path.get(path.size()-1);
            int end = path.get(0);
            pheromone[start][end] +=pheAdd;
        }
        // 重新计算转移概率
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                prob[i][j] = Math.pow(pheromone[i][j], alpha) * distanceBeta[i][j];
            }
        }
    }

    /**
     * 初始化距离
     * 初始化概率选择函数的一部分
     */
    private void initDistance() {
        distance = DistanceUtil.initDistance(cityNum);
        distanceBeta = new double[cityNum][cityNum];
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                distanceBeta[i][j] = Math.pow( 1.0/ distance[i][j], beta);
            }
        }
        printlnDistance();
    }

    /**
     * 初始化信息素和概率函数
     */
    private void initPheromone() {
        pheromone = new double[cityNum][cityNum];
        prob = new double[cityNum][cityNum];
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j < cityNum; j++) {
                if (i == j) {
                    pheromone[i][j] = 0;
                    continue;
                }
                pheromone[i][j] = initPheromone;
                prob[i][j] = Math.pow(pheromone[i][j], alpha) * distanceBeta[i][j];
            }
        }
    }

    public static double getDistance(int i, int j) { //注意i,j和城市之间的对应关系
        return distance[i][j];
    }

    public static double[] getProb(int i) {
        return prob[i];
    }

    public static void printlnDistance() {
        System.out.println("distance is :");
        System.out.printf("%8s", "");
        for (int i = 0; i < cityNum; i++) {
            System.out.printf("%5s", i);
        }
        System.out.println();
        for (int i = 0; i < cityNum; i++) {
            System.out.printf("%5s", i);
            for (int j = 0; j < cityNum; j++) {
                System.out.printf("%5s", (int) distance[i][j]);
            }
            System.out.println();
        }
    }
}
