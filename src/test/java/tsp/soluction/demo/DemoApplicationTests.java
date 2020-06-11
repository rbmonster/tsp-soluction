package tsp.soluction.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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
        List<Double> gaResult = new ArrayList<>();
        List<Double> gammasResult = new ArrayList<>();
        for (int i = 0; i < time; i++) {
            MmasMain ga= new MmasMain();
            ga.run();
            gaResult.add(MmasMain.bestLength);
            MmasMain.bestLength = Double.MAX_VALUE;
            Thread.sleep(100);
            Gammas gammas = new Gammas();
            gammas.run();
            gammasResult.add(Gammas.bestLength);
            Gammas.bestLength = Double.MAX_VALUE;
        }
        Collections.sort(gammasResult);
        Collections.sort(gaResult);
        System.out.println("原始最大最小蚁群：");
        System.out.println(Arrays.toString(gaResult.toArray()));
        Double averageResult1 = gaResult.stream().collect(Collectors.averagingDouble(p -> p));
        System.out.println(averageResult1);
        System.out.println("改造最大最小蚁群:");
        System.out.println(Arrays.toString(gammasResult.toArray()));
        Double averageResult2 = gammasResult.stream().collect(Collectors.averagingDouble(p -> p));
        System.out.println(averageResult2);
    }

}
