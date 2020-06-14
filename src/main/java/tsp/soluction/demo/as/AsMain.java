package tsp.soluction.demo.as;


import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StopWatch;

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
public class AsMain {

    private AsCity initCity;
    private int cityNum = 30;
    private int p = 300;//迭代次数
    private double bestLength;
    private long costTime;
    private String bestTourStr;
    private int antNum = 100;
    private AsAnt[] ants;

    // 信息启发因子
    private double alpha = 1.0;
    // 信息期望启发因子
    private double beta = 2;
    // 信息挥发因子
    private double rho = 0.8;
    // 信息素的量
    private double initPheromone = 1000;
    private Integer bestAntIndex = -1;
    public List<ArrayList> result = new ArrayList<>();
    public List<Integer> bestPath = new LinkedList<>();
    private long iterationTime = 0;

    public static void main(String[] args) {
        AsMain main = new AsMain();
        main.run();
    }

    /**
     * 迭代结束
     */
    public void run() {
        initDistance();
        initParas();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < p; i++) {//一次迭代即更新了一次解空间
            System.out.println("第" + i + "次迭代：");
            initAnts();
            movetoNextCity();
            findBestRoad();
            updatePheromone();
            //保存当前迭代结果
            ArrayList preResult = new ArrayList();
            preResult.add(i);
            preResult.add(Double.valueOf(String.format("%.2f", bestLength)));
            result.add(preResult);
        }
        stopWatch.stop();
        costTime = stopWatch.getTotalTimeMillis();
        System.out.println(costTime);
    }

    //初始化城市信息，假设为非对称TSP问题
    private void initDistance() {
        initCity = new AsCity(cityNum, alpha, beta, rho, initPheromone);
    }

    //参数初始化
    private void initParas() {
        bestLength = Double.MAX_VALUE;
        bestTourStr = "";
    }

    /**
     * P<最大迭代次数
     */
    private void initAnts() { //每次循环的每只蚂蚁都是新蚂蚁，没有残留信息
        ants = null;
        ants = new AsAnt[antNum];
        for (int i = 0; i < antNum; i++) {
            ants[i] = new AsAnt(cityNum);
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
        System.out.println("寻找城市耗时！" + watch.getTotalTimeMillis() / 1000 + "s");
    }

    /**
     * 记录当前最好解
     * 使用全局最优解进行 信息素更新
     */
    private void findBestRoad() {
//        bestLength = Double.MAX_VALUE;
        for (int i = 0; i < antNum; i++) {
            if (bestLength > ants[i].getRoadLength()) {
                bestLength = ants[i].getRoadLength();
                bestTourStr = ants[i].getRoad();
                bestAntIndex = i;
                bestPath = ants[i].getPath();
            }
        }
        System.out.println("当前最优解是：" + bestTourStr);
        System.out.println("该路径下的最低消耗：" + bestLength);
    }

    //按更新方程修改轨迹长度
    private void updatePheromone() {
        initCity.updatePheromone(ants);
    }
}
