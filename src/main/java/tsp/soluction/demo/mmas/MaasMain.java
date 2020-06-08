package tsp.soluction.demo.mmas;


import org.springframework.util.StopWatch;
import tsp.soluction.demo.gaaa.DistanceUtil;

import java.util.*;

/**
 * <pre>
 * @Description:
 * TODO
 * </pre>
 *
 * @version v1.0
 * @ClassName: Maas
 * @Author: sanwu
 * @Date: 2020/5/17 13:25
 */
public class MaasMain {

    private City initCity;
    private int cityNum = 30;
    private int p = 300;//迭代次数
    private int antNum = 30;
    private Ant[] ants;

    // 信息启发因子
    private double alpha = 1.0;
    // 信息期望启发因子
    private double beta = 2;
    // 信息挥发因子
    private double rho = 0.95;
    // 信息素的量
    private double initPheromone = 1000;
    public static double bestLength = Double.MAX_VALUE;
    private String bestTourStr = "";
    public List<Integer> bestPath = new LinkedList<>();
    public Set<ArrayList> result = new HashSet<>();
    // 迭代最优的蚂蚁索引ID
    private Integer iterationBestIndex = -1;
    public double iterationBestLen = Double.MAX_VALUE;
    public List<Integer> iterationBestPath = new LinkedList<>();

    public static void main(String[] args) {
        MaasMain main = new MaasMain();
        main.run();
    }

    /**
     * 迭代结束
     */
    public void run() {

        DistanceUtil.initDistance(cityNum);
        initDistance();
        for (int i = 0; i < p; i++) {//一次迭代即更新了一次解空间
            System.out.println("第" + i + "次迭代：");
            initAnts();
            movetoNextCity();
            findBestRoad(i);
            updatePheromone();
        }
    }

    //初始化城市信息，假设为非对称TSP问题
    private void initDistance() {
        initCity = new City(cityNum, false ,alpha, beta, rho, initPheromone);
    }

    /**
     * P<最大迭代次数
     */
    private void initAnts() { //每次循环的每只蚂蚁都是新蚂蚁，没有残留信息
        ants = null;
        ants = new Ant[antNum];
        for (int i = 0; i < antNum; i++) {
            ants[i] = new Ant(cityNum);
        }
    }

    /**
     * 对每只蚂蚁按概率选择移动到下一个节点，直至遍历全部节点
     * 当一只蚂蚁结束遍历时，应该更新全局信息素矩阵
     * 计算每只蚂蚁的路径长度，
     */
    private void movetoNextCity() {
        StopWatch watch = new StopWatch();
        watch.start();
        for (int i = 0; i < antNum; i++) {
            ants[i].chooseNextCity();
        }
        watch.stop();
        System.out.println("寻找城市耗时:" + watch.getTotalTimeMillis() +"ms");
    }

    /**
     * 记录当前最好解
     * 使用全局最优解进行 信息素更新
     */
    private void findBestRoad(double iterationTime) {
        iterationBestLen = Double.MAX_VALUE;
        for (int i = 0; i < antNum; i++) {

            double currentLen = ants[i].getRoadLength();
            // 更新最优解的结果
            if (bestLength > currentLen) {
                bestLength = currentLen;
                bestTourStr = ants[i].getRoadStr();
                bestPath = ants[i].getPath();
            }
            // 记录迭代最优解
            if (iterationBestLen > currentLen){
                iterationBestIndex = i;
                iterationBestLen = currentLen;
                iterationBestPath = ants[i].getPath();
            }
        }
        ArrayList antFootprint = new ArrayList();
        antFootprint.add(iterationTime);
        antFootprint.add(bestLength);
        // 用于记录所有的蚂蚁的行走次数
        result.add(antFootprint);
        System.out.println("当前迭代最优解长度是"+ iterationBestLen);
        System.out.println("当前最优解长度是"+ bestLength +",路径为是：" + bestTourStr);
    }

    //按更新方程修改轨迹长度
    private void updatePheromone() {
        initCity.updatePheromone(ants[iterationBestIndex]);
    }

    public Set<ArrayList> getResult() {
        return result;
    }

    public void setResult(Set<ArrayList> result) {
        this.result = result;
    }
    //
//    private City initCity;
//    private int cityNum = 30;
//    private int p = 1000;//迭代次数
//    private double bestLength;
//    private String bestTour;
//    private int antNum = 100;
//    private Ant[] ants;
//
//    // 信息启发因子
//    private double alpha = 1.0;
//    // 信息期望启发因子
//    private double beta = 2;
//    // 信息挥发因子
//    private double rho = 0.8;
//
////    private static double numBest = 0.05; // 一次找到最优路径的概率
//    // 信息素的量
//    private double Q = 1000;
//    private Integer bestAntIndex = -1;
//    public Set<ArrayList> result = new HashSet<>();
//    public List<Integer> bestPath = new LinkedList<>();
//
//    public static void main(String[] args) {
//        MaasMain main = new MaasMain();
//        main.run();
//    }
//
//    /**
//     * 迭代结束
//     */
//    public void run() {
//        init_Distance();
//        init_paras();
//        for (int i = 0; i < p; i++) {//一次迭代即更新了一次解空间
//            System.out.println("第" + i + "次迭代：");
//            init_Ants();
//            movetoNextCity();
//            findBestRoad(i);
//            updatePheromone();
//        }
//    }
//
//    //初始化城市信息，假设为非对称TSP问题
//    private void init_Distance() {
//        initCity = new City(cityNum, false ,alpha, beta, rho);
//    }
//
//    //参数初始化
//    private void init_paras() {
//        bestLength = Double.MAX_VALUE;
//        bestTour = "";
//    }
//
//    /**
//     * P<最大迭代次数
//     */
//    private void init_Ants() { //每次循环的每只蚂蚁都是新蚂蚁，没有残留信息
//        ants = null;
//        ants = new Ant[antNum];
//        for (int i = 0; i < antNum; i++) {
//            ants[i] = new Ant(cityNum);
//        }
//    }
//
//    //对每只蚂蚁按概率选择移动到下一个节点，直至遍历全部节点
//    //当一只蚂蚁结束遍历时，应该更新全局信息素矩阵
//    //计算每只蚂蚁的路径长度，
//    private void movetoNextCity() {
//        double startMill = System.currentTimeMillis();
//        for (int i = 0; i < antNum; i++) {
//            ants[i].chooseNextCity();
////            ants[i].updatePheromone();//信息素更新在一次迭代结束
////            ants[i].calRoad();
//        }
//        double endMill = System.currentTimeMillis();
//        System.out.println("寻找城市耗时！" + (endMill - startMill) / 1000 +"s");
//    }
//
//    //记录当前最好解
//
//    /**
//     * TODO 设置信息素最大最小
//     * @param time
//     */
//    private void findBestRoad(int time) {
//        for (int i = 0; i < antNum; i++) {
//            ArrayList preResult = new ArrayList();
//            preResult.add(time);
//            preResult.add(ants[i].getRoadLength());
//            result.add(preResult);
//            if (bestLength > ants[i].getRoadLength()) {
//                bestLength = ants[i].getRoadLength();
//                bestTour = ants[i].getRoad();
//                bestAntIndex = i;
//                bestPath = ants[i].getPath();
//            }
//        }
//        System.out.println("当前最优解是：" + bestTour);
//        System.out.println("该路径下的最低消耗：" + bestLength);
//    }
//
//    //按更新方程修改轨迹长度
//    private void updatePheromone() {
//        initCity.updatePheromone(Q, ants[bestAntIndex]);
//    }
}
