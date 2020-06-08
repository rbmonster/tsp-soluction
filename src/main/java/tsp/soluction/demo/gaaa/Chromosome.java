/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsp.soluction.demo.gaaa;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 *
 * @author weangdan
 */

/**
 * 遗传算法个体，采取基于路径的编码方式
 */
@Getter
@Setter
public class Chromosome {

    private Integer cityNum;
    private Integer[] road;
    private double roadLength;
    private double adaptability = 0.0;//个体适应度
    private double survivalProb = 0.0;//幸存概率

    Chromosome(int n, Integer[] cityIndex) {
        this.cityNum = n;
        road = new Integer[cityNum];
        initCityRoad(cityIndex);
    }

    public Chromosome() {

    }

    private void initCityRoad(Integer[] initCity) {//随机解
        List<Integer> cityList = Arrays.asList(initCity);
        Collections.shuffle(cityList, new Random());
        road = (Integer[]) cityList.toArray();
    }

    public double getRoadLength(){
//        if (roadLength <= 0){
            roadLength = DistanceUtil.getDistance(road);
//        }
        return roadLength;
    }

    /**
     * 计算个体的适应程度
     * @return
     */
    public double calculateAdaptability() {
        roadLength = DistanceUtil.getDistance(road);
        adaptability = 1/roadLength;
        return adaptability;
    }

    /**
     * 计算自然选择概率
     * @param allAbility
     * @return
     */
    public double calculateSurvivalProb(double allAbility) {
        survivalProb = adaptability/allAbility;
        return survivalProb;
    }

    public String printRoad() {
        String p = "";
        for (int i = 0; i < cityNum; i++) {
            p += "  " + road[i] + ",";
        }
//        p+="幸存概率："+p_lucky;
        return p;
    }
}
