package tsp.soluction.demo.mmasbk;


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
public class MaasMainOri {

    private CityOri initCity;
    private int cityNum = 30;
    private int p = 100;//迭代次数
    private double bestLength;
    private String bestTour;
    private int antNum = 100;
    private AntOri[] ants;

    // 信息启发因子
    private double alpha = 1.0;
    // 信息期望启发因子
    private double beta = 2;
    // 信息挥发因子
    private double rho = 0.95;

    private static double numBest = 0.05; // 一次找到最优路径的概率
    // 信息素的量
    private double Q = 1000;
    private Integer bestAntIndex = -1;
    public Set<ArrayList> result = new HashSet<>();
    public List<Integer> bestPath = new LinkedList<>();

    public static void main(String[] args) {
        //
        double cityNum =100;
        double tmp = Math.exp(Math.log(numBest)/cityNum);
        // 最大最小值之间的比例
        double mmRate =(2/tmp-2)/(cityNum -2);
        MaasMainOri main = new MaasMainOri();
        main.run();
    }

    /**
     * 迭代结束
     */
    public void run() {
        init_Distance();
        init_paras();
        for (int i = 0; i < p; i++) {//一次迭代即更新了一次解空间
            System.out.println("第" + i + "次迭代：");
            init_Ants();
            movetoNextCity();
            findBestRoad(i);
            updatePheromone();
        }
    }

    //初始化城市信息，假设为非对称TSP问题
    private void init_Distance() {
        initCity = new CityOri(cityNum, false);
    }

    //参数初始化
    private void init_paras() {
        bestLength = Double.MAX_VALUE;
        bestTour = "";
    }

    /**
     * P<最大迭代次数
     */
    private void init_Ants() { //每次循环的每只蚂蚁都是新蚂蚁，没有残留信息
        ants = null;
        ants = new AntOri[antNum];
        for (int i = 0; i < antNum; i++) {
            ants[i] = new AntOri(cityNum, alpha, beta);
        }
    }

    //对每只蚂蚁按概率选择移动到下一个节点，直至遍历全部节点
    //当一只蚂蚁结束遍历时，应该更新全局信息素矩阵
    //计算每只蚂蚁的路径长度，
    private void movetoNextCity() {
        double startMill = System.currentTimeMillis();
        for (int i = 0; i < antNum; i++) {
            ants[i].chooseNextCity();
//            ants[i].updatePheromone();//信息素更新在一次迭代结束
//            ants[i].calRoad();
        }
        double endMill = System.currentTimeMillis();
        System.out.println("寻找城市耗时！" + (endMill - startMill) / 1000 +"s");
    }

    //记录当前最好解
    private void findBestRoad(int time) {
        for (int i = 0; i < antNum; i++) {
            ArrayList preResult = new ArrayList();
            preResult.add(time);
            preResult.add(ants[i].getRoadLength());
            result.add(preResult);
            if (bestLength > ants[i].getRoadLength()) {
                bestLength = ants[i].getRoadLength();
                bestTour = ants[i].getRoad();
                bestAntIndex = i;
                bestPath = ants[i].getPath();
            }
        }
        System.out.println("当前最优解是：" + bestTour);
        System.out.println("该路径下的最低消耗：" + bestLength);
    }

    //按更新方程修改轨迹长度
    private void updatePheromone() {
        initCity.updatePheromone(Q, ants[bestAntIndex].getPath());
    }
}
