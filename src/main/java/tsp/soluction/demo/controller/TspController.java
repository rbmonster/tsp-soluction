package tsp.soluction.demo.controller;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsp.soluction.demo.as.AsMain;
import tsp.soluction.demo.ga.Ga;
import tsp.soluction.demo.gammas.Gammas;
import tsp.soluction.demo.mmas.MmasMain;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * @Description:
 *  请求controller
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
        Ga gaaa =new Ga();
        gaaa.run();
        return removeSameNode(gaaa.getResult());
    }

    @PostMapping("/as")
    public Object getAs(){
        AsMain asMain = new AsMain();
        asMain.run();
        return removeSameNode(asMain.getResult());
    }

    @PostMapping("/mmas")
    public Object getMmas(){
        MmasMain mmasMain =new MmasMain();
        mmasMain.run();
        return removeSameNode(mmasMain.getResult());
    }

    @PostMapping("/gammas")
    public Object getGammas(){
        Gammas gammas =new Gammas();
        gammas.run();
        return removeSameNode(gammas.getResult());
    }

    /**
     * 用于把结果中重复的节点移除
     *
     * @param result
     * @return
     */
    private List<ArrayList> removeSameNode(List<ArrayList> result){
        if (CollectionUtils.isEmpty(result)){
            return new ArrayList<>();
        }
        Set<ArrayList> setResult = result.stream().collect(Collectors.toSet());
        List<ArrayList> newList = setResult.stream().collect(Collectors.toList());
        Collections.sort(newList,(o1, o2) -> Integer.valueOf((Integer) o1.get(0)).compareTo((Integer) o2.get(0)));
        Map<Double, List> countMap =new LinkedHashMap<>();
       for(ArrayList tmp: newList){
           Double key = (Double) tmp.get(1);
           if(countMap.containsKey(key)){
               List tmpList =   countMap.get(key);
               tmpList.add(tmp);
               continue;
           }
           List<ArrayList> lists = new ArrayList<>();
           lists.add(tmp);
           countMap.put(key,lists);
       }
       List<ArrayList> resultList = new ArrayList<>();
       for(Double key : countMap.keySet()){
           List<ArrayList> tmpList = countMap.get(key);
           Collections.sort(tmpList,(o1, o2) -> Integer.valueOf((Integer) o1.get(0)).compareTo((Integer) o2.get(0)));
           resultList.add(tmpList.get(0));
           if (tmpList.size()>1){
               resultList.add(tmpList.get(tmpList.size()-1));
           }
       }

        Collections.sort(resultList,(o1, o2) -> Integer.valueOf((Integer) o1.get(0)).compareTo((Integer) o2.get(0)));
        return resultList;
    }
}
