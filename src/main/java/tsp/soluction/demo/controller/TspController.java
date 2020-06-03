package tsp.soluction.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsp.soluction.demo.GAAA.Gaaa;
import tsp.soluction.demo.mmas.MaasMain;

/**
 * <pre>
 * @Description:
 * TODO
 * </pre>
 *
 * @version v1.0
 * @ClassName: TspController
 * @Author: sanwu
 * @Date: 2020/5/16 19:59
 */
@RestController
@RequestMapping("/tsp")
public class TspController {

    @PostMapping("/ga")
    public Object getGa(){
//        Gaaa gaaa = new Gaaa();
//        gaaa.run();
//        ACO aco = new ACO();
//        aco.iterator();
        MaasMain maasMain = new MaasMain();
        maasMain.run();
        return maasMain.getResult();
    }

}
