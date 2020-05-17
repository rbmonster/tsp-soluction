package tsp.soluction.demo.mmas;

import java.util.List;
import java.util.Random;

/**
 * <pre>
 * @Description:
 * TODO
 * </pre>
 *
 * @version v1.0
 * @ClassName: City
 * @Author: sanwu
 * @Date: 2020/5/17 13:19
 */
public class City {
    private static double distance[][];
    private static double pheromone[][];
    private static int num;
    Random random = new Random();
    private int min = 2;
    private int max = 100;
    // 信息启发因子
    private double alpha = 1.0;
    // 信息期望启发因子
    private double beta = 5;
    // 信息挥发因子
    private double rho = 0.5;

    public City(int num, boolean symmetryFlag) {
        this.num = num;
        initDistance(symmetryFlag);
        initPheromone();
    }

    private void initDistance(boolean symmetryFlag) {
        distance = new double[num][num];
        //distance是一个对称阵，且对角元素设为无穷大；对角线元素不会被用到，如果算法正确
        for (int i = 0; i < num; i++) {
            if (symmetryFlag) {
                for (int j = i; j < num; j++) {
                    if (i == j) {
                        distance[i][j] = 0;
                    } else {
                        distance[i][j] = distance[j][i] = min + ((max - min) * random.nextDouble()); //产生2-100之间的随机浮点数
                    }
                }
            } else {
                for (int j = 0; j < num; j++) {
                    if (i == j) {
                        distance[i][j] = 0;
                    } else {
                        distance[i][j] = min + ((max - min) * random.nextDouble()); //产生2-100之间的随机浮点数
                    }
                }
            }
        }
        printlnDistance();
    }

    private void initPheromone() {
        pheromone = new double[num][num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                if (i == j) {
                    pheromone[i][j] = 0;
                    continue;
                }
                pheromone[i][j] = 100;
            }
        }
    }

    public void updatePheromone(double Q, List<Integer> bestPath){
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                pheromone[i][j] = pheromone[i][j] * rho;
            }
        }
        for (int i = 0; i < bestPath.size(); i++) {
            Integer cityIndex = bestPath.get(i);
            pheromone[cityIndex][i] = pheromone[cityIndex][i] *beta;
        }
    }

    public static void normalizePheromone() {
        double all_phero = 0.0;
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                all_phero += pheromone[i][j];
            }
        }
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                pheromone[i][j]=pheromone[i][j]/all_phero;
            }
        }

    }

    public static double getDistance(int i, int j) { //注意i,j和城市之间的对应关系
        return distance[i][j];
    }

    public static double getPheromone(int i, int j) {
        return pheromone[i][j];
    }

    public static double[] getCityPheromone(int i){
        return pheromone[i];
    }
    public static double[] getCityDistance(int i) {
        return distance[i];
    }

    public static void setPheromone(int i,int j ,double Q,long t){
        pheromone[i][j]+=Q-t/100* pheromone[i][j];
    }

    public static void printlnDistance() {
        System.out.println("distance is :");
        System.out.printf("%8s","");
        for (int i = 0; i < num; i++) {
            System.out.printf("%5s",i);
        }
        System.out.println();
        for (int i = 0; i < num; i++) {
            System.out.printf("%5s",i);
            for (int j = 0; j < num; j++) {
                System.out.printf("%5s",(int)distance[i][j]);
            }
            System.out.println();
        }
    }
}
