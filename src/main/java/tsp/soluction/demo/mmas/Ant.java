package tsp.soluction.demo.mmas;
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
    private double[][] Delta;
    private int currentCity;
    private int cityNum;
    private double roadLength;
    private Random random;
    private List<Integer> path = new LinkedList<>();
    private double alpha;
    private double beta;
    private double[] phePercentage = new double[cityNum];

    Ant(int cityNum, double alpha, double beta) {
        this.cityNum = cityNum;
        this.alpha = alpha;
        this.beta = beta;
        roadLength = 0.0;
        tabu = new ArrayList<Integer>();
        allowed = new ArrayList<Integer>();
        random = new Random();
        for (int i = 0; i < cityNum; i++) {
            allowed.add(i);
        }
        Delta = new double[cityNum][cityNum];
        initCurrentCity();
    }

    private void initCurrentCity() {
        currentCity = random.nextInt(cityNum);
        tabu.add(allowed.get(currentCity));
        allowed.removeAll(tabu);
        path.add(currentCity);
    }

    //轮盘赌选择,对于第一只蚂蚁，每个方向上的可能性是一样的，先归一化信息素矩阵
    public void chooseNextCity() {
        while (allowed.size() > 0) {
            double[] nextCityPheromone = Arrays.copyOf(City.getCityPheromone(currentCity), cityNum);//得到从当前城市可到达的下一城市，可根据City形式改变
            double[] nextCityDistance = Arrays.copyOf(City.getCityDistance(currentCity), cityNum);
//            System.out.println("current distance is :" + Arrays.toString(nextCityDistance));
            //去除不可到达城市
            for (int i = 0; i < tabu.size(); i++) {
                nextCityPheromone[tabu.get(i)] = 0;
                nextCityDistance[tabu.get(i)] = 0;
            }
            phePercentage = new double[cityNum];
            //轮盘赌选择
            int tempCity = -1;
            double sumParam = 0;
            for (int i = 0; i < nextCityPheromone.length; i++) {
                if (nextCityPheromone[i] == 0){
                    continue;
                }
                double tmp =  Math.pow(nextCityPheromone[i], alpha)
                        * Math.pow(1.0 / nextCityDistance[i], beta);
                sumParam += tmp;
                phePercentage[i] = tmp;
            }
            for (int i = 0; i < phePercentage.length; i++) {
                phePercentage[i] = phePercentage[i]/sumParam;
            }
            for (int i = 1; i < phePercentage.length; i++) {
                phePercentage[i] += phePercentage[i-1];
            }
            while (true) {
                tempCity = getTempCity(nextCityPheromone, nextCityDistance, tempCity, sumParam);
                if (tempCity == -1){
                    continue;
                }
                if (!(tempCity == currentCity) && !tabu.contains(tempCity)) {
                    break;
                }
            }
//            roadLength += City.getDistance(currentCity, tempCity);
            currentCity = tempCity;
            tabu.add(currentCity);
            allowed.removeAll(tabu);
            path.add(currentCity);
        }
//        roadLength += City.getDistance(currentCity, tabu.get(0));
        roadLength = 0;
        for (int i = 1; i < tabu.size(); i++) {
            roadLength += City.getDistance(tabu.get(i-1), tabu.get(i));
        }
        roadLength += City.getDistance(currentCity, tabu.get(0));
    }

    public int getTempCity(double[] nextCityPheromone, double[] nextCityDistance, int tempCity, double sumParam) {
//        double randomNum = sumParam * random.nextDouble();
        double randomNum =  random.nextDouble();
        double temp = 0.0;
        for (int i = 0; i < nextCityPheromone.length; i++) {
            if (nextCityPheromone[i] == 0) {
                continue;
            }

            if (randomNum < phePercentage[i]) {
                tempCity = i;
                break;
            }
        }
        return tempCity;
    }

    public double getRoadLength() {
        return roadLength;
    }

    public String getRoad() {
        String p = "";
        for (int i = 0; i < cityNum; i++) {
            p += tabu.get(i) + ";";
        }
        return p;
    }

    public void  getAllowed() {
        String p = "";
        for (int i = 0; i < allowed.size(); i++) {
            p += allowed.get(i) + ";";
        }
        System.out.println(p);
    }

//    public void updatePheromone(double Q,long t){
//        Q = Q/roadLength;
//        for(int i=0;i<tabu.size()-1;i++){
//            City.setPhero(tabu.get(i), tabu.get(i+1), Q,t);
//        }
//        City.setPhero(tabu.get(tabu.size()-1), tabu.get(0), Q,t);
//    }


    public List<Integer> getPath() {
        return path;
    }

    public void setPath(List<Integer> path) {
        this.path = path;
    }
}
