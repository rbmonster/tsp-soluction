package tsp.soluction.demo.mmas;
import tsp.soluction.demo.gaaa.DistanceUtil;

import java.util.*;

/**
 * <pre>
 * @Description:
 * TODO
 * </pre>
 *
 * @version v1.0
 * @ClassName: Ant
 * @Author: sanwu
 * @Date: 2020/5/17 13:20
 */
public class Ant {
    //禁忌表
    private List<Integer> tabu;
    //允许访问的城市
    private List<Integer> allowed;
    private int currentCity;
    private int cityNum;
    private double roadLength;
    private Random random;
    private List<Integer> path = new LinkedList<>();

    Ant(int cityNum) {
        this.cityNum = cityNum;
        roadLength = 0.0;
        tabu = new ArrayList<Integer>();
        allowed = new ArrayList<Integer>();
        random = new Random();
        for (int i = 0; i < cityNum; i++) {
            allowed.add(i);
        }
        initCurrentCity();
    }

    /**
     * 初始化蚁群
     * 1. 随机初始城市
     * 2. 设置可访问城市及禁忌表
     * 3. 路径添加初始城市
     */
    private void initCurrentCity() {
        currentCity = random.nextInt(cityNum);
        tabu.add(allowed.get(currentCity));
        allowed.removeAll(tabu);
        path.add(currentCity);
    }

    /**
     * 选择下个城市
     * 轮盘赌选择
     * 公式: 求当前城市到允许城市的概率
     *       (信息素)^alpha*(1/城市距离)^beta 除以
     *       [sum](信息素)^alpha*(1/城市距离)^beta
     *  本方法 使用 sum * (0~1)随机数
     */
    public void chooseNextCity() {
        while (allowed.size() > 0) {
            double[] prob = Arrays.copyOf(City.getProb(currentCity), cityNum);
            for (int i = 0; i < tabu.size(); i++) {
                prob[tabu.get(i)] = 0;
            }
            double sum = Arrays.stream(prob).sum();
            int tempCity = -1;
            while (true){
                double roundPoint =  random.nextDouble()* sum;
                for (int i = 0; i < prob.length; i++) {
                    if (prob[i] == 0) {
                        continue;
                    }
                    roundPoint = roundPoint - prob[i];
                    if (roundPoint < 0) {
                        tempCity = i;
                        break;
                    }
                }
                if (tempCity == -1){
                    continue;
                }
                if (!(tempCity == currentCity) && !tabu.contains(tempCity)) {
                    break;
                }
            }
            //轮盘赌选择
            currentCity = tempCity;
            tabu.add(currentCity);
            allowed.removeAll(tabu);
            path.add(currentCity);
        }
        roadLength = 0;
        roadLength = DistanceUtil.getDistance(tabu.toArray(new Integer[cityNum]));
//        for (int i = 1; i < tabu.size(); i++) {
//            roadLength += City.getDistance(tabu.get(i-1), tabu.get(i));
//        }
//        roadLength += City.getDistance(currentCity, tabu.get(0));
    }

    public double getRoadLength() {
        return roadLength;
    }

    public String getRoadStr() {
        String p = "";
        for (int i = 0; i < cityNum; i++) {
            p += tabu.get(i) + ";";
        }
        return p;
    }

    public List<Integer> getPath() {
        return path;
    }

    public void setPath(List<Integer> path) {
        this.path = path;
    }
//    //禁忌表
//    private List<Integer> tabu;
//    //允许访问的城市
//    private List<Integer> allowed;
//    private int currentCity;
//    private int cityNum;
//    private double roadLength;
//    private Random random;
//    private List<Integer> path = new LinkedList<>();
//
//    Ant(int cityNum) {
//        this.cityNum = cityNum;
//        roadLength = 0.0;
//        tabu = new ArrayList<Integer>();
//        allowed = new ArrayList<Integer>();
//        random = new Random();
//        for (int i = 0; i < cityNum; i++) {
//            allowed.add(i);
//        }
//        initCurrentCity();
//    }
//
//    /**
//     * 初始化蚁群
//     * 1. 随机初始城市
//     * 2. 设置可访问城市及禁忌表
//     * 3. 路径添加初始城市
//     */
//    private void initCurrentCity() {
//        currentCity = random.nextInt(cityNum);
//        tabu.add(allowed.get(currentCity));
//        allowed.removeAll(tabu);
//        path.add(currentCity);
//    }
//
//    //轮盘赌选择,对于第一只蚂蚁，每个方向上的可能性是一样的，先归一化信息素矩阵
//    public void chooseNextCity() {
//        while (allowed.size() > 0) {
//            double[] prob = Arrays.copyOf(City.getProb(currentCity), cityNum);
//            for (int i = 0; i < tabu.size(); i++) {
//                prob[tabu.get(i)] = 0;
//            }
//            double sum = Arrays.stream(prob).sum();
//            int tempCity = -1;
//            while (true){
//                double roundPoint =  random.nextDouble()* sum;
//                for (int i = 0; i < prob.length; i++) {
//                    if (prob[i] == 0) {
//                        continue;
//                    }
//                    roundPoint = roundPoint - prob[i];
//                    if (roundPoint < 0) {
//                        tempCity = i;
//                        break;
//                    }
//                }
//                if (tempCity == -1){
//                    continue;
//                }
//                if (!(tempCity == currentCity) && !tabu.contains(tempCity)) {
//                    break;
//                }
//            }
//            //轮盘赌选择
//            currentCity = tempCity;
//            tabu.add(currentCity);
//            allowed.removeAll(tabu);
//            path.add(currentCity);
//        }
////        roadLength += City.getDistance(currentCity, tabu.get(0));
//        roadLength = 0;
//        for (int i = 1; i < tabu.size(); i++) {
//            roadLength += City.getDistance(tabu.get(i-1), tabu.get(i));
//        }
//        roadLength += City.getDistance(currentCity, tabu.get(0));
//    }
//
//    public double getRoadLength() {
//        return roadLength;
//    }
//
//    public String getRoad() {
//        String p = "";
//        for (int i = 0; i < cityNum; i++) {
//            p += tabu.get(i) + ";";
//        }
//        return p;
//    }
//
//    public List<Integer> getPath() {
//        return path;
//    }
//
//    public void setPath(List<Integer> path) {
//        this.path = path;
//    }
}
