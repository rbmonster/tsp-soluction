package tsp.soluction.demo.gammas;


import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StopWatch;
import tsp.soluction.demo.gaaa.Chromosome;
import tsp.soluction.demo.gaaa.DistanceUtil;
import tsp.soluction.demo.gaaa.GaaaUtil;

import java.util.*;
import java.util.stream.Collectors;

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
public class Gammas {

    private GaCity initCity;
    private int cityNum = 30;
    private int p = 300;//迭代次数
    private int antNum = 50;
    private GaAnt[] gaAnts;

    //变异概率
    private double mutationProb = 0.10;
    private double mutationNum;
    //交配概率
    private double matingProb = 0.80;
    private int matingNum;

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
    private double[] survivalProb;

    private List<GaAnt> newAnts = new ArrayList<>();

    public static void main(String[] args) {
        Gammas main = new Gammas();
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
            calculateProb();
            mating();
            mutation();
            copyGeneration();
            findBestRoad(i);
            updatePheromone();
        }
    }

    private void calculateProb(){
        for (int i = 0; i < antNum; i++) {
            survivalProb[i] = 1/gaAnts[i].getRoadLength();
        }
        double sumProb = Arrays.stream(survivalProb).sum();
        for (int i = 0; i < antNum; i++) {
            survivalProb[i] = survivalProb[i]/sumProb;
        }
    }

    //初始化城市信息，假设为非对称TSP问题
    private void initDistance() {
        initCity = new GaCity(cityNum, false ,alpha, beta, rho, initPheromone);
        // 求出交配的数量
        matingNum = (int) Math.round(antNum * matingProb);
        // 取整交配数量
        if (matingNum % 2 != 0) {
            matingNum--;
        }
        mutationNum = (int) Math.round(antNum*mutationProb);
        survivalProb = new double[antNum];
    }

    /**
     * P<最大迭代次数
     */
    private void initAnts() { //每次循环的每只蚂蚁都是新蚂蚁，没有残留信息
        gaAnts = null;
        gaAnts = new GaAnt[antNum];
        for (int i = 0; i < antNum; i++) {
            gaAnts[i] = new GaAnt(cityNum);
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
            gaAnts[i].chooseNextCity();
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

            double currentLen = gaAnts[i].getRoadLength();
            // 更新最优解的结果
            if (bestLength > currentLen) {
                bestLength = currentLen;
                bestTourStr = gaAnts[i].getRoadStr();
                bestPath = gaAnts[i].getPath();
            }
            // 记录迭代最优解
            if (iterationBestLen > currentLen){
                iterationBestIndex = i;
                iterationBestLen = currentLen;
                iterationBestPath = gaAnts[i].getPath();
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
        initCity.updatePheromone(gaAnts[iterationBestIndex]);
    }

    /**
     * 染色体交配
     */
    private void mating() {
        for (int i = 0; i < matingNum; i+=2 ) {
            // 取两个不同的父 染色体
            int papaIndex = rouletteSelectionMethod();
            int mamaIndex = rouletteSelectionMethod();
            while (mamaIndex == papaIndex) {
                mamaIndex = rouletteSelectionMethod();
            }
            GaAnt child1 = copyNewSingleAnt(gaAnts[papaIndex]);
            GaAnt child2 = copyNewSingleAnt(gaAnts[mamaIndex]);
            // 取两个不同的切点
            Random random = new Random();
//            int cutPointLow = 0;
            int cutPointLow =  random.nextInt(cityNum - 1);
            int cutPointHigh = random.nextInt(cityNum - 1);
            while (cutPointHigh == cutPointLow) {
                cutPointHigh = random.nextInt(cityNum - 1);
            }
            if (cutPointHigh<cutPointLow){
                int tmp = cutPointHigh;
                cutPointHigh = cutPointLow;
                cutPointLow =tmp;
            }

            Integer[] road1 = new Integer[cityNum];
            child1.getPath().toArray(road1);

            Integer[] road2 = new Integer[cityNum];
            child2.getPath().toArray(road2);
            GaaaUtil.crossMapping(road1, road2, cutPointLow, cutPointHigh);
//            GaaaUtil.cross(road1, road2, cutPointLow, cutPointHigh);
            child1.setPath(Arrays.asList(road1));
            child2.setPath(Arrays.asList(road2));
            newAnts.add(child1);
            newAnts.add(child2);
        }
    }

    /**
     * 常见的变异操作方法有替换变异、交换变异、插入变异、简单倒位变异、倒位变异、争夺变异等。
     * 本方法选择交换变异（Exchange Mutation， EM）
     */
    private void mutation() {
        Random random = new Random();
        int currentSize = newAnts.size();
        for (int i = 0; i < mutationNum; i++) {
            int chromosomeIndex1 = random.nextInt(currentSize);
            GaAnt mutationSample1 = newAnts.get(chromosomeIndex1);
            int tryNum = 0;

            while (tryNum <=10) {
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
                List<Integer> road = mutationSample1.getPath();
                List<Integer> subList = new ArrayList<>(road.subList(mutationIndex1, mutationIndex2));
                double oriLen = DistanceUtil.getDistance(subList);
                Collections.reverse(subList);
                double newLen = DistanceUtil.getDistance(subList);
                if (oriLen > newLen) {
                    List<Integer> perceOfList = road.subList(mutationIndex1, mutationIndex2);
                    Collections.reverse(perceOfList);
                    mutationSample1.setPath(road);
                }
            }
        }
    }

    /**
     * 获取优秀的染色体
     * @return
     */
    private List<GaAnt>  copyBestGeneration() {
        int superNum = antNum - matingNum;
        List<GaAnt> gaAntList = Arrays.asList(gaAnts);
        Collections.sort(gaAntList, (o1, o2) -> Double.valueOf(o1.getRoadLength()).compareTo(o2.getRoadLength()));
        return gaAntList.subList(0, superNum);
    }

    /**
     * 复制染色体新生代
     */
    private void copyGeneration() {
        List<GaAnt> superList = copyBestGeneration();

        newAnts.addAll(superList);
        if (newAnts.size() != antNum) {
            throw new RuntimeException("数量error ~~~~~~~~~");
        }
        for (int i = 0; i < antNum; i++) {
            gaAnts[i] = newAnts.get(i);
        }
        newAnts.clear();
    }


    /**
     * 根据旧的染色体 创建新的染色体
     *
     * @param old
     */
    private GaAnt copyNewSingleAnt(GaAnt old) {
        GaAnt ant = new GaAnt(cityNum);
        ant.setPath(old.getPath());
        return ant;
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
        for (int i = 0; i < antNum; i++) {
            sum += survivalProb[i];
            if (sum >= tmp) {
                return i;
            }
        }
        return returnIndex == -1 ? rouletteSelectionMethod() : returnIndex;
    }
}
