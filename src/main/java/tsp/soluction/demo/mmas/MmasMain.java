package tsp.soluction.demo.mmas;


import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StopWatch;
import tsp.soluction.demo.util.DistanceUtil;

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
@Getter
@Setter
public class MmasMain {

    private City initCity;
    private int cityNum = 30;
    private int p = 300;//迭代次数
    private int antNum = 50;
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
    public List<ArrayList> result = new ArrayList<>();
    // 迭代最优的蚂蚁索引ID
    private Integer iterationBestIndex = -1;
    public double iterationBestLen = Double.MAX_VALUE;
    public List<Integer> iterationBestPath = new LinkedList<>();
    private long costTime;
    private long iterationTime = 0;

    public static void main(String[] args) {
        MmasMain main = new MmasMain();
        main.run();
    }

    /**
     * 迭代结束
     */
    public void run() {

        DistanceUtil.initDistance(cityNum);
        initDistance();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < p; i++) {//一次迭代即更新了一次解空间
            System.out.println("第" + i + "次迭代：");
            initAnts();
            movetoNextCity();
            findBestRoad(i);
            updatePheromone();
        }
        stopWatch.stop();
        costTime = stopWatch.getTotalTimeMillis();
        System.out.println(costTime);
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
    private void findBestRoad(Integer iterationTime) {
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
        antFootprint.add(Double.valueOf(String.format("%.2f",bestLength)));
        // 用于记录所有的蚂蚁的行走次数
        result.add(antFootprint);
        System.out.println("当前迭代最优解长度是"+ iterationBestLen);
        System.out.println("当前最优解长度是"+ bestLength +",路径为是：" + bestTourStr);
    }

    //按更新方程修改轨迹长度
    private void updatePheromone() {
        initCity.updatePheromone(ants[iterationBestIndex]);
    }
}
