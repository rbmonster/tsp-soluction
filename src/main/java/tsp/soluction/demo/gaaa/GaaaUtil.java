/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsp.soluction.demo.gaaa;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用于处理交叉操作
 * 染色体映射问题
 *
 * @author sanwu
 */
public class GaaaUtil {

    public static List<Chromosome> findBestNIndex(Double[] survivalProb, Chromosome[] chromosomes, int topNum) {
        Arrays.stream(chromosomes).collect(Collectors.toSet());
        List<Double> survivalProbList = Arrays.asList(survivalProb);
        Collections.sort(survivalProbList);
        List<Double> superColonyProb = new ArrayList<>();
        for (int i = 0, j = survivalProbList.size()-1; i < topNum; i++,j--) {
            superColonyProb.add(survivalProbList.get(j));
        }

        List<Chromosome> superColony = new ArrayList<>();
        for (int i = 0; i < chromosomes.length; i++) {
           if( superColonyProb.contains(chromosomes[i].getSurvivalProb()) ){
               superColony.add(chromosomes[i]);
           }
        }
        Collections.sort(superColony, new Comparator<Chromosome>() {
            @Override
            public int compare(Chromosome o1, Chromosome o2) {
                return Double.valueOf(o1.getRoadLength()).compareTo(o2.getRoadLength());
            }
        });
        return superColony.subList(0, topNum);
    }

    /**
     * 部分匹配法  交配
     *
//     * @param papa
//     * @param mama
     * @param cutPointLow
     * @param cutPointHigh
     */
    public static void crossMapping( Integer[] papaRoad,  Integer[] mamaRoad, int cutPointLow, int cutPointHigh) {
//        Integer[] papaRoad = papa.getRoad();
//        Integer[] mamaRoad = mama.getRoad();
        LinkedHashMap<Integer, Integer> papaMapping = new LinkedHashMap<>();
        LinkedHashMap<Integer, Integer> mamaMapping = new LinkedHashMap<>();
        for (int i = cutPointLow; i <= cutPointHigh; i++) {
            mamaMapping.put(papaRoad[i], mamaRoad[i]);
            papaMapping.put(mamaRoad[i], papaRoad[i]);
            Integer tmp = papaRoad[i];
            papaRoad[i] = mamaRoad[i];
            mamaRoad[i] = tmp;
        }
        crossMapping(papaRoad, papaMapping, cutPointLow, cutPointHigh);
        crossMapping(mamaRoad, mamaMapping, cutPointLow, cutPointHigh);
    }

    /**
     * 交配交换后，取值映射
     * 例： 1 | 5 4 3 | 6
     * 5 | 6 1 4 | 3
     * 交换后为：  1 6 1 4 6
     * 5 5 4 3 3
     * 在交配区域外，与交配区重复的值，需要做取值映射，保证染色体的访问路径正常
     * 映射后结果为： 3 6 1 4 5
     * 6 5 4 3 1
     *
     * @param road
     * @param valueMapping
     * @param cutPointLow
     * @param cutPointHigh
     */
    public static void crossMapping(Integer[] road, LinkedHashMap<Integer, Integer> valueMapping, int cutPointLow, int cutPointHigh) {
        Set<Integer> switchValue = valueMapping.keySet();
        for (int i = 0; i < road.length; i++) {
            if (i >= cutPointLow && i <= cutPointHigh) {
                continue;
            }
            Integer current = road[i];
            boolean containFlag = false;
            while (switchValue.contains(current)) {
                if (current == valueMapping.get(current)) {
                    break;
                }
                current = valueMapping.get(current);
                containFlag = true;
            }
            if (containFlag) {
                road[i] = current;
            }
        }
    }


    /**
     * 交换元素
     *
     * @param arr
     * @param a
     * @param b
     */
    public static void swap(Double[] arr, int a, int b) {
        double temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }


}
