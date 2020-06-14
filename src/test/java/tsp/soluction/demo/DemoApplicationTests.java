package tsp.soluction.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tsp.soluction.demo.as.AsMain;
import tsp.soluction.demo.ga.Ga;
import tsp.soluction.demo.gammas.Gammas;
import tsp.soluction.demo.mmas.MmasMain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() throws InterruptedException {
        int time =30 ;
        List<Double> mmasResult = new ArrayList<>();
        List<Double> gammasResult = new ArrayList<>();
        List<Double> gaResult = new ArrayList<>();
        List<Double> asResult = new ArrayList<>();
        for (int i = 0; i < time; i++) {
            MmasMain mmasMain= new MmasMain();
            mmasMain.run();
            mmasResult.add(MmasMain.bestLength);
            MmasMain.bestLength = Double.MAX_VALUE;
            Thread.sleep(100);

            Gammas gammas = new Gammas();
            gammas.run();
            gammasResult.add(Double.valueOf(Gammas.bestLength));
            Gammas.bestLength = Double.MAX_VALUE;
            Thread.sleep(100);

            AsMain asMain = new AsMain();
            asMain.run();
            asResult.add(asMain.getBestLength());
            Thread.sleep(100);

            Ga gaaa = new Ga();
            gaaa.run();
            gaResult.add(gaaa.getBestRoadLen());
            Thread.sleep(100);
        }
        Thread.sleep(100);
//        Collections.sort(gammasResult);
//        Collections.sort(mmasResult);
//        System.out.println("原始最大最小蚁群：");
//        System.out.println(Arrays.toString(mmasResult.toArray()));
//        Double averageResult1 = mmasResult.stream().collect(Collectors.averagingDouble(p -> p));
//        System.out.println(averageResult1);
//        System.out.println("改造最大最小蚁群:");
//        System.out.println(Arrays.toString(gammasResult.toArray()));
//        Double averageResult2 = gammasResult.stream().collect(Collectors.averagingDouble(p -> p));
//        System.out.println(averageResult2);
        printResult(gaResult, "遗传算法最优解为：");
        printResult(asResult, "传统蚁群算法最优解为：");
        printResult(mmasResult, "最大最小蚁群算法最优解为：");
        printResult(gammasResult, "遗传混合蚁群算法最优解为：");

    }

    @Test
    void contextTimeLoads() throws InterruptedException {
        int time =30 ;
        List<Long> mmasResult = new ArrayList<>();
        List<Long> gammasResult = new ArrayList<>();
        List<Long> asResult = new ArrayList<>();
        List<Long> mmasTimeResult = new ArrayList<>();
        List<Long> gammassTimeResult = new ArrayList<>();
        List<Long> assTimeResult = new ArrayList<>();
        for (int i = 0; i < time; i++) {
            MmasMain mmasMain= new MmasMain();
            mmasMain.run();
            mmasResult.add(mmasMain.getCostTime());
            mmasTimeResult.add(mmasMain.getIterationTime());
            MmasMain.bestLength = Double.MAX_VALUE;
            Thread.sleep(100);

            Gammas gammas = new Gammas();
            gammas.run();
            gammasResult.add(gammas.getCostTime());
            gammassTimeResult.add(gammas.getIterationTime());
            Gammas.bestLength = Double.MAX_VALUE;
            Thread.sleep(100);

            AsMain asMain = new AsMain();
            asMain.run();
            asResult.add(asMain.getCostTime());
            assTimeResult.add(asMain.getIterationTime());
            Thread.sleep(100);
        }
        Thread.sleep(100);
//        Collections.sort(gammasResult);
//        Collections.sort(mmasResult);
//        System.out.println("原始最大最小蚁群：");
//        System.out.println(Arrays.toString(mmasResult.toArray()));
//        Double averageResult1 = mmasResult.stream().collect(Collectors.averagingDouble(p -> p));
//        System.out.println(averageResult1);
//        System.out.println("改造最大最小蚁群:");
//        System.out.println(Arrays.toString(gammasResult.toArray()));
//        Double averageResult2 = gammasResult.stream().collect(Collectors.averagingDouble(p -> p));
//        System.out.println(averageResult2);
        printTimeResult(asResult, "传统蚁群算法最优解为：");
        printTimeResult(mmasResult, "最大最小蚁群算法最优解为：");
        printTimeResult(gammasResult, "遗传混合蚁群算法最优解为：");

        printTimeResult(assTimeResult, "传统蚁群算法迭代为：");
        printTimeResult(mmasTimeResult, "最大最小蚁群算法迭代为：");
        printTimeResult(gammassTimeResult, "遗传混合蚁群算法迭代为：");

    }

    private void printResult(List<Double> result, String message){
        Collections.sort(result);
        System.out.println(message);
        System.out.println(Arrays.toString(result.toArray()));
        Double averageResult1 = result.stream().collect(Collectors.averagingDouble(p -> p));
        System.out.println(averageResult1);
    }

    private void printTimeResult(List<Long> result, String message){
        Collections.sort(result);
        System.out.println(message);
        System.out.println(Arrays.toString(result.toArray()));
        Double averageResult1 = result.stream().collect(Collectors.averagingDouble(p -> p));
        System.out.println(averageResult1);
    }

}
